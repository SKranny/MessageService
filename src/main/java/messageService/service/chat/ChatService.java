package messageService.service.chat;

import aws.AwsClient;
import lombok.RequiredArgsConstructor;
import messageService.constants.chat.ChatType;
import messageService.dto.chat.*;
import messageService.exception.MessageException;
import messageService.mapper.ChatMapper;
import messageService.mapper.MessageMapper;
import messageService.model.chat.Chat;
import messageService.model.person.Person;
import messageService.repository.chat.ChatRepository;
import messageService.repository.message.MessageRepository;
import messageService.service.person.PersonService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import security.dto.TokenData;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;

    private final AwsClient awsClient;

    private final ChatMapper chatMapper;

    private final MessageMapper messageMapper;

    private final MessageRepository messageRepository;

    private final PersonService personService;


    private PageRequest buildPageableByPropDesc(Integer page, Integer offset, String property) {
        return Optional.ofNullable(property)
                .map(prop -> PageRequest.of(page, offset, Sort.by(prop).descending()))
                .orElseGet(() -> PageRequest.of(page, offset));
    }

    private Chat findMyChat(Long chatId, Long personId) {
        return chatRepository.findByConsumersIsContainingAndId(personService.getOrCreatePerson(personId), chatId)
                .orElseThrow(() -> new MessageException("Error! Chat not found!", HttpStatus.NOT_FOUND));
    }

    private Chat changeNameIfChatTypeIsPrivate(Chat chat, Person person) {
        if (Objects.requireNonNull(chat.getType()) == ChatType.PRIVATE && chat.getConsumers().size() == 2) {
            String chatName = chat.getConsumers().stream()
                    .filter(p -> !Objects.equals(p, person.getPersonId()))
                    .map(Person::getUserName)
                    .findFirst()
                    .orElse(person.getUserName());
            chat.setName(chatName);
            return chat;
        }
        return chat;
    }

    public Set<ChatDTO> getMyChats(TokenData data, Integer page, Integer offset) {
        Person person = personService.getOrCreatePerson(data.getId());
        Map<Long, ChatDTO> chats = chatRepository.findDistinctByConsumersContaining(person,
                        buildPageableByPropDesc(page, offset, "createDateTime"))
                .get().map(chat -> changeNameIfChatTypeIsPrivate(chat, person))
                .map(chatMapper::toDTO)
                .collect(Collectors.toMap(ChatDTO::getId, chat -> chat));
        findAndSetLastMessagesToChats(chats, data.getId());
        return new HashSet<>(chats.values());
    }

    private void findAndSetLastMessagesToChats(Map<Long, ChatDTO> chats, Long personId) {
        messageRepository.findLastMessagesByChatsId(chats.keySet(), personId)
                .forEach(ms -> chats.get(ms.getChat().getId())
                        .setLastMessage(messageMapper.toDTO(ms)));;
    }

    private void ensurePrivateChatMaxConsumers(Integer countConsumers, @NotNull ChatType type) {
        if (countConsumers != 2 && type == ChatType.PRIVATE) {
            throw new MessageException("Error! Allowed only 2 customer!");
        }
    }

    private Long getNotAdminId(Set<Long> consumersIds, Long adminId) {
        consumersIds.add(adminId);
        return consumersIds.stream().filter(id -> !Objects.equals(id, adminId)).findFirst()
                .orElseThrow(() -> new MessageException("Error! Invalid consumers data!"));
    }

    public ChatDTO getOrCreateChat(CreateChatRequest req, TokenData tokenData) {
        Person admin = personService.getOrCreatePerson(tokenData.getId());
        Set<Person> consumers = personService.getPersons(req.getConsumersIds());
        consumers.add(admin);
        ensurePrivateChatMaxConsumers(consumers.size(), req.getType());
        switch (req.getType()) {
            case PRIVATE:
                Long consumerId = getNotAdminId(consumers.stream().map(Person::getId).collect(Collectors.toSet()), admin.getId());
                return chatRepository.findPrivateChatByConsumersId(consumerId, admin.getId())
                        .map(chatMapper::toDTO).orElseGet(() -> createNewChat(req, admin, consumers));
            case COOPERATIVE:
                return createNewChat(req, admin, consumers);
            default:
                throw new MessageException("Error! Unknown chat type");
        }
    }

    @Transactional
    public ChatDTO createNewChat(CreateChatRequest req, Person admin, Set<Person> consumers) {
        Chat chat = Chat.builder()
                .type(req.getType())
                .name(req.getName())
                .photo(req.getPhoto())
                .description(req.getDescription())
                .createDateTime(ZonedDateTime.now())
                .consumers(consumers)
                .admin(admin)
                .build();
        ensurePrivateChatMaxConsumers(consumers.size(), req.getType());
        return chatMapper.toDTO(chatRepository.save(chat));
    }

    @Transactional
    public String uploadPhoto(MultipartFile file) {
        try {
            return awsClient.uploadImage(file);
        } catch (Throwable ex) {
            throw new MessageException(ex, HttpStatus.BAD_REQUEST);
        }
    }

    private void deletePhoto(Chat chat){
        String key = null;
        String regex = "/skillboxjava31/";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(chat.getPhoto());
        while (matcher.find()){
            key = chat.getPhoto().substring(matcher.end());
        }
        chat.setPhoto(null);
        awsClient.deleteImage(key);
    }

    @Transactional
    public String updatePhoto(Long chatId, MultipartFile file, TokenData data) {
        Chat chat = findMyChat(chatId, data.getId());
        chat.setPhoto(uploadPhoto(file));
        chatRepository.save(chat);
        return chat.getPhoto();
    }

    @Transactional
    public void deleteChatPhoto(Long chatId, TokenData data) {
        Chat chat = findMyChat(chatId, data.getId());
        deletePhoto(chat);
        chatRepository.save(chat);
    }

    public ChatDetailDTO getChatDetail(Long chatId, TokenData data) {
        Chat chat = findMyChat(chatId, data.getId());
        return chatMapper.toDetail(chat);
    }

    @Transactional
    public void updateChatInfo(UpdateChatInfoRequest req, TokenData data) {
        Chat chat = chatRepository.findByIdAndConsumersIsContaining(req.getChatId(),
                        personService.getOrCreatePerson(data.getId()))
                .orElseThrow(() -> new MessageException("Error! Chat not found!", HttpStatus.NOT_FOUND));

        chat.setName(getActualFieldValue(chat.getName(), req.getName()).trim());
        chat.setDescription(getActualFieldValue(chat.getDescription(), chat.getDescription()).trim());

        if (Objects.equals(chat.getAdmin().getId(), data.getId())) {
            chat.setAdmin(personService.getOrCreatePerson(req.getAdminId()));
        }

        chatRepository.save(chat);
    }

    private <T> T getActualFieldValue(T targetField, T sourceField) {
        return Optional.ofNullable(sourceField).orElse(targetField);
    }

    @Transactional
    public void addConsumerToChat(UpdateTheChatLineupRequest req, TokenData data) {
        Chat chat = findMyChat(req.getChatId(), data.getId());
        chat.getConsumers().addAll(personService.getPersons(req.getConsumersId()));
        ensurePrivateChatMaxConsumers(chat.getConsumers().size(), chat.getType());
        chatRepository.save(chat);
    }

    @Transactional
    public void deleteChat(Long id, boolean isForceDelete, TokenData tokenData) {
        Chat chat = chatRepository.findByIdAndConsumersIsContaining(id,
                        personService.getOrCreatePerson(tokenData.getId()))
                .orElseThrow(() -> new MessageException("Error! Chat not found!", HttpStatus.NOT_FOUND));

        if (isForceDelete && chat.getAdmin().getPersonId() == tokenData.getId()) {
            chatRepository.delete(chat);
        } else {
            chat.setConsumers(chat.getConsumers().stream()
                    .filter(c -> !c.getPersonId().equals(tokenData.getId()))
                    .collect(Collectors.toSet()));
            chatRepository.save(chat);
        }
    }

    public Set<Long> getConsumersId(Long chatId) {
        Chat chat = chatRepository.findWithConsumersById(chatId)
                .orElseThrow(() -> new MessageException("Error! Chat not found!", HttpStatus.NOT_FOUND));
        return chat.getConsumers().stream().map(Person::getPersonId).collect(Collectors.toSet());
    }

    public Chat findById(Long chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new MessageException("Error! Chat not found!", HttpStatus.NOT_FOUND));
    }
}
