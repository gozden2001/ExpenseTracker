package internship.project.dto;

import lombok.Data;
import org.apache.kafka.common.protocol.types.Field;

import java.time.LocalDateTime;

@Data
public class TransactionLogDTO {
    private String description;
    private LocalDateTime date;
    private String eventType;
    private String user;
    private String role;
    private String transactionGroupName;
    private String transactionGroupType;
    private String transactionGroupDescription;

    public TransactionLogDTO(String description, LocalDateTime now, String event, String username, String role, String name, String transactionGroupType, String transactionGroupDescription) {
        this.description = description;
        this.date = now;
        this.eventType = event;
        this.user = username;
        this.role = role;
        this.transactionGroupName = name;
        this.transactionGroupType = transactionGroupType;
        this.transactionGroupDescription = transactionGroupDescription;
    }
}
