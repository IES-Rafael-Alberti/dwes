package com.example.minitasks.repositories;

import com.example.minitasks.entities.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.jpa.defer-datasource-initialization=true")
class TaskRepositoryPagingSortingTest {

    @Autowired TaskRepository repo;

    @BeforeEach
    void seed() {
        repo.deleteAll();
        repo.save(new Task("Zeta"));
        repo.save(new Task("Alfa"));
        repo.save(new Task("Gamma"));
        repo.save(new Task("Beta"));
        repo.save(new Task("Delta"));
    }

    @Test
    void paging_returnsFirstPageOfTwo_sortedByTitleAsc() {
        Pageable pageable = PageRequest.of(0, 2, Sort.by("title").ascending());
        Page<Task> page = repo.findAll(pageable);

        assertThat(page.getContent()).extracting(Task::getTitle)
            .containsExactly("Alfa", "Beta");
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getTotalPages()).isEqualTo(3);
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    void secondPage_sortedDesc() {
        Pageable pageable = PageRequest.of(1, 2, Sort.by("title").descending());
        Page<Task> page = repo.findAll(pageable);

        assertThat(page.getContent()).extracting(Task::getTitle)
            .containsExactly("Delta", "Beta");
        assertThat(page.getNumber()).isEqualTo(1);
        assertThat(page.getSize()).isEqualTo(2);
    }
}
