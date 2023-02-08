package messageService.service.person;

import dto.userDto.PersonDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient("person-service/api/v1/account")
public interface PersonMicroService {
    @PostMapping
    PersonDTO createPerson(@RequestBody PersonDTO personDTO);

    @GetMapping("/{email}")
    PersonDTO getPersonDTOByEmail(@PathVariable(name = "email") String email);

    @GetMapping("/info/{id}")
    PersonDTO getPersonById(@PathVariable(name = "id") Long id);

    @GetMapping("/me")
    PersonDTO getMyAccount();
}
