package daw2a.springmvc;

import daw2a.springmvc.model.User;
import daw2a.springmvc.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.profiles.active=demo",
        "DEMO_USERNAME=demo-test",
        "DEMO_PASSWORD=demo-test-password",
        "spring.datasource.url=jdbc:h2:mem:demo-test;DB_CLOSE_DELAY=-1"
})
class DemoUserInitializerTests {
    @Autowired UserRepository users;
    @Autowired PasswordEncoder passwords;

    @Test
    void demoProfileCreatesOneUserWithABcryptPassword() {
        User user = users.findByUsername("demo-test").orElseThrow();
        assertThat(passwords.matches("demo-test-password", user.getPassword())).isTrue();
        assertThat(users.findAll()).hasSize(1);
    }
}
