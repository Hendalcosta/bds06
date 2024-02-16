package com.devsuperior.movieflix.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.movieflix.entities.User;
import com.devsuperior.movieflix.repositories.UserRepository;
import com.devsuperior.movieflix.services.exceptions.ForbiddenException;
import com.devsuperior.movieflix.services.exceptions.UnauthorizedException;

@Service
public class AuthService {
	
	@Autowired
	private UserRepository repository;
	
	@Transactional(readOnly = true)
	public User authenticated () {
		try {
			String username = SecurityContextHolder.getContext().getAuthentication().getName(); //obtém o nome do usuário que já foi reconhecido pelo spring security
			return repository.findByEmail(username);
		}
		catch (Exception e) { 
			throw new UnauthorizedException("Invalid User");
		}
	}
	
	public void validateSelfOrAdmin(Long userId) { //método para verificar se o id passado é do usuário logado ou de um admin
		User user = authenticated();
		if (!user.getId().equals(userId) && !user.hasRole("ROLE_MEMBER")) {
			throw new ForbiddenException("Access denied");
		}
	}
	
	
}
