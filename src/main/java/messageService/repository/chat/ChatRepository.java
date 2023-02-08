package messageService.repository.chat;

import messageService.model.chat.Chat;
import messageService.model.person.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ChatRepository extends JpaRepository<Chat, Long> {

    Page<Chat> findDistinctByConsumersContaining(Person person, Pageable pageable);

    @EntityGraph("chat_with_admin_and_consumers")
    Optional<Chat> findByIdAndConsumersIsContaining(Long chatId, Person person);

    Optional<Chat> findByConsumersIsContainingAndId(Person person, Long chatId);
}
