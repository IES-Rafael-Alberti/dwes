package daw2a.springmvc.model;

import jakarta.persistence.*;

@Entity
@Table(name = "app_user")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 80)
    private String username;
    @Column(nullable = false)
    private String password;

    protected User() {}
    public User(String username, String password) { this.username = username; this.password = password; }
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}
