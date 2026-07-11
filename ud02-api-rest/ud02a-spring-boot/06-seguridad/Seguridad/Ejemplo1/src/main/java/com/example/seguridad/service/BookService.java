package com.example.seguridad.service;

import com.example.seguridad.domain.Book;
import com.example.seguridad.repository.BookRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class BookService {

  private final BookRepository bookRepository;

  public BookService(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
  }

  public List<Book> findAll() {
    return bookRepository.findAll();
  }

  public Optional<Book> findById(Long id) {
    return bookRepository.findById(id);
  }

  public Book save(Book book) {
    return bookRepository.save(book);
  }
}
