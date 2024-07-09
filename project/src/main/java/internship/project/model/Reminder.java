package internship.project.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "reminders")
@Data
public class Reminder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "reminder_day")
    private LocalDate reminderDay;
    @Column(name = "active")
    private boolean active;
    @Column(name = "type")
    private String type;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
}
