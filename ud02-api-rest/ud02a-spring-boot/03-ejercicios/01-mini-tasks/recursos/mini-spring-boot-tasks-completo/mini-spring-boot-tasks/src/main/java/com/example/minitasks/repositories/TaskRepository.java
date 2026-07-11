package com.example.minitasks.repositories;

import com.example.minitasks.entities.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByDone(boolean done);
    List<Task> findByTitleContainingIgnoreCase(String q);
    Page<Task> findByTitleContainingIgnoreCase(String q, Pageable pageable);
}
