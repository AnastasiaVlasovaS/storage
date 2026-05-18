package com.example.cloud.storage.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String login;

    @Column(nullable = false)
    private String password;

    private String name;
    private String email;

    @Column(unique = true)
    private String token;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<FileRecord> files;

    // Конструкторы
    public User() {}

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public List<FileRecord> getFiles() { return files; }
    public void setFiles(List<FileRecord> files) { this.files = files; }
}