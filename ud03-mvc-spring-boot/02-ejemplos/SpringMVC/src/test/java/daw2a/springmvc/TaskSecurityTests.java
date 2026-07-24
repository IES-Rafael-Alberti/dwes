package daw2a.springmvc;

import daw2a.springmvc.model.Task;
import daw2a.springmvc.model.User;
import daw2a.springmvc.repository.TaskRepository;
import daw2a.springmvc.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskSecurityTests {
    @Autowired MockMvc mvc;
    @Autowired UserRepository users;
    @Autowired TaskRepository tasks;
    @Autowired PasswordEncoder passwords;
    User ana;
    User bob;

    @BeforeEach
    void setUp() {
        tasks.deleteAll();
        users.deleteAll();
        ana = users.save(new User("ana", passwords.encode("password-ana")));
        bob = users.save(new User("bob", passwords.encode("password-bob")));
    }

    @Test
    void listingContainsOnlyTheOwnersTasks() throws Exception {
        tasks.save(new Task("Ana task", ana));
        tasks.save(new Task("Bob task", bob));
        mvc.perform(get("/tasks").with(user("ana")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Ana task")))
                .andExpect(content().string(not(containsString("Bob task"))));
    }

    @Test
    void foreignAndMissingTasksBothReturnNotFound() throws Exception {
        Task foreign = tasks.save(new Task("Private", bob));
        mvc.perform(get("/tasks/{id}/edit", foreign.getId()).with(user("ana")))
                .andExpect(status().isNotFound());
        mvc.perform(get("/tasks/{id}/edit", 999999).with(user("ana")))
                .andExpect(status().isNotFound());
    }

    @Test
    void ownerCanCreateEditToggleAndDelete() throws Exception {
        mvc.perform(post("/tasks").with(user("ana")).with(csrf()).param("description", "Learn MVC"))
                .andExpect(status().is3xxRedirection());
        Task task = tasks.findAllByOwnerUsernameOrderByIdAsc("ana").getFirst();
        mvc.perform(post("/tasks/{id}", task.getId()).with(user("ana")).with(csrf()).param("description", "Learn secure MVC"))
                .andExpect(status().is3xxRedirection());
        mvc.perform(post("/tasks/{id}/toggle", task.getId()).with(user("ana")).with(csrf()))
                .andExpect(status().is3xxRedirection());
        mvc.perform(post("/tasks/{id}/delete", task.getId()).with(user("ana")).with(csrf()))
                .andExpect(status().is3xxRedirection());
        org.assertj.core.api.Assertions.assertThat(tasks.findById(task.getId())).isEmpty();
    }

    @Test
    void foreignTasksCannotBeUpdatedToggledOrDeleted() throws Exception {
        Task foreign = tasks.save(new Task("Private", bob));
        mvc.perform(post("/tasks/{id}", foreign.getId()).with(user("ana")).with(csrf()).param("description", "Stolen"))
                .andExpect(status().isNotFound());
        mvc.perform(post("/tasks/{id}/toggle", foreign.getId()).with(user("ana")).with(csrf()))
                .andExpect(status().isNotFound());
        mvc.perform(post("/tasks/{id}/delete", foreign.getId()).with(user("ana")).with(csrf()))
                .andExpect(status().isNotFound());
        org.assertj.core.api.Assertions.assertThat(tasks.findById(foreign.getId())).get()
                .extracting(Task::getDescription, Task::isCompleted).containsExactly("Private", false);
    }

    @Test
    void invalidDescriptionRedisplaysFormWithoutSaving() throws Exception {
        mvc.perform(post("/tasks").with(user("ana")).with(csrf()).param("description", " "))
                .andExpect(status().isOk()).andExpect(view().name("tasks/new"))
                .andExpect(model().attributeHasFieldErrors("taskForm", "description"));
        org.assertj.core.api.Assertions.assertThat(tasks.count()).isZero();
    }

    @Test
    void mutatingRequestWithoutCsrfIsForbidden() throws Exception {
        mvc.perform(post("/tasks").with(user("ana")).param("description", "No token"))
                .andExpect(status().isForbidden());
    }

    @Test
    void persistedUserAuthenticatesOnlyWithTheCorrectPassword() throws Exception {
        mvc.perform(formLogin().user("ana").password("password-ana"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"))
                .andExpect(authenticated().withUsername("ana"));

        mvc.perform(formLogin().user("ana").password("wrong-password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @Test
    void thymeleafFormsIncludeCsrfAndLogoutRequiresIt() throws Exception {
        mvc.perform(get("/tasks/new").with(user("ana")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("name=\"_csrf\"")));

        mvc.perform(post("/logout").with(user("ana")))
                .andExpect(status().isForbidden());
        mvc.perform(post("/logout").with(user("ana")).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout"));
    }

    @Test
    void invalidEditDoesNotMutateTheTask() throws Exception {
        Task task = tasks.save(new Task("Keep this", ana));
        mvc.perform(post("/tasks/{id}", task.getId()).with(user("ana")).with(csrf())
                        .param("description", " "))
                .andExpect(status().isOk())
                .andExpect(view().name("tasks/edit"))
                .andExpect(model().attributeHasFieldErrors("taskForm", "description"));
        org.assertj.core.api.Assertions.assertThat(tasks.findById(task.getId())).get()
                .extracting(Task::getDescription).isEqualTo("Keep this");
    }
}
