package com.example.minitasks.repositories;
import com.example.minitasks.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.*;
import java.util.*;
public interface TaskRepository extends JpaRepository<Task, Long>{ List<Task> findByDone(boolean done); List<Task> findByTitleContainingIgnoreCase(String q); Page<Task> findByTitleContainingIgnoreCase(String q, Pageable p);}