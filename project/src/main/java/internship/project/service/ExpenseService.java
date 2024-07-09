package internship.project.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import internship.project.dto.ExpenseDTO;
import internship.project.dto.ExpenseResponse;
import internship.project.dto.MapStructMapper;
import internship.project.dto.TransactionLogDTO;
import internship.project.model.Expense;
import internship.project.model.ExpenseGroup;
import internship.project.model.User;
import internship.project.repository.ExpenseGroupRepository;
import internship.project.repository.ExpenseRepository;
import internship.project.repository.UserRepository;
import internship.project.utils.UserUtils;
import jakarta.persistence.EntityNotFoundException;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final MapStructMapper mapStructMapper;
    private final ExpenseRepository expenseRepository;
    private final ExpenseGroupRepository expenseGroupRepository;
    private final UserUtils userUtils;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic = "transaction_logs";
    private final ObjectMapper objectMapper;

    public ExpenseDTO saveExpense(ExpenseDTO dto){
        Optional<ExpenseGroup> expenseGroup = Optional.of(expenseGroupRepository.findById(dto.getExpenseGroupId())
                .orElseThrow(() -> new EntityNotFoundException("Expense Group not found")));

        Expense expense = mapStructMapper.expenseDTOToExpense(dto);
        expense.setExpenseGroup(expenseGroup.get());
        expense.setDate(LocalDate.now());
        expense.setUser(userUtils.GetCurrentUser());



        expenseRepository.save(expense);
        sendMessage(new TransactionLogDTO(expense.getDescription(), LocalDateTime.now(), "save",
                expense.getUser().getUsername(), expense.getUser().getRole().getName() ,expenseGroup.get().getName(),
                "expense" ,expenseGroup.get().getDescription()));
        return  mapStructMapper.expenseToExpenseDTO(expense);
    }

    public ExpenseResponse getAll(int pageNo, int pageSize, String field) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(field).descending());
        Page<Expense> expenses = expenseRepository.findByUser(userUtils.GetCurrentUser(), pageable);
        List<Expense> listOfExpenses = expenses.getContent();
        List<ExpenseDTO> dtos = mapStructMapper.expenseListToExpenseDTOList(listOfExpenses);
        ExpenseResponse response = new ExpenseResponse();
        response.setContent(dtos);
        response.setPageNo(expenses.getNumber());
        response.setPageSize(expenses.getSize());
        response.setTotalPages(expenses.getTotalPages());
        response.setTotalElements(expenses.getTotalElements());
        response.setLast(expenses.isLast());
        response.setSortedBy(field);
        return response;
    }


    public void deleteExpense(int id) {
        Expense expenseToDelete = expenseRepository.getReferenceById(id);

        expenseRepository.delete(expenseToDelete);

        sendMessage(new TransactionLogDTO(expenseToDelete.getDescription(), LocalDateTime.now(), "delete",
                expenseToDelete.getUser().getUsername(), expenseToDelete.getUser().getRole().getName() ,expenseToDelete.getExpenseGroup().getName(),
                "expense", expenseToDelete.getExpenseGroup().getDescription()));
    }

    public ExpenseDTO updateExpense(ExpenseDTO dto) {

        Optional<ExpenseGroup> expenseGroup = Optional.of(expenseGroupRepository.findById(dto.getExpenseGroupId())
                .orElseThrow(() -> new EntityNotFoundException("Expense Group not found")));
        Optional<Expense> expense = Optional.of(expenseRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Expense not found")));

        expense.get().setDescription(dto.getDescription());
        expense.get().setAmount(dto.getAmount());
        expense.get().setExpenseGroup(expenseGroup.get());
        expense.get().setUser(userUtils.GetCurrentUser());
        expenseRepository.save(expense.get());

        sendMessage(new TransactionLogDTO(expense.get().getDescription(), LocalDateTime.now(), "update",
                expense.get().getUser().getUsername(), expense.get().getUser().getRole().getName(), expenseGroup.get().getName(),
                "expense", expenseGroup.get().getDescription()));

        return mapStructMapper.expenseToExpenseDTO(expense.get());
    }

    private void sendMessage(TransactionLogDTO dto) {
        try {
            String message = objectMapper.writeValueAsString(dto);
            kafkaTemplate.send(topic, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
