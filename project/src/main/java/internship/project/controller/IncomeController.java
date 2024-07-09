package internship.project.controller;

import internship.project.dto.IncomeDTO;
import internship.project.dto.IncomeResponse;
import internship.project.model.Income;
import internship.project.service.IncomeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incomes")
@RequiredArgsConstructor
public class IncomeController {

    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<IncomeDTO> saveIncome(@RequestBody @Valid IncomeDTO dto){
        IncomeDTO incomeDto = incomeService.saveIncome(dto);
        return new ResponseEntity<>(incomeDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<IncomeResponse> getAllIncomes(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "date", required = false) String field
    ){
        IncomeResponse incomes = incomeService.getAll(pageNo, pageSize, field);
        return new ResponseEntity<>(incomes, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Income> deleteIncome(@PathVariable int id){
        incomeService.deleteIncome(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping
    public ResponseEntity<IncomeDTO> updateExpense(@RequestBody @Valid IncomeDTO dto){
        IncomeDTO incomeDto = incomeService.updateIncome(dto);

        return  new ResponseEntity<>(incomeDto, HttpStatus.OK);
    }
}
