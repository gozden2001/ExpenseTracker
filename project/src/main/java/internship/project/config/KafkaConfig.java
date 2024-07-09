package internship.project.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic logTopic() {
        return new NewTopic("transaction_logs", 1, (short) 1);
    }
}
