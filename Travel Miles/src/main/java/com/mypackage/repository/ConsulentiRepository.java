package com.mypackage.repository;


import com.mypackage.entity.Consulenti;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsulentiRepository extends JpaRepository<Consulenti, String> {
	Consulenti findByEmail(String email);
	Consulenti findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
