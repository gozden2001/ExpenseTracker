package internship.project.service;

import internship.project.model.Role;
import internship.project.model.User;
import internship.project.repository.RoleRepository;
import internship.project.repository.UserRepository;
import internship.project.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserUtils userUtils;


    public ResponseEntity<String> upgradeAccount() {
        User currentUser = userUtils.GetCurrentUser();
        Role role = roleRepository.findByName("ROLE_PREMIUM").get();
        currentUser.setRole(role);
        userRepository.save(currentUser);

        return new ResponseEntity<>("User upgraded to Premium Status", HttpStatus.OK);
    }
}
