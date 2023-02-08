package messageService.service.person;

import dto.userDto.PersonDTO;
import kafka.dto.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import messageService.exception.MessageException;
import messageService.model.person.Person;
import messageService.repository.person.PersonRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PersonService {
    private final PersonMicroService personMicroService;

    private final PersonRepository personRepository;


    @KafkaListener(topics = "NewCustomer")
    public void registerPerson(PersonDTO personDTO) {
        Optional.ofNullable(personDTO)
                .map(req -> Person.builder()
                        .personId(req.getId())
                        .userName(req.getLastName().concat(" ").concat(req.getFirstName()))
                        .build()
                ).map(personRepository::save)
                .orElseThrow(() -> new MessageException("Error! Invalid kafka message"));
    }


    public Person getOrCreatePerson(Long personId) {
        return personRepository.findByPersonId(personId)
                .orElseGet(() -> Optional.ofNullable(personMicroService.getPersonById(personId))
                        .map(p -> Person.builder()
                                .personId(p.getId())
                                .userName(p.getLastName().concat(" ").concat(p.getFirstName()))
                                .build())
                        .map(personRepository::save)
                        .orElseThrow(() -> new MessageException("Error! Person not found!")));
    }

    public Person getPersonWithChats(Long personId) {
        return personRepository.findWithChatsById(personId)
                .orElseThrow(() -> new MessageException("Error! User not found"));
    }

    public Set<Person> getPersons(List<Long> consumersIds) {
        return personRepository.findAllByPersonIdIn(consumersIds);
    }
}
