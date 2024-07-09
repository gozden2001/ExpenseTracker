package internship.project.logger.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import internship.project.logger.model.*;
import internship.project.logger.repository.*;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KafkaLogService {

    private final ObjectMapper objectMapper;
    private final EventTypeRepository eventTypeRepository;
    private final EventLogRepository eventLogRepository;
    private final UserRepository userRepository;
    private final TransactionGroupRepository transactionGroupRepository;
    private final StagingEventLogRepository stagingEventLogRepository;

    @KafkaListener(topics = "transaction_logs")
    public void receiveMessage(String message){
        try {
            Map<String, Object> messageMap = objectMapper.readValue(message, Map.class);

            String description = (String) messageMap.get("description");
            LocalDateTime date = objectMapper.convertValue(messageMap.get("date"), LocalDateTime.class);
            String eventTypeName = (String) messageMap.get("eventType");
            String username = (String) messageMap.get("user");
            String role = (String) messageMap.get("role");
            String transactionType = (String) messageMap.get("transactionGroupType");
            String transactionGroupDescription = (String) messageMap.get("transactionGroupDescription");
            String transactionGroupName = (String) messageMap.get("transactionGroupName");

//            Optional<User> user = userRepository.findByUsernameAndRole(username, role)
//                    .or(() -> Optional.of(saveUser(username, role)));
//
//            Optional<EventType> eventType = Optional.of(eventTypeRepository.findByEventType(eventTypeName)
//                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "item not found")));
//
//            Optional<TransactionGroup> transactionGroup = transactionGroupRepository.findByName(transactionGroupName)
//                    .or(() -> Optional.of(saveTransactionGroup(transactionGroupName, transactionGroupType, transactionGroupDescription)));
//
//            EventLog newEventLog = EventLog.builder()
//                    .eventType(eventType.get())
//                    .user((user.get()))
//                    .transactionGroup(transactionGroup.get())
//                    .date(date)
//                    .description(description)
//                    .build();
//            eventLogRepository.save(newEventLog);
            StagingEventLog stagingEventLog = StagingEventLog.builder()
                    .eventType(eventTypeName)
                    .date(date)
                    .description(description)
                    .username(username)
                    .role(role)
                    .transactionType(transactionType)
                    .name(transactionGroupName)
                    .build();


            stagingEventLogRepository.save(stagingEventLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private TransactionGroup saveTransactionGroup(String name, String transactionType, String description) {
//        TransactionGroup transactionGroup = new TransactionGroup();
//        transactionGroup.setTransactionType(transactionType);
//        transactionGroup.setName(name);
//        transactionGroup.setDescription(description);
//        return  transactionGroupRepository.save(transactionGroup);
//    }
//
//    private User saveUser(String username, String role) {
//        User user = new User();
//        user.setRole(role);
//        user.setUsername(username);
//        return userRepository.save(user);
//    }
}
