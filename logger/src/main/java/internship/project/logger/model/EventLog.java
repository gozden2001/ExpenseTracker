package internship.project.logger.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@Table(name = "fact_event_log")
public class EventLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name = "description")
    private String description;
    @Column(name = "date")
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name="event_type_id")
    private EventType eventType;

    @ManyToOne
    @JoinColumn(name="transaction_group_id")
    private TransactionGroup transactionGroup;
}
