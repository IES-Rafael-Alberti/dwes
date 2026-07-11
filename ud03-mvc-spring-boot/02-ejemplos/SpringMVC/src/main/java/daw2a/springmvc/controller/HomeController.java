package daw2a.springmvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Gestión de Tareas");
        model.addAttribute("description", "Bienvenido a la aplicación de gestión de tareas. Aquí puedes organizar tus tareas, marcarlas como completadas, eliminarlas y mucho más.");
        return "index";
    }

    @GetMapping(value = "/about", produces = "text/html; charset=UTF-8")
    public String about(Model model) {
        model.addAttribute("title", "Acerca de esta Aplicación");
        model.addAttribute("description", "Esta aplicación permite gestionar tareas fácilmente, incluyendo opciones para buscar, filtrar, marcar como completadas o pendientes, y eliminarlas. Está desarrollada utilizando Spring MVC y Mustache.");
        return "about";
    }
}