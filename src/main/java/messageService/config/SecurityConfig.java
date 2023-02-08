package messageService.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
public class SecurityConfig {

    @Autowired
    public HttpSecurity httpSecurity(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeHttpRequests(conf -> {
                    conf.antMatchers("/api/v1/message/**").authenticated();
                    conf.antMatchers("/api/v1/chats/**").authenticated();
                    conf.antMatchers("/v3/api-docs/**").permitAll();
                    conf.antMatchers("/docs/**").permitAll();
                    conf.anyRequest().authenticated();
                });
    }
}
