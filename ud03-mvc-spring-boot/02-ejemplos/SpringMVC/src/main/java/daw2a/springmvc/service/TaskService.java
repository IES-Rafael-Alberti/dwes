package daw2a.springmvc.service;

import daw2a.springmvc.model.Task;
import daw2a.springmvc.model.User;
import daw2a.springmvc.repository.TaskRepository;
import daw2a.springmvc.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }


    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public void saveTask(Task task) {
        taskRepository.save(task);
    }

    public void changeTaskState(Long id) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.setCompleted(task.isNotCompleted());
            taskRepository.save(task);
        }
    }

    public void deleteTask(Long id) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.setCompleted(task.isNotCompleted());
            taskRepository.deleteById(id);
        }
    }

    public void deleteTasksByIds(List<Long> taskIds) {
        taskRepository.deleteAllById(taskIds);
    }

    public void deleteTask(Long id, User user) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            if (task.getUser().equals(user)) { // Validamos que la tarea pertenece al usuario
                taskRepository.delete(task);
            }
        }
    }

    public void changeStatusForTasks(List<Long> taskIds) {
        List<Task> tasks = taskRepository.findAllById(taskIds);
        for (Task task : tasks) {
            task.setCompleted(task.isNotCompleted()); // Alterna el estado
        }
        taskRepository.saveAll(tasks); // Guarda los cambios en lote
    }

    public List<Task> searchTasks(String keyword) {
        return taskRepository.findByDescriptionContainingIgnoreCase(keyword);
    }

    public List<Task> getTasksByCompleted(boolean completed) {
        return taskRepository.findByCompleted(completed);
    }

    public List<Task> getTasksByUser(User user) {
        return taskRepository.findByUser(user);
    }
}
