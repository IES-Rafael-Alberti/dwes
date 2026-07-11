package daw2a.springmvc.controller;

import daw2a.springmvc.model.Task;
import daw2a.springmvc.model.User;
import daw2a.springmvc.repository.UserRepository;
import daw2a.springmvc.service.TaskService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@Controller
public class TaskController {
    private final TaskService taskService;
    private final UserRepository userRepository;
    public TaskController(TaskService taskService, UserRepository userRepository) {
        this.taskService = taskService;
        this.userRepository = userRepository;
    }

    // Listar tareas
    @GetMapping("/tasks")
    public String listTasks(Model model)
    {
        User currentUser = getCurrentUser();
        model.addAttribute("tasks",taskService.getTasksByUser(currentUser));
        model.addAttribute("keyword", ""); // Agregar el atributo keyword vacío
        return "tasks";
    }

    // Mostrar formulario para añadir tarea
    @GetMapping("/tasks/new")
    public String showAddTaskForm(Model model)
    {
        model.addAttribute("task",new Task());
        return "add-task";
    }

    // Añadir tarea
    @PostMapping("/tasks")
    public String addTask(@ModelAttribute Task task) {
        User currentUser = getCurrentUser();
        task.setUser(currentUser);
        taskService.saveTask(task);
        return "redirect:/tasks";
    }

    // Marcar como completada o pendiente
    @PostMapping("/tasks/{id}/statechange")
    public String changeTaskState(@PathVariable Long id)
    {
        taskService.changeTaskState(id);
        return "redirect:/tasks";
    }

    // Borrar una tarea
    @PostMapping("/tasks/{id}/delete")
    public String deleteTask(@PathVariable Long id)
    {
        taskService.deleteTask(id);
        return "redirect:/tasks";
    }

    @PostMapping("/tasks/action")
    public String handleTaskActions(@RequestParam List<Long> taskIds, @RequestParam String actionType) {
        if ("delete".equals(actionType)) {
            taskService.deleteTasksByIds(taskIds);
        } else if ("toggle".equals(actionType)) {
            taskService.changeStatusForTasks(taskIds);
        }
        return "redirect:/tasks";
    }

    @GetMapping("/tasks/search")
    public String searchTasks(@RequestParam(required = false) String keyword, Model model) {
        List<Task> tasks;
        if (keyword != null && !keyword.isEmpty()) {
            tasks = taskService.searchTasks(keyword);
        } else {
            tasks = taskService.getAllTasks();
        }
        model.addAttribute("tasks", tasks);
        model.addAttribute("keyword", keyword != null ? keyword : ""); // Mantener keyword en el modelo
        return "tasks";
    }

    @GetMapping("/tasks/filter")
    public String filterTasks(@RequestParam String status, Model model) {
        List<Task> tasks;
        if ("completed".equalsIgnoreCase(status)) {
            tasks = taskService.getTasksByCompleted(true);
        } else if ("pending".equalsIgnoreCase(status)) {
            tasks = taskService.getTasksByCompleted(false);
        } else {
            tasks = taskService.getAllTasks();
        }
        model.addAttribute("tasks", tasks);
        model.addAttribute("keyword", ""); // Mantener keyword vacío para esta vista
        return "tasks";
    }

    // Método para obtener el usuario autenticado
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Obtener el nombre de usuario del contexto de seguridad
        return userRepository.findByUsername(username); // Buscar el usuario en la base de datos
    }
}
