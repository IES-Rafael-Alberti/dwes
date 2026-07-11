package com.example.minitasks.services;

import com.example.minitasks.dto.CreateTaskDTO;
import com.example.minitasks.dto.UpdateTaskDTO;
import com.example.minitasks.entities.Task;
import com.example.minitasks.repositories.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TaskService {
    private final TaskRepository repo;
    public TaskService(TaskRepository repo) { this.repo = repo; }

    public List<Task> list(Boolean done) {
        return (done == null) ? repo.findAll() : repo.findByDone(done);
    }

    public Task create(CreateTaskDTO dto) {
        Task t = new Task(dto.title());
        t.setDone(false);
        return repo.save(t);
    }

    public Task toggle(Long id) {
        Task t = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Task not found"));
        t.setDone(!t.isDone());
        return t;
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) throw new IllegalArgumentException("Task not found");
        repo.deleteById(id);
    }

    public Page<Task> listPage(Boolean done, String q, Pageable pageable) {
        if (q != null && !q.isBlank()) {
            return repo.findByTitleContainingIgnoreCase(q, pageable);
        }
        return repo.findAll(pageable);
    }

    public Task update(Long id, UpdateTaskDTO dto) {
        Task t = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Task not found"));
        if (dto.title() != null && !dto.title().isBlank()) t.setTitle(dto.title());
        if (dto.done() != null) t.setDone(dto.done());
        return t;
    }
}
