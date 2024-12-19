package com.mypackage.repository;


import com.mypackage.entity.Utenti;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UtentiRepository extends JpaRepository<Utenti, String> {
	Utenti findByEmail(String email);
	Utenti findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
