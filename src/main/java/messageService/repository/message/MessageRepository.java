package messageService.repository.message;

import messageService.model.message.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query(value = "SELECT ms.* FROM message AS ms " +
            "WHERE create_date_time IN (SELECT MAX(create_date_time) FROM message AS ms2 WHERE ms2.chat_id = ms.chat_id) " +
            "AND chat_id in (:chatIds)", nativeQuery = true)
    List<Message> findLastMessagesByChatsId(@Param("chatIds") Collection<Long> chatIds);
}
