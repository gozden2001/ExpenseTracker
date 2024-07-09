package internship.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginDTO {
    @NotBlank(message = "Please provide a username")
    private String username;
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
}
