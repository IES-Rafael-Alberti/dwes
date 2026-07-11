package com.example.minitasks.services;
import com.example.minitasks.dto.CreateTaskDTO;
import com.example.minitasks.entities.Task;
import com.example.minitasks.repositories.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service @Transactional
public class TaskService {
  private final TaskRepository repo; public TaskService(TaskRepository repo){this.repo=repo;}
  public List<Task> list(Boolean done){ return (done==null)? repo.findAll(): repo.findByDone(done); }
  public Task create(CreateTaskDTO dto){ Task t=new Task(dto.title()); t.setDone(false); return repo.save(t); }
  public Task toggle(Long id){ Task t=repo.findById(id).orElseThrow(()->new IllegalArgumentException("Task not found")); t.setDone(!t.isDone()); return t; }
  public void delete(Long id){ if(!repo.existsById(id)) throw new IllegalArgumentException("Task not found"); repo.deleteById(id); }
}