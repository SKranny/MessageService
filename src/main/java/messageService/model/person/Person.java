package messageService.model.person;

import lombok.*;
import messageService.model.chat.Chat;
import messageService.model.message.Message;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "person")
@NamedEntityGraphs(
        @NamedEntityGraph(
                name = "withAllMyChats",
                attributeNodes = @NamedAttributeNode("chats")
        )
)
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    private Long personId;

    private String userName;

    @NotNull
    @Builder.Default
    @ManyToMany(mappedBy = "consumers")
    private Set<Chat> chats = new HashSet<>();

    @NotNull
    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "jt_message_like",
            joinColumns = @JoinColumn(name = "person_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "message_id", referencedColumnName = "id"))
    private Set<Message> likeMessage = new HashSet<>();

    @NotNull
    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "jt_message_delete",
            joinColumns = @JoinColumn(name = "person_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "message_id", referencedColumnName = "id"))
    private Set<Message> deletedMessages = new HashSet<>();
}
