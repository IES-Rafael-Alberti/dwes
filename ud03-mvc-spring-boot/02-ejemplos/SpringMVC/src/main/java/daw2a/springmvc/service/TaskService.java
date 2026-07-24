package daw2a.springmvc.service;

import daw2a.springmvc.error.TaskNotFoundException;
import daw2a.springmvc.form.TaskForm;
import daw2a.springmvc.model.Task;
import daw2a.springmvc.model.User;
import daw2a.springmvc.repository.TaskRepository;
import daw2a.springmvc.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class TaskService {
    private final TaskRepository tasks;
    private final UserRepository users;
    public TaskService(TaskRepository tasks, UserRepository users) { this.tasks = tasks; this.users = users; }

    @Transactional(readOnly = true)
    public List<Task> listOwnedBy(String username) { return tasks.findAllByOwnerUsernameOrderByIdAsc(username); }
    @Transactional(readOnly = true)
    public Task getOwned(Long id, String username) { return tasks.findByIdAndOwnerUsername(id, username).orElseThrow(TaskNotFoundException::new); }
    public Task create(TaskForm form, String username) {
        User owner = users.findByUsername(username).orElseThrow();
        return tasks.save(new Task(form.getDescription().trim(), owner));
    }
    public void update(Long id, TaskForm form, String username) { getOwned(id, username).rename(form.getDescription().trim()); }
    public void toggle(Long id, String username) { getOwned(id, username).toggle(); }
    public void delete(Long id, String username) { tasks.delete(getOwned(id, username)); }
}
