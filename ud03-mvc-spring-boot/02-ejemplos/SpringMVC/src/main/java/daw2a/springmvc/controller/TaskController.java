package daw2a.springmvc.controller;

import daw2a.springmvc.form.TaskForm;
import daw2a.springmvc.model.Task;
import daw2a.springmvc.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class TaskController {
    private final TaskService service;
    public TaskController(TaskService service) { this.service = service; }

    @GetMapping("/") String home() { return "redirect:/tasks"; }
    @GetMapping("/login") String login() { return "login"; }
    @GetMapping("/tasks") String list(Model model, Authentication auth) {
        model.addAttribute("tasks", service.listOwnedBy(auth.getName())); return "tasks/list";
    }
    @GetMapping("/tasks/new") String newForm(Model model) {
        model.addAttribute("taskForm", new TaskForm()); return "tasks/new";
    }
    @PostMapping("/tasks") String create(@Valid @ModelAttribute TaskForm taskForm, BindingResult errors, Authentication auth) {
        if (errors.hasErrors()) return "tasks/new";
        service.create(taskForm, auth.getName()); return "redirect:/tasks";
    }
    @GetMapping("/tasks/{id}/edit") String editForm(@PathVariable Long id, Model model, Authentication auth) {
        Task task = service.getOwned(id, auth.getName());
        TaskForm form = new TaskForm(); form.setDescription(task.getDescription());
        model.addAttribute("taskForm", form); model.addAttribute("taskId", id); return "tasks/edit";
    }
    @PostMapping("/tasks/{id}") String update(@PathVariable Long id, @Valid @ModelAttribute TaskForm taskForm,
                                               BindingResult errors, Model model, Authentication auth) {
        if (errors.hasErrors()) { model.addAttribute("taskId", id); return "tasks/edit"; }
        service.update(id, taskForm, auth.getName()); return "redirect:/tasks";
    }
    @PostMapping("/tasks/{id}/toggle") String toggle(@PathVariable Long id, Authentication auth) {
        service.toggle(id, auth.getName()); return "redirect:/tasks";
    }
    @PostMapping("/tasks/{id}/delete") String delete(@PathVariable Long id, Authentication auth) {
        service.delete(id, auth.getName()); return "redirect:/tasks";
    }
}
