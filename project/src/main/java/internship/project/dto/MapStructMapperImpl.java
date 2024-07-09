package internship.project.dto;

import internship.project.model.Expense;
import internship.project.model.ExpenseGroup;
import internship.project.model.Income;
import internship.project.model.IncomeGroup;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MapStructMapperImpl implements MapStructMapper{
    @Override
    public Expense expenseDTOToExpense(ExpenseDTO expenseDTO) {
        if (expenseDTO == null) return null;

        Expense expense = new Expense();

        expense.setAmount(expenseDTO.getAmount());
        expense.setDescription(expenseDTO.getDescription());
        return expense;
    }

    @Override
    public ExpenseDTO expenseToExpenseDTO(Expense expense) {
        if(expense == null) return null;

        ExpenseDTO dto = new ExpenseDTO();
        dto.setId(expense.getId());
        dto.setAmount(expense.getAmount());
        dto.setDescription(expense.getDescription());
        dto.setExpenseGroupId(expense.getExpenseGroup().getId());

        return dto;
    }

    @Override
    public List<ExpenseDTO> expenseListToExpenseDTOList(List<Expense> expenses) {
        return expenses.stream().map(this::expenseToExpenseDTO).collect(Collectors.toList());
    }

    @Override
    public List<IncomeDTO> incomeListToIncomeDTOList(List<Income> incomes) {
        return incomes.stream().map(this::incomeToIncomeDTO).collect(Collectors.toList());
    }

    @Override
    public List<ExpenseGroupDTO> expenseGroupListToExpenseGroupDTOList(List<ExpenseGroup> expenseGroups) {
        return expenseGroups.stream().map(this::expenseGrouptoExpenseGroupDTO).collect(Collectors.toList());
    }

    @Override
    public List<IncomeGroupDTO> incomeGroupListToIncomeGroupsDTOList(List<IncomeGroup> incomeGroups) {
        return incomeGroups.stream().map(this::incomeGrouptoIncomeGroupDTO).collect(Collectors.toList());
    }

    @Override
    public Income incomeDTOToIncome(IncomeDTO incomeDTO) {
        if (incomeDTO == null) return null;

        Income income = new Income();

        income.setAmount(incomeDTO.getAmount());
        income.setDescription(incomeDTO.getDescription());
        return income;
    }

    @Override
    public IncomeDTO incomeToIncomeDTO(Income income) {
        if(income == null) return null;

        IncomeDTO dto = new IncomeDTO();

        dto.setId(income.getId());
        dto.setAmount(income.getAmount());
        dto.setDescription(income.getDescription());
        dto.setIncomeGroupId(income.getIncomeGroup().getId());

        return dto;
    }

    @Override
    public ExpenseGroup expenseGroupDTOtoExpenseGroup(ExpenseGroupDTO expenseGroupDTO) {
        if(expenseGroupDTO == null) return null;

        ExpenseGroup expenseGroup = new ExpenseGroup();
        expenseGroup.setDescription(expenseGroupDTO.getDescription());
        expenseGroup.setName(expenseGroupDTO.getName());

        return expenseGroup;
    }

    @Override
    public ExpenseGroupDTO expenseGrouptoExpenseGroupDTO(ExpenseGroup expenseGroup) {
        if(expenseGroup == null) return null;

        ExpenseGroupDTO expenseGroupDTO = new ExpenseGroupDTO();
        expenseGroupDTO.setId(expenseGroup.getId());
        expenseGroupDTO.setName(expenseGroup.getName());
        expenseGroupDTO.setDescription(expenseGroup.getDescription());

        return expenseGroupDTO;
    }

    @Override
    public IncomeGroup incomeGroupDTOtoIncomeGroup(IncomeGroupDTO incomeGroupDTO) {
        if(incomeGroupDTO==null) return null;

        IncomeGroup incomeGroup = new IncomeGroup();
        incomeGroup.setDescription(incomeGroupDTO.getDescription());
        incomeGroup.setName(incomeGroupDTO.getName());

        return incomeGroup;
    }

    @Override
    public IncomeGroupDTO incomeGrouptoIncomeGroupDTO(IncomeGroup incomeGroup) {
        if(incomeGroup==null) return null;

        IncomeGroupDTO incomeGroupDTO = new IncomeGroupDTO();
        incomeGroupDTO.setDescription(incomeGroup.getDescription());
        incomeGroupDTO.setId(incomeGroup.getId());
        incomeGroupDTO.setName(incomeGroup.getName());

        return incomeGroupDTO;
    }
}
