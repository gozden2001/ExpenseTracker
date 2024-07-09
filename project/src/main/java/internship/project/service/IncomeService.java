package internship.project.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import internship.project.dto.*;
import internship.project.model.*;
import internship.project.repository.*;
import internship.project.utils.UserUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final IncomeRepository incomeRepository;
    private final IncomeGroupRepository incomeGroupRepository;
    private final MapStructMapper mapStructMapper;
    private final UserUtils userUtils;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic = "transaction_logs";
    private final ObjectMapper objectMapper;

    public IncomeDTO saveIncome(IncomeDTO dto){
        Optional<IncomeGroup> incomeGroup = Optional.of(incomeGroupRepository.findById(dto.getIncomeGroupId())
                .orElseThrow(() -> new EntityNotFoundException("Income Group not found")));

        Income income = mapStructMapper.incomeDTOToIncome(dto);
        income.setIncomeGroup(incomeGroup.get());
        income.setDate(LocalDate.now());
        income.setUser(userUtils.GetCurrentUser());

        incomeRepository.save(income);

        sendMessage(new TransactionLogDTO(income.getDescription(), LocalDateTime.now(), "save",
                income.getUser().getUsername(), income.getUser().getRole().getName(), incomeGroup.get().getName(),
                "income", incomeGroup.get().getDescription()));
        return mapStructMapper.incomeToIncomeDTO(income);
    }

    public IncomeResponse getAll(int pageNo, int pageSize, String field) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(field).descending());
        Page<Income> incomes = incomeRepository.findByUser(userUtils.GetCurrentUser(),pageable);
        List<Income> listOfIncomes = incomes.getContent();
        List<IncomeDTO> dtos = mapStructMapper.incomeListToIncomeDTOList(listOfIncomes);
        IncomeResponse response = new IncomeResponse();
        response.setContent(dtos);
        response.setPageNo(incomes.getNumber());
        response.setPageSize(incomes.getSize());
        response.setTotalPages(incomes.getTotalPages());
        response.setTotalElements(incomes.getTotalElements());
        response.setLast(incomes.isLast());
        response.setSortedBy(field);
        return response;
    }

    public void deleteIncome(int id) {
        Optional<Income> incomeToDelete = incomeRepository.findById(id);

        incomeRepository.delete(incomeToDelete.get());

        sendMessage(new TransactionLogDTO(incomeToDelete.get().getDescription(), LocalDateTime.now(), "delete",
                incomeToDelete.get().getUser().getUsername(), incomeToDelete.get().getUser().getRole().getName(), incomeToDelete.get().getIncomeGroup().getName(),
                "income", incomeToDelete.get().getIncomeGroup().getDescription()));
    }

    public IncomeDTO updateIncome(IncomeDTO dto) {

        Optional<IncomeGroup> incomeGroup = Optional.of(incomeGroupRepository.findById(dto.getIncomeGroupId())
                .orElseThrow(() -> new EntityNotFoundException("Income Group not found")));
        Optional<Income> income = Optional.of(incomeRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Income not found")));


        income.get().setDescription(dto.getDescription());
        income.get().setAmount(dto.getAmount());
        income.get().setIncomeGroup(incomeGroup.get());
        income.get().setUser(userUtils.GetCurrentUser());

        sendMessage(new TransactionLogDTO(income.get().getDescription(), LocalDateTime.now(), "update",
                income.get().getUser().getUsername(), income.get().getUser().getRole().getName(), incomeGroup.get().getName(),
                "income", incomeGroup.get().getDescription()));

        incomeRepository.save(income.get());

        return mapStructMapper.incomeToIncomeDTO(income.get());
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
