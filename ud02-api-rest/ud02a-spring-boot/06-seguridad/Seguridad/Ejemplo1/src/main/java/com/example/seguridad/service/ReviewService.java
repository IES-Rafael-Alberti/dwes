package com.example.seguridad.service;

import com.example.seguridad.domain.Book;
import com.example.seguridad.domain.Review;
import com.example.seguridad.domain.User;
import com.example.seguridad.repository.ReviewRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

  private final ReviewRepository reviewRepository;

  public ReviewService(ReviewRepository reviewRepository) {
    this.reviewRepository = reviewRepository;
  }

  public Review create(String content, Book book, User user) {
    Review review = new Review();
    review.setContent(content);
    review.setBook(book);
    review.setUser(user);
    return reviewRepository.save(review);
  }

  public List<Review> findByBook(Long bookId) {
    return reviewRepository.findByBookId(bookId);
  }

  public Optional<Review> findById(Long id) {
    return reviewRepository.findById(id);
  }
}
