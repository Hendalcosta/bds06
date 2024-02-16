package com.devsuperior.movieflix.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.movieflix.dto.ReviewDTO;
import com.devsuperior.movieflix.entities.Movie;
import com.devsuperior.movieflix.entities.Review;
import com.devsuperior.movieflix.entities.User;
import com.devsuperior.movieflix.repositories.MovieRepository;
import com.devsuperior.movieflix.repositories.ReviewRepository;
import com.devsuperior.movieflix.services.exceptions.ResourceNotFoundException;

@Service
public class ReviewService {
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	private ReviewRepository reviewRepository;
	
	@Autowired
	private MovieRepository movieRepository;
	
	
	@Transactional(readOnly = true)
	public List<ReviewDTO> FindReviews(Long id) {
		
		Optional<Movie> obj = movieRepository.findById(id);
		obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
		
		User user = authService.authenticated();
		List<Review> list = reviewRepository.find(user, id);
		return list.stream().map(x -> new ReviewDTO(x)).collect(Collectors.toList());
	}
	
	@PreAuthorize("hasRole('MEMBER')")
	@Transactional
	public ReviewDTO insert(ReviewDTO dto) {
		
		Review entity = new Review();
		User user = authService.authenticated();
		Optional<Movie> obj = movieRepository.findById(dto.getMovieId());
		Movie movie = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
		
		entity.setText(dto.getText());
		entity.setMovie(movie);
		entity.setUser(user);
		
		entity = reviewRepository.save(entity);
		return new ReviewDTO(entity);
	}
	

}
