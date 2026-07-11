package com.example.minitasks.web;
import com.example.minitasks.dto.CreateTaskDTO;
import com.example.minitasks.entities.Task;
import com.example.minitasks.repositories.TaskRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/v3/tasks")
public class TaskControllerV3 {
  private final TaskRepository repo; public TaskControllerV3(TaskRepository repo){this.repo=repo;}
  @GetMapping public List<Task> list(@RequestParam(required=false) Boolean done){ return (done==null)? repo.findAll(): repo.findByDone(done); }
  @PostMapping @ResponseStatus(HttpStatus.CREATED) public Task create(@RequestBody @jakarta.validation.Valid CreateTaskDTO dto){ return repo.save(new Task(dto.title())); }
  @PatchMapping("/{id}/toggle") public Task toggle(@PathVariable Long id){ Task t=repo.findById(id).orElseThrow(()->new IllegalArgumentException("Task not found")); t.setDone(!t.isDone()); return t; }
  @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id){ if(!repo.existsById(id)) throw new IllegalArgumentException("Task not found"); repo.deleteById(id); }
}