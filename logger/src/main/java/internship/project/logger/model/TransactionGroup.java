package internship.project.logger.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Data
@Entity
@Table(name = "dim_transaction_group")
public class TransactionGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name = "transaction_type")
    private String transactionType;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
}
