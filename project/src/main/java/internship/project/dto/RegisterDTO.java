package internship.project.dto;

import internship.project.validation.CountryValidation;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterDTO {

    @NotBlank(message = "Please provide a username")
    private String username;
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
    @Email(message = "Please provide a valid email address")
    private String email;
    @Min(18)
    @Max(110)
    private int age;
    @NotBlank(message = "Please provide a gender")
    @Pattern(regexp = "^(Male|Female|Other)$")
    private String gender;
    @CountryValidation(message = "Country does not exist!")
    private String country;
}
