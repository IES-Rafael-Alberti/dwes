package com.example.minitasks.web;
import com.example.minitasks.dto.CreateTaskDTO;
import com.example.minitasks.entities.Task;
import com.example.minitasks.services.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI; import java.util.List;
@RestController @RequestMapping("/v4/tasks")
public class TaskControllerV4 {
  private final TaskService service; public TaskControllerV4(TaskService service){this.service=service;}
  @GetMapping public ResponseEntity<List<Task>> list(@RequestParam(required=false) Boolean done){ return ResponseEntity.ok(service.list(done)); }
  @PostMapping public ResponseEntity<Task> create(@RequestBody @jakarta.validation.Valid CreateTaskDTO dto){ Task saved=service.create(dto); return ResponseEntity.created(URI.create("/v4/tasks/"+saved.getId())).body(saved); }
  @PatchMapping("/{id}/toggle") public ResponseEntity<Task> toggle(@PathVariable Long id){ return ResponseEntity.ok(service.toggle(id)); }
  @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id){ service.delete(id); return ResponseEntity.noContent().build(); }
}