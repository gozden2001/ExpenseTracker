package internship.project.repository;

import internship.project.model.Role;
import internship.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    List<User> findByRoleAndReminderFrequency(Role role, String reminderFrequency);
}
