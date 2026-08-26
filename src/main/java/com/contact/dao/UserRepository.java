package com.contact.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.contact.entities.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	// Create Custom Finder Query JPA
	@Query("select u from User u where u.email = :email")
	public User getUserByEmail(@Param("email") String email);
}
