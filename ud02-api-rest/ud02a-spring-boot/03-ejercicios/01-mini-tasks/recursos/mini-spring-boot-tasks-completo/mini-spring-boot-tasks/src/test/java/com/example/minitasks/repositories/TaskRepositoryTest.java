package com.example.minitasks.repositories;

import com.example.minitasks.entities.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(properties = "spring.jpa.defer-datasource-initialization=true")
class TaskRepositoryTest {

    @Autowired TaskRepository repo;

    @Test void save_generatesId_and_defaultsDoneFalse() {
        Task saved = repo.save(new Task("Persistida"));
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.isDone()).isFalse();
    }

    @Test void findByDone_returnsOnlyMatching() {
        Task t1 = repo.save(new Task("A"));
        Task t2 = repo.save(new Task("B")); t2.setDone(true); repo.save(t2);

        List<Task> done = repo.findByDone(true);
        List<Task> notDone = repo.findByDone(false);

        assertThat(done).extracting(Task::getTitle).containsExactly("B");
        assertThat(notDone).extracting(Task::getTitle).contains("A");
    }

    @Test void title_notNull_constraint_enforced() {
        Task invalid = new Task();
        assertThatThrownBy(() -> repo.saveAndFlush(invalid))
            .isInstanceOf(DataIntegrityViolationException.class);
    }
}
