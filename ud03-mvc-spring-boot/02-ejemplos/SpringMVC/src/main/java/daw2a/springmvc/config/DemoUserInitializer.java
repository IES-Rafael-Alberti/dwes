package daw2a.springmvc.config;

import daw2a.springmvc.model.User;
import daw2a.springmvc.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("demo")
public class DemoUserInitializer {

    @Bean
    CommandLineRunner provisionDemoUser(
            UserRepository users,
            PasswordEncoder passwordEncoder,
            @Value("${DEMO_USERNAME}") String username,
            @Value("${DEMO_PASSWORD}") String password) {
        return args -> {
            if (username.isBlank() || password.isBlank()) {
                throw new IllegalStateException("DEMO_USERNAME and DEMO_PASSWORD must not be blank");
            }
            if (users.findByUsername(username).isEmpty()) {
                users.save(new User(username, passwordEncoder.encode(password)));
            }
        };
    }
}
