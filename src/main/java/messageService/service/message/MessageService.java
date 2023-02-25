package messageService.service.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import messageService.constants.attachment.AttachmentType;
import messageService.dto.mesage.*;
import messageService.exception.MessageException;
import messageService.mapper.MessageMapper;
import messageService.model.message.Attachment;
import messageService.model.message.Message;
import messageService.model.person.Person;
import messageService.repository.message.MessageRepository;
import messageService.service.chat.ChatService;
import messageService.service.person.PersonService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;

    private final PersonService personService;

    private final MessageMapper messageMapper;

    private final ObjectMapper jacksonMapper;

    @Lazy
    private final ChatService chatService;

    @Transactional
    public MessageDTO likeMessage(LikeMessage likeMessage) {
        Message message = messageRepository.findWithLikedPersonsById(likeMessage.getMessageId())
                .orElseThrow(() -> new MessageException("Error! Message not found!"));
        if (isTrue(likeMessage.getIsLike())) {
            message.getWhoIsLike().add(personService.findById(likeMessage.getPersonId()));
            messageRepository.save(message);
        } else {
            message.setWhoIsLike(message.getWhoIsLike().stream()
                    .filter(p -> !p.getPersonId().equals(likeMessage.getPersonId()))
                    .collect(Collectors.toSet()));
            messageRepository.save(message);
        }
        return messageMapper.toDTO(message);
    }

    public MessageDTO saveMessage(SendMessage sendMessage) {
        Message message = Message.builder()
                .text(sendMessage.getContent())
                .author(personService.findById(sendMessage.getAuthorId()))
                .chat(chatService.findById(sendMessage.getChatId()))
                .attachments(buildAttachments(Optional.ofNullable(sendMessage.getMessageAttachments()).orElse(new ArrayList<>())))
                .createDateTime(LocalDateTime.now())
                .build();
        messageRepository.save(message);
        return messageMapper.toDTO(message);
    }

    private List<Attachment> buildAttachments(List<SendAttachment> messageAttachments) {
        Map<AttachmentType, List<SendAttachment>> attachmentTypeListMap = messageAttachments.stream()
                .collect(Collectors.groupingBy(SendAttachment::getType));

        List<Attachment> attachments = new ArrayList<>();

        for (Map.Entry<AttachmentType, List<SendAttachment>> entry: attachmentTypeListMap.entrySet()) {
            switch (entry.getKey()) {
                case MESSAGE:
                    attachments.addAll(buildAttachedMessages(entry.getValue()));
                    break;
                case POST:
                case MEDIA:
                    attachments.addAll(buildAttachmentsMedia(entry.getValue()));
                    break;
                default:
                    throw new MessageException("Error! Unknown message type!");
            }
        }

        return attachments;
    }

    private Set<Attachment> buildAttachmentsMedia(List<SendAttachment> attachments) {
        return attachments.stream().map(a -> Attachment.builder()
                        .postId(a.getPostId())
                        .content(a.getContent())
                        .type(a.getType())
                .build())
                .collect(Collectors.toSet());
    }

    private Set<Attachment> buildAttachedMessages(List<SendAttachment> attachments) {
        Set<Long> messagesIds = attachments.stream()
                .map(SendAttachment::getAttachedMessageId)
                .collect(Collectors.toSet());
        return messageRepository.findAllByIdIn(messagesIds).stream()
                .map(m -> Attachment.builder()
                        .type(AttachmentType.MESSAGE)
                        .attachedMessage(m)
                        .build())
                .collect(Collectors.toSet());
    }

    @Transactional
    public Long deleteMessage(DeleteMessage deleteMessage) {
        Message message = messageRepository.findWithAuthorById(deleteMessage.getMessageId())
                .orElseThrow(() -> new MessageException("Error! Message not found!"));
        if (Objects.equals(message.getAuthor().getPersonId(), deleteMessage.getPersonId()) && isTrue(deleteMessage.getIsForce())) {
            messageRepository.deleteById(deleteMessage.getMessageId());
        } else {
            message.getWhoIsDelete().add(personService.findById(deleteMessage.getPersonId()));
            messageRepository.save(message);
        }
        return message.getChat().getId();
    }

    @SneakyThrows
    public Page<MessageDTO> getMessagesByFilter(Long chatId, Long personId, PageRequest pageable) {
        Person person = personService.findById(personId);
        Page<Message> messagePage = messageRepository.findAllByChat_IdAndWhoIsDeleteIsNotContainingOrderByCreateDateTimeDesc(chatId, person, pageable);
        Message message = messagePage.getContent().get(0);
        ZonedDateTime zonedDateTime = LocalDateTime.now().atZone(ZoneId.of("Europe/Moscow"));
        System.out.println(jacksonMapper.writeValueAsString(zonedDateTime));
        return new PageImpl<>(messagePage.getContent().stream()
                .sorted(Comparator.comparing(Message::getCreateDateTime))
                .map(messageMapper::toDTO)
                .collect(Collectors.toList()),
                pageable, messageRepository.countAllByChat_IdAndWhoIsDeleteIsNotContainingOrderByCreateDateTimeDesc(chatId, person));
    }
}
