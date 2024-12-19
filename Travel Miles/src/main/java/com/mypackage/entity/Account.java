package com.mypackage.entity;

import jakarta.persistence.*;
import java.io.Serializable;


@SuppressWarnings("serial")
@Entity
@Table(name = "Account")
public class Account implements Serializable {

    @Id
    @Column(name = "USERNAME", nullable = false, length = 50)
    private String username;

    @Column(name = "PASSWORD", nullable = false, length = 255)
    private String password;
    
    public Account() {}

    public Account(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    
    
    @Override
    public String toString() {
        return "Account{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
