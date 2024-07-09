package internship.project.repository;

import internship.project.model.Income;
import internship.project.model.Reminder;
import internship.project.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Integer> {
    Page<Reminder> findByUser(User user, Pageable pageable);
}
