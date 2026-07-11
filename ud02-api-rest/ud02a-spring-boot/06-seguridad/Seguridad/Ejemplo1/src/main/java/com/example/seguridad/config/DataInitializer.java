package com.example.seguridad.config;

import com.example.seguridad.domain.Book;
import com.example.seguridad.repository.BookRepository;
import com.example.seguridad.repository.UserRepository;
import com.example.seguridad.service.CustomUserDetailsService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

  @Bean
  CommandLineRunner initData(BookRepository bookRepository,
                             UserRepository userRepository,
                             CustomUserDetailsService userService) {
    return args -> {
      if (userRepository.findByUsername("admin").isEmpty()) {
        userService.register("admin", "admin123", "ROLE_ADMIN");
      }
      if (userRepository.findByUsername("user").isEmpty()) {
        userService.register("user", "user1234", "ROLE_USER");
      }
      if (bookRepository.count() == 0) {
        bookRepository.save(new Book(null, "Clean Code", "Robert C. Martin"));
        bookRepository.save(new Book(null, "Effective Java", "Joshua Bloch"));
      }
    };
  }
}
