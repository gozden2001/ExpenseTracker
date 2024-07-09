package internship.project.logger.repository;

import internship.project.logger.model.TransactionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionGroupRepository extends JpaRepository<TransactionGroup, Integer> {
    Optional<TransactionGroup> findByName(String name);
}
