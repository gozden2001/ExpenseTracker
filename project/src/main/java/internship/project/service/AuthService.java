package internship.project.service;

import internship.project.dto.AuthResponseDTO;
import internship.project.dto.LoginDTO;
import internship.project.dto.RegisterDTO;
import internship.project.model.Role;
import internship.project.model.User;
import internship.project.repository.RoleRepository;
import internship.project.repository.UserRepository;
import internship.project.security.JWTGenerator;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.embedded.netty.NettyWebServer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTGenerator jwtGenerator;

    public ResponseEntity<String> register(RegisterDTO registerDto){
        if(userRepository.existsByUsername(registerDto.getUsername())){
            throw new EntityExistsException("Username is taken!");
        }

        User newUser = new User();
        newUser.setUsername(registerDto.getUsername());
        newUser.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        newUser.setEmail(registerDto.getEmail());
        newUser.setReminderFrequency("weekly");
        newUser.setAge(registerDto.getAge());
        newUser.setGender(registerDto.getGender());
        newUser.setCountry(registerDto.getCountry());

        Role role = roleRepository.findByName("ROLE_USER").get();
        newUser.setRole(role);

        userRepository.save(newUser);

        return new ResponseEntity<>("User registered! " + newUser.getRole().getName() + newUser.getRole().getId(), HttpStatus.OK);
    }

    public ResponseEntity<AuthResponseDTO> login(LoginDTO loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtGenerator.generateToken(authentication);
        return new ResponseEntity<>(new AuthResponseDTO(token), HttpStatus.OK);
    }
}
