package com.mypackage.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@SuppressWarnings("serial")
@Entity
@Table(name = "Utenti")
public class Utenti implements Serializable {

    @Id
    @Column(name = "USERNAME", nullable = false, length = 50)
    private String username;

    @Column(name = "NOME", nullable = false, length = 50)
    private String nome;

    @Column(name = "COGNOME", nullable = false, length = 50)
    private String cognome;

    @Column(name = "NICKNAME", length = 50)
    private String nickname;

    @Column(name = "EMAIL", nullable = false, length = 100)
    private String email;

    
    @OneToOne //Relzione uno a uno con account
    @JoinColumn(name = "USERNAME", referencedColumnName = "USERNAME", insertable = false, updatable = false)
    private Account account;

   
    public Utenti() {}

    public Utenti(String username, String nome, String cognome, String nickname, String email) {
        this.username = username;
        this.nome = nome;
        this.cognome = cognome;
        this.nickname = nickname;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "Utenti{" +
                "username='" + username + '\'' +
                ", nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", account=" + account +
                '}';
    }
}
