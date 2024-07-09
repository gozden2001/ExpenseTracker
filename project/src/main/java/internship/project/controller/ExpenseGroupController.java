package internship.project.controller;

import internship.project.dto.ExpenseGroupDTO;
import internship.project.dto.ExpenseGroupResponse;
import internship.project.model.ExpenseGroup;
import internship.project.service.ExpenseGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/expense_groups")
public class ExpenseGroupController {
    private final ExpenseGroupService expenseGroupService;

    @PostMapping
    public ResponseEntity<ExpenseGroupDTO> saveExpenseGroup(@RequestBody @Valid ExpenseGroupDTO dto){
        ExpenseGroupDTO expenseGroupDto = expenseGroupService.saveExpenseGroup(dto);
        return new ResponseEntity<>(expenseGroupDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ExpenseGroupResponse> getAllExpenseGroups(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "date", required = false) String field
    ){
        ExpenseGroupResponse expenses = expenseGroupService.getAll(pageNo, pageSize, field);
        return new ResponseEntity<>(expenses, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ExpenseGroup> deleteExpenseGroup(@PathVariable int id){
        expenseGroupService.deleteExpenseGroup(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping
    public ResponseEntity<ExpenseGroupDTO> updateExpenseGroup(@RequestBody @Valid ExpenseGroupDTO dto){
        ExpenseGroupDTO expenseGroupDto = expenseGroupService.updateExpenseGroup(dto);

        return  new ResponseEntity<>(expenseGroupDto, HttpStatus.OK);
    }
}
