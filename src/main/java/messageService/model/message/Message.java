package messageService.model.message;

import lombok.*;
import messageService.model.chat.Chat;
import messageService.model.person.Person;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    private String text;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Person author;

    @NotNull
    @Builder.Default
    private Boolean isChange = false;

    private LocalDateTime createDateTime;

    @Builder.Default
    @OneToMany(mappedBy = "message")
    private List<Attachment> attachments = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "chat_id", referencedColumnName = "id")
    private Chat chat;

    @Builder.Default
    @ManyToMany(mappedBy = "likeMessage")
    private Set<Person> whoIsLike = new HashSet<>();

    @Builder.Default
    @ManyToMany(mappedBy = "deletedMessages")
    private Set<Person> whoIsDelete = new HashSet<>();
}
