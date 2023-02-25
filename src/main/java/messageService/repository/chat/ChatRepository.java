package messageService.repository.chat;

import messageService.model.chat.Chat;
import messageService.model.person.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    @EntityGraph("chat_with_admin_and_consumers")
    Page<Chat> findDistinctByConsumersContaining(Person person, Pageable pageable);

    long countDistinctByConsumersContaining(Person person);

    @EntityGraph("chat_with_admin_and_consumers")
    Optional<Chat> findByIdAndConsumersIsContaining(Long chatId, Person person);

    Optional<Chat> findByConsumersIsContainingAndId(Person person, Long chatId);

    @EntityGraph("chat_with_admin_and_consumers")
    Optional<Chat> findWithConsumersById(Long chatId);

    @Query(value = "SELECT c.* FROM jt_person_chat AS jt " +
            "    JOIN chat AS c ON c.id = jt.chat_id " +
            "    WHERE type = 'PRIVATE' " +
            "      AND ((c.admin_id = :anotherConsumerId AND jt.person_id = :consumerId) " +
            "               OR (c.admin_id = :consumerId AND jt.person_id = :anotherConsumerId))", nativeQuery = true)
    Optional<Chat> findPrivateChatByConsumersId(@Param("consumerId") Long consumerId, @Param("anotherConsumerId") Long anotherConsumerId);
}
