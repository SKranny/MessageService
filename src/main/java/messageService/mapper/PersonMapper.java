package messageService.mapper;

import messageService.dto.customers.ShortPerson;
import messageService.model.person.Person;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PersonMapper {
    ShortPerson toShort(Person person);
}
