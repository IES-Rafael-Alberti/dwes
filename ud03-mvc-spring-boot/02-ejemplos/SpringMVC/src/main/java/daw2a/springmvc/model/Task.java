package daw2a.springmvc.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Usuario asociado a la tarea

    private boolean completed;

    public boolean isNotCompleted()
    {
        return !completed;
    }
    public boolean isCompleted()
    {
        return completed;
    }


}
