package com.contact.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.contact.entities.Contact;
import com.contact.entities.User;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
	@Query("select c from Contact c where c.user.id = :id")
	public Page<Contact> getContactById(@Param("id") int id, Pageable pageable);
}
