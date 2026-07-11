package com.example.seguridad.controller;

import com.example.seguridad.domain.Book;
import com.example.seguridad.service.BookService;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
public class BookController {

  private final BookService bookService;

  public BookController(BookService bookService) {
    this.bookService = bookService;
  }

  @GetMapping
  public List<Book> findAll() {
    return bookService.findAll();
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping
  public Book create(@RequestBody Book book) {
    return bookService.save(book);
  }
}
