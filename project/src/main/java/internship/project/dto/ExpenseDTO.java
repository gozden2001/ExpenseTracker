package internship.project.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ExpenseDTO {
    private int id;
    @NotBlank(message = "Please provide a description")
    private String description;
    @NotBlank(message = "Please provide an amount")
    private int amount;
    private int expenseGroupId;
}
