package internship.project.logger.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "dim_event_type")
public class EventType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name = "event_type")
    private String eventType;
}
