package internship.project.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "income_group")
@Data
public class IncomeGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="description")
    private String description;
    @Column(name="name")
    private String name;

    @OneToMany(mappedBy = "incomeGroup")
    private List<Income> incomes;
}
