package com.example.tasks.controllers;

import com.example.tasks.entities.Task;
import com.example.tasks.dto.CreateTaskDTO;
import com.example.tasks.services.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
// Vamos a refactorizar el controlador para que use el servicio TaskService en lugar de interactuar directamente con el repositorio.
@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<Task> list(@RequestParam(required = false) Boolean done) {
       return taskService.list(done);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> taskById(@PathVariable Long id) {
        Task task= taskService.showTask(id);
        return ResponseEntity.ok(task);
    }

    @PostMapping
    public ResponseEntity<Task> create(@RequestBody @Valid CreateTaskDTO dto) {
        Task saved = taskService.create(dto);

        return ResponseEntity
                .created(URI.create("/tasks/" + saved.getId()))
                .body(saved);
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Task> toggle(@PathVariable Long id) {
        Task toggled = taskService.toggle(id);
        return ResponseEntity.ok(toggled);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}