package internship.project.controller;

import internship.project.dto.ExpenseGroupDTO;
import internship.project.dto.IncomeGroupDTO;
import internship.project.dto.IncomeGroupResponse;
import internship.project.dto.IncomeResponse;
import internship.project.model.ExpenseGroup;
import internship.project.model.IncomeGroup;
import internship.project.service.IncomeGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/income_groups")
public class IncomeGroupController {
    private final IncomeGroupService incomeGroupService;

    @PostMapping
    public ResponseEntity<IncomeGroupDTO> saveIncomeGroup(@RequestBody @Valid IncomeGroupDTO dto){
        IncomeGroupDTO incomeGroupDto = incomeGroupService.saveExpenseGroup(dto);
        return new ResponseEntity<>(incomeGroupDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<IncomeGroupResponse> getAllIncomeGroups(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "date", required = false) String field
    ){
        IncomeGroupResponse incomes = incomeGroupService.getAll(pageNo, pageSize, field);
        return new ResponseEntity<>(incomes, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<IncomeGroup> deleteIncomeGroup(@PathVariable int id){
        incomeGroupService.deleteExpenseGroup(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping
    public ResponseEntity<IncomeGroupDTO> updateIncomeGroup(@RequestBody @Valid IncomeGroupDTO dto){
        IncomeGroupDTO incomeGroupDto = incomeGroupService.updateExpenseGroup(dto);

        return  new ResponseEntity<>(incomeGroupDto, HttpStatus.OK);
    }
}
