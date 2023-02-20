package messageService.model.message;

import lombok.*;
import messageService.constants.attachment.AttachmentType;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "attachment")
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "message_id", referencedColumnName = "id")
    private Message message;

    private AttachmentType type;

    private String content;

    private Long postId;

    @OneToOne
    @JoinColumn(name = "attached_message_id", referencedColumnName = "id")
    private Message attachedMessage;
}
