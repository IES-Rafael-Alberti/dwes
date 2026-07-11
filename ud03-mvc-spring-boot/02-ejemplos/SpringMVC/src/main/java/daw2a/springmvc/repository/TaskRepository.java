package daw2a.springmvc.repository;

import daw2a.springmvc.model.Task;
import daw2a.springmvc.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByDescriptionContainingIgnoreCase(String keyword);
    List<Task> findByUser(User user); // Buscar tareas de un usuario específico

    List<Task> findByCompleted(boolean completed);
}

