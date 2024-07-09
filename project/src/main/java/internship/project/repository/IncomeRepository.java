package internship.project.repository;


import internship.project.model.Expense;
import internship.project.model.Income;
import internship.project.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Integer> {
    List<Income> findByUserAndDateAfter(User user, LocalDate date);
    Page<Income> findByUser(User user, Pageable pageable);
}
