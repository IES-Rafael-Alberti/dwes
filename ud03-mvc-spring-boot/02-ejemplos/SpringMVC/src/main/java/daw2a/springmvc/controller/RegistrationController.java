package daw2a.springmvc.controller;

import daw2a.springmvc.model.Role;
import daw2a.springmvc.model.User;
import daw2a.springmvc.repository.RoleRepository;
import daw2a.springmvc.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Set;

@Controller
public class RegistrationController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationController(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            return "redirect:/register?error=exists"; // Usuario ya existe
        }

        // Asignar rol USER por defecto
        Role userRole = roleRepository.findByName("USER");
        user.setRoles(Set.of(userRole));
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Encriptar contraseña
        userRepository.save(user);

        return "redirect:/login?success";
    }
}