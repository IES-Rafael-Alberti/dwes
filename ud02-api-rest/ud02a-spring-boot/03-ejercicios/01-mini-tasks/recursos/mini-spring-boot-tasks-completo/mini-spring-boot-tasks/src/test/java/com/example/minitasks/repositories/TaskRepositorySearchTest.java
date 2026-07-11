package com.example.minitasks.repositories;

import com.example.minitasks.entities.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.jpa.defer-datasource-initialization=true")
class TaskRepositorySearchTest {

    @Autowired TaskRepository repo;

    @Test
    void findByTitleContainingIgnoreCase_returnsMatches() {
        repo.deleteAll();
        repo.save(new Task("Aprender Spring"));
        repo.save(new Task("Comprar pan"));
        repo.save(new Task("spring data jpa"));

        List<Task> res = repo.findByTitleContainingIgnoreCase("spring");
        assertThat(res).extracting(Task::getTitle)
            .containsExactlyInAnyOrder("Aprender Spring", "spring data jpa");
    }
}
