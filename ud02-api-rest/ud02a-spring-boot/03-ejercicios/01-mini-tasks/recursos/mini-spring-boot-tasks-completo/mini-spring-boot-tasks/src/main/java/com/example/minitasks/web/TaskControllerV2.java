package com.example.minitasks.web;

import com.example.minitasks.dto.CreateTaskDTO;
import com.example.minitasks.model.SimpleTask;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/v2")
public class TaskControllerV2 {

    private final List<SimpleTask> inMemory = new ArrayList<>();
    private final AtomicLong seq = new AtomicLong(0);

    @GetMapping("/sample")
    public ResponseEntity<Map<String, Object>> sample() {
        return ResponseEntity.ok(Map.of("message", "hola", "time", System.currentTimeMillis()));
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<SimpleTask>> list() {
        return ResponseEntity.ok(inMemory);
    }

    @PostMapping("/tasks")
    public ResponseEntity<SimpleTask> create(@RequestBody CreateTaskDTO dto) {
        SimpleTask t = new SimpleTask(seq.incrementAndGet(), dto.title(), false);
        inMemory.add(t);
        return ResponseEntity.created(URI.create("/v2/tasks/" + t.getId())).body(t);
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        inMemory.removeIf(t -> Objects.equals(t.getId(), id));
        return ResponseEntity.noContent().build();
    }
}
