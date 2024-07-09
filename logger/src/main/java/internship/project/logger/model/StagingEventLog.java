package internship.project.logger.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.springframework.boot.context.properties.bind.Name;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@Table(name = "staging_event_log")
public class StagingEventLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name = "description")
    private String description;
    @Column(name = "date")
    private LocalDateTime date;
    @Column(name = "username")
    private String username;
    @Column(name = "role")
    private String role;
    @Column(name = "event_type")
    private String eventType;
    @Column(name = "transaction_type")
    private String transactionType;
    @Column(name = "name")
    private String name;
}
