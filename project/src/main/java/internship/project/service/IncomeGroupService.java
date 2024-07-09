package internship.project.service;


import internship.project.dto.*;
import internship.project.model.Expense;
import internship.project.model.ExpenseGroup;
import internship.project.model.IncomeGroup;
import internship.project.repository.IncomeGroupRepository;
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
public class IncomeGroupService {
    private final MapStructMapper mapStructMapper;
    private final IncomeGroupRepository incomeGroupRepository;

    public IncomeGroupDTO saveExpenseGroup(IncomeGroupDTO dto) {
        IncomeGroup incomeGroup = mapStructMapper.incomeGroupDTOtoIncomeGroup(dto);
        incomeGroupRepository.save(incomeGroup);
        return  mapStructMapper.incomeGrouptoIncomeGroupDTO(incomeGroup);
    }

    public IncomeGroupResponse getAll(int pageNo, int pageSize, String field) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(field).descending());
        Page<IncomeGroup> incomeGroups = incomeGroupRepository.findAll(pageable);
        List<IncomeGroup> listOfIncomeGroups = incomeGroups.getContent();
        List<IncomeGroupDTO> dtos = mapStructMapper.incomeGroupListToIncomeGroupsDTOList(listOfIncomeGroups);
        IncomeGroupResponse response = new IncomeGroupResponse();
        response.setContent(dtos);
        response.setPageNo(incomeGroups.getNumber());
        response.setPageSize(incomeGroups.getSize());
        response.setTotalPages(incomeGroups.getTotalPages());
        response.setTotalElements(incomeGroups.getTotalElements());
        response.setLast(incomeGroups.isLast());
        response.setSortedBy(field);
        return response;
    }

    public void deleteExpenseGroup(int id) {
        incomeGroupRepository.delete(incomeGroupRepository.getReferenceById(id));
    }

    public IncomeGroupDTO updateExpenseGroup(IncomeGroupDTO dto) {
        Optional<IncomeGroup> incomeGroup = Optional.of(incomeGroupRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Income Group not found")));

        incomeGroup.get().setName(dto.getName());
        incomeGroup.get().setDescription(dto.getDescription());
        incomeGroupRepository.save(incomeGroup.get());

        return  mapStructMapper.incomeGrouptoIncomeGroupDTO(incomeGroup.get());
    }
}
