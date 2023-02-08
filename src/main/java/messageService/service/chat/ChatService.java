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
import messageService.service.message.MessageService;
import messageService.service.person.PersonService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import security.dto.TokenData;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;

    private final AwsClient awsClient;

    private final ChatMapper chatMapper;

    private final MessageMapper messageMapper;

    private final MessageService messageService;

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

        findAndSetLastMessagesToChats(chats);
        return new HashSet<>(chats.values());
    }

    private void findAndSetLastMessagesToChats(Map<Long, ChatDTO> chats) {
        messageService.findLastMessagesByChatIds(chats.keySet())
                .forEach(ms -> chats.get(ms.getChat().getId())
                        .setLastMessage(messageMapper.toDTO(ms)));
    }

    public ChatDTO createNewChat(CreateChatRequest req, TokenData tokenData) {
        Set<Person> consumers = personService.getPersons(req.getConsumersIds());
        Person admin = personService.getOrCreatePerson(tokenData.getId());
        consumers.add(admin);
        Chat chat = Chat.builder()
                .type(req.getType())
                .name(req.getName())
                .photo(req.getPhoto())
                .description(req.getDescription())
                .createDateTime(LocalDateTime.now())
                .consumers(consumers)
                .admin(admin)
                .build();

        return chatMapper.toDTO(chatRepository.save(chat));
    }

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

    public String updatePhoto(Long chatId, MultipartFile file, TokenData data) {
        Chat chat = findMyChat(chatId, data.getId());
        chat.setPhoto(uploadPhoto(file));
        chatRepository.save(chat);
        return chat.getPhoto();
    }

    public void deleteChatPhoto(Long chatId, TokenData data) {
        Chat chat = findMyChat(chatId, data.getId());
        deletePhoto(chat);
        chatRepository.save(chat);
    }

    public ChatDetailDTO getChatDetail(Long chatId, TokenData data) {
        Chat chat = findMyChat(chatId, data.getId());
        return chatMapper.toDetail(chat);
    }

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

    public void addConsumerToChat(UpdateTheChatLineupRequest req, TokenData data) {
        Chat chat = findMyChat(req.getChatId(), data.getId());
        chat.getConsumers().addAll(personService.getPersons(req.getConsumersId()));
        chatRepository.save(chat);
    }

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
}
