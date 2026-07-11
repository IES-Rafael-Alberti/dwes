package com.example.seguridad.controller;

import com.example.seguridad.domain.Book;
import com.example.seguridad.domain.Review;
import com.example.seguridad.domain.User;
import com.example.seguridad.dto.CreateReviewRequest;
import com.example.seguridad.repository.UserRepository;
import com.example.seguridad.service.BookService;
import com.example.seguridad.service.ReviewService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

  private final ReviewService reviewService;
  private final BookService bookService;
  private final UserRepository userRepository;

  public ReviewController(ReviewService reviewService, BookService bookService, UserRepository userRepository) {
    this.reviewService = reviewService;
    this.bookService = bookService;
    this.userRepository = userRepository;
  }

  @PreAuthorize("hasRole('USER')")
  @PostMapping
  public Review create(@RequestBody CreateReviewRequest request, Authentication authentication) {
    Book book = bookService.findById(request.bookId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
    User user = userRepository.findByUsername(authentication.getName())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    return reviewService.create(request.content(), book, user);
  }

  @GetMapping("/book/{bookId}")
  public List<Review> findByBook(@PathVariable Long bookId) {
    return reviewService.findByBook(bookId);
  }
}
