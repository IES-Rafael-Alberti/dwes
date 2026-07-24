package daw2a.springmvc.model;

import jakarta.persistence.*;

@Entity
public class Task {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 200)
    private String description;
    @Column(nullable = false)
    private boolean completed;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    protected Task() {}
    public Task(String description, User owner) { this.description = description; this.owner = owner; }
    public Long getId() { return id; }
    public String getDescription() { return description; }
    public boolean isCompleted() { return completed; }
    public void rename(String description) { this.description = description; }
    public void toggle() { completed = !completed; }
}
