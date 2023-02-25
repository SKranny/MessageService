package messageService.service.person;

import dto.userDto.PersonDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@FeignClient("person-service/api/v1/account")
public interface PersonMicroService {
    @GetMapping("/info/{id}")
    PersonDTO getPersonById(@PathVariable(name = "id") Long id);

    @GetMapping("/accountIds")
    Set<PersonDTO> getAccountByIds(@RequestParam List<Long> usersId);
}
