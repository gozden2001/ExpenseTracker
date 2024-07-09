package internship.project.controller;

import internship.project.dto.AuthResponseDTO;
import internship.project.dto.LoginDTO;
import internship.project.dto.RegisterDTO;
import internship.project.model.Role;
import internship.project.model.User;
import internship.project.repository.RoleRepository;
import internship.project.repository.UserRepository;
import internship.project.service.AuthService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import jakarta.validation.Valid;
import java.sql.Connection;
import java.sql.SQLException;

@Validated
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final  DataSource dataSource;

    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterDTO registerDto){
        return authService.register(registerDto);
    }

    @PostMapping("login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody @Valid LoginDTO loginDto){
        return authService.login(loginDto);
    }

    @GetMapping("test")
    public String HellWorld() {
        return "Greetings from Spring Boot!";
    }

    @GetMapping("/testdb")
    public ResponseEntity<String> testDb() {
        try (Connection connection = dataSource.getConnection()) {
            return ResponseEntity.ok("Database connection successful!");
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database connection failed: " + e.getMessage());
        }
    }
}
