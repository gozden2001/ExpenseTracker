package internship.project.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Entity
@Data
@Table(name = "expense_group")
public class ExpenseGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;
    @Column(name="description")
    private String description;
    @Column(name="name")
    private String name;

    @OneToMany(mappedBy = "expenseGroup")
    private List<Expense> expenses;
}
