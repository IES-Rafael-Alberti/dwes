package com.example.minitasks.repositories;

import com.example.minitasks.entities.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.jpa.defer-datasource-initialization=true")
class TaskRepositoryDataSqlTest {
    @Autowired TaskRepository repo;

    @Test void dataSql_loadedWithTwoRows() {
        List<Task> all = repo.findAll();
        assertThat(all).extracting(Task::getTitle)
            .contains("Probar v3 listado", "Leer README");
    }
}
