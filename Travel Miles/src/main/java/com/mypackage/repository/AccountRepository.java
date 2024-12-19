package com.mypackage.repository;


import com.mypackage.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {
	
	Account findByUsername(String username);
    boolean existsByUsername(String username);
}
