package internship.project.controller;

import internship.project.dto.ExpenseDTO;
import internship.project.dto.ExpenseResponse;
import internship.project.model.Expense;
import internship.project.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseDTO> saveExpense(@RequestBody @Valid ExpenseDTO dto){
        ExpenseDTO expenseDto = expenseService.saveExpense(dto);
        return new ResponseEntity<>(expenseDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ExpenseResponse> getAllExpenses(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "date", required = false) String field
    ){
        ExpenseResponse expenses = expenseService.getAll(pageNo, pageSize, field);
        return new ResponseEntity<>(expenses, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Expense> deleteExpense(@PathVariable int id){
        expenseService.deleteExpense(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping
    public ResponseEntity<ExpenseDTO> updateExpense(@RequestBody @Valid ExpenseDTO dto){
        ExpenseDTO expenseDto = expenseService.updateExpense(dto);

        return  new ResponseEntity<>(expenseDto, HttpStatus.OK);
    }
}
