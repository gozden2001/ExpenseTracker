package internship.project.dto;

import internship.project.model.Expense;
import internship.project.model.ExpenseGroup;
import internship.project.model.Income;
import internship.project.model.IncomeGroup;

import java.util.List;

public interface MapStructMapper {
    Expense expenseDTOToExpense(ExpenseDTO expenseDTO);
    ExpenseDTO expenseToExpenseDTO(Expense expense);
    List<ExpenseDTO> expenseListToExpenseDTOList(List<Expense> expenses);
    List<IncomeDTO> incomeListToIncomeDTOList(List<Income> incomes);
    List<ExpenseGroupDTO> expenseGroupListToExpenseGroupDTOList(List<ExpenseGroup> expenseGroups);
    List<IncomeGroupDTO> incomeGroupListToIncomeGroupsDTOList(List<IncomeGroup> incomeGroups);
    Income incomeDTOToIncome(IncomeDTO incomeDTO);
    IncomeDTO incomeToIncomeDTO(Income income);
    ExpenseGroup expenseGroupDTOtoExpenseGroup(ExpenseGroupDTO expenseGroupDTO);
    ExpenseGroupDTO expenseGrouptoExpenseGroupDTO(ExpenseGroup expenseGroup);
    IncomeGroup incomeGroupDTOtoIncomeGroup(IncomeGroupDTO incomeGroupDTO);
    IncomeGroupDTO incomeGrouptoIncomeGroupDTO(IncomeGroup incomeGroup);
}
