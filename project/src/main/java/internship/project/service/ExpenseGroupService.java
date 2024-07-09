package internship.project.service;

import internship.project.dto.*;
import internship.project.model.Expense;
import internship.project.model.ExpenseGroup;
import internship.project.repository.ExpenseGroupRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExpenseGroupService {
    private final MapStructMapper mapStructMapper;
    private final ExpenseGroupRepository expenseGroupRepository;

    public ExpenseGroupDTO saveExpenseGroup(ExpenseGroupDTO dto) {
        ExpenseGroup expenseGroup = mapStructMapper.expenseGroupDTOtoExpenseGroup(dto);
        expenseGroupRepository.save(expenseGroup);
        return  mapStructMapper.expenseGrouptoExpenseGroupDTO(expenseGroup);
    }

    public ExpenseGroupResponse getAll(int pageNo, int pageSize, String field) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(field).descending());
        Page<ExpenseGroup> expenseGroups = expenseGroupRepository.findAll(pageable);
        List<ExpenseGroup> listOfExpenseGroups = expenseGroups.getContent();
        List<ExpenseGroupDTO> dtos = mapStructMapper.expenseGroupListToExpenseGroupDTOList(listOfExpenseGroups);
        ExpenseGroupResponse response = new ExpenseGroupResponse();
        response.setContent(dtos);
        response.setPageNo(expenseGroups.getNumber());
        response.setPageSize(expenseGroups.getSize());
        response.setTotalPages(expenseGroups.getTotalPages());
        response.setTotalElements(expenseGroups.getTotalElements());
        response.setLast(expenseGroups.isLast());
        response.setSortedBy(field);
        return response;
    }

    public void deleteExpenseGroup(int id) {
        expenseGroupRepository.delete(expenseGroupRepository.getReferenceById(id));
    }

    public ExpenseGroupDTO updateExpenseGroup(ExpenseGroupDTO dto) {
        Optional<ExpenseGroup> expenseGroup = Optional.of(expenseGroupRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Expense Group not found")));

        expenseGroup.get().setName(dto.getName());
        expenseGroup.get().setDescription(dto.getDescription());
        expenseGroupRepository.save(expenseGroup.get());

        return  mapStructMapper.expenseGrouptoExpenseGroupDTO(expenseGroup.get());
    }
}
