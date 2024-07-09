package internship.project.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "reminder_frequency")
    private String reminderFrequency;

    @Column(name = "age")
    private int age;
    @Column(name = "gender")
    private String gender;
    @Column(name = "country")
    private String country;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<Reminder> reminders;
    @OneToMany(mappedBy = "user")
    private List<Income> incomes;
    @OneToMany(mappedBy = "user")
    private List<Expense> expenses;
}
