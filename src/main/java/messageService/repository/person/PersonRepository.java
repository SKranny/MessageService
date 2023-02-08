package messageService.repository.person;

import messageService.model.person.Person;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByPersonId(Long personId);

    @EntityGraph("withAllMyChats")
    Optional<Person> findWithChatsById(Long id);
    Set<Person> findAllByPersonIdIn(Collection<Long> personIds);
}
