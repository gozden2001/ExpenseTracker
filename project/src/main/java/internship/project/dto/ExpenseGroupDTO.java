package internship.project.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ExpenseGroupDTO {
    private int id;
    @NotBlank(message = "Please provide a name")
    private String name;
    @NotBlank(message = "Please provide a description")
    private String description;
}
