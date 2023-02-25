package messageService.repository.message;

import messageService.model.message.Message;
import messageService.model.person.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query(value = "SELECT ms.* FROM message AS ms " +
            "WHERE create_date_time IN (SELECT MAX(create_date_time) FROM message AS ms2 WHERE ms2.chat_id = ms.chat_id) " +
            "AND chat_id in (:chatIds) " +
            "AND ms.id NOT IN (SELECT message_id FROM jt_message_delete AS del where person_id = :personId)", nativeQuery = true)
    List<Message> findLastMessagesByChatsId(@Param("chatIds") Collection<Long> chatIds, @Param("personId") Long personId);

    List<Message> findAllByIdIn(Collection<Long> messagesIds);

    Optional<Message> findWithLikedPersonsById(Long id);

    Optional<Message> findWithAuthorById(Long id);

    Page<Message> findAllByChat_IdAndWhoIsDeleteIsNotContainingOrderByCreateDateTimeDesc(Long chatId, Person whoIsDelete, Pageable pageable);

    long countAllByChat_IdAndWhoIsDeleteIsNotContainingOrderByCreateDateTimeDesc(Long chatId, Person whoIsDelete);
}
