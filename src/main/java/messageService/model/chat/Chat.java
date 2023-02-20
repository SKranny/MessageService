package messageService.model.chat;

import lombok.*;
import messageService.constants.chat.ChatType;
import messageService.model.message.Message;
import messageService.model.person.Person;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chat")
@NamedEntityGraphs(
        @NamedEntityGraph(
                name = "chat_with_admin_and_consumers",
                attributeNodes = {
                        @NamedAttributeNode(value = "admin"),
                        @NamedAttributeNode(value = "consumers")
                }
        )
)
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    private String name;

    private String photo;

    @Enumerated(EnumType.STRING)
    private ChatType type;

    private String description;

    @Builder.Default
    private LocalDateTime createDateTime = LocalDateTime.now();

    @Builder.Default
    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL)
    private Set<Message> messages = new HashSet<>();

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "admin_id", referencedColumnName = "id")
    private Person admin;

    @NotNull
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Builder.Default
    @JoinTable(name = "jt_person_chat",
            joinColumns = @JoinColumn(name = "chat_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "person_id", referencedColumnName = "id"))
    private Set<Person> consumers = new HashSet<>();
}
