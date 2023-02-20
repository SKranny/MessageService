package messageService.mapper;

import messageService.dto.customers.ShortPerson;
import messageService.dto.mesage.AttachmentDTO;
import messageService.dto.mesage.MessageDTO;
import messageService.model.chat.Chat;
import messageService.model.message.Attachment;
import messageService.model.message.Message;
import messageService.model.person.Person;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class MessageMapper {


    private final AttachmentMapper attachmentMapper;
    private final PersonMapper personMapper;


    public MessageMapper(@Lazy AttachmentMapper attachmentMapper, PersonMapper personMapper) {
        this.attachmentMapper = attachmentMapper;
        this.personMapper = personMapper;
    }


    public MessageDTO toDTO(Message message) {
        if ( message == null ) {
            return null;
        }

        MessageDTO.MessageDTOBuilder messageDTO = MessageDTO.builder();

        messageDTO.chatId( messageChatId( message ) );
        messageDTO.id( message.getId() );
        messageDTO.text( message.getText() );
        messageDTO.author( personMapper.toShort( message.getAuthor() ) );
        messageDTO.attachments( attachmentListToAttachmentDTOList( message.getAttachments() ) );
        messageDTO.createDateTime( message.getCreateDateTime() );
        messageDTO.whoIsLike( personListToShortPersonList(message.getWhoIsLike()) );

        return messageDTO.build();
    }

    private Long messageChatId(Message message) {
        if ( message == null ) {
            return null;
        }
        Chat chat = message.getChat();
        if ( chat == null ) {
            return null;
        }
        Long id = chat.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    protected List<AttachmentDTO> attachmentListToAttachmentDTOList(List<Attachment> list) {
        if ( list == null ) {
            return null;
        }

        List<AttachmentDTO> list1 = new ArrayList<AttachmentDTO>( list.size() );
        for ( Attachment attachment : list ) {
            list1.add( attachmentMapper.toDTO( attachment ) );
        }

        return list1;
    }

    protected Set<ShortPerson> personListToShortPersonList(Set<Person> set) {
        if ( set == null ) {
            return null;
        }

        Set<ShortPerson> set1 = new HashSet<>( set.size() );
        for ( Person person : set ) {
            set1.add( personMapper.toShort( person ) );
        }

        return set1;
    }
}
