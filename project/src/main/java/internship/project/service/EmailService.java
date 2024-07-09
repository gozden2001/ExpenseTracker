package internship.project.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.util.FastList;
import internship.project.dto.TransactionLogDTO;
import internship.project.model.Expense;
import internship.project.model.Income;
import internship.project.model.User;
import internship.project.repository.ExpenseRepository;
import internship.project.repository.IncomeRepository;
import internship.project.repository.UserRepository;
import internship.project.utils.UserUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@EnableAsync
public class EmailService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;
    private final UserUtils userUtils;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic = "transaction_logs";
    private final ObjectMapper objectMapper;

    public void sendExpenseReport() throws MessagingException {
        //MimeMessage message = mailSender.createMimeMessage();

        User currentUser = userUtils.GetCurrentUser();

        LocalDate twelveMonthsAgo = LocalDate.now().minusMonths(12);
        List<Expense> expenses = expenseRepository.findByUserAndDateAfter(currentUser, twelveMonthsAgo);

        Map<YearMonth, List<Expense>> expensesByMonth = expenses.stream()
                .collect(Collectors.groupingBy(expense -> YearMonth.from(expense.getDate())));

        MimeMessage message = mailSender.createMimeMessage();
        message.setFrom("borisgozdenovic@gmail.com");
        message.setRecipients(MimeMessage.RecipientType.TO, currentUser.getEmail());
        message.setSubject("Expense Report");

        StringBuilder emailBody = createExpenseReportBody(expensesByMonth);

        message.setContent(emailBody.toString(), "text/html; charset=utf-8");
        mailSender.send(message);

        sendMessage(new TransactionLogDTO("Report created", LocalDateTime.now(), "expense report",
                currentUser.getUsername(), currentUser.getRole().getName(),"n/a", null, null));
    }

    private StringBuilder createExpenseReportBody(Map<YearMonth, List<Expense>> expensesByMonth){
        StringBuilder emailBody = new StringBuilder("<html><body><h3>Expense Report</h3>");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        expensesByMonth.forEach((month, monthExpenses) -> {
            emailBody.append("<h4>").append(month.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH))
                    .append(" ").append(month.getYear()).append("</h4>");
            emailBody.append(createExpenseTable(monthExpenses, formatter));
        });
        emailBody.append("</body></html>");

        return emailBody;
    }

    private String createExpenseTable(List<Expense> expenses, DateTimeFormatter formatter) {
        StringBuilder table = new StringBuilder();
        table.append("<table border='1'>");
        table.append("<tr><th>Description</th><th>Amount</th><th>Date</th><th>Expense Group</th></tr>");

        for (Expense expense : expenses) {
            table.append("<tr>")
                    .append("<td>").append(expense.getDescription()).append("</td>")
                    .append("<td>").append(expense.getAmount()).append("</td>")
                    .append("<td>").append(expense.getDate().format(formatter)).append("</td>")
                    .append("<td>").append(expense.getExpenseGroup().getName()).append("</td>")
                    .append("</tr>");
        }

        table.append("</table>");
        return table.toString();
    }

    public void sendIncomeReport() throws MessagingException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        LocalDate twelveMonthsAgo = LocalDate.now().minusMonths(12);
        List<Income> incomes = incomeRepository.findByUserAndDateAfter(currentUser, twelveMonthsAgo);

        Map<YearMonth, List<Income>> incomesByMonth = incomes.stream()
                .collect(Collectors.groupingBy(income -> YearMonth.from(income.getDate())));

        MimeMessage message = mailSender.createMimeMessage();
        message.setFrom("borisgozdenovic@gmail.com");
        message.setRecipients(MimeMessage.RecipientType.TO, currentUser.getEmail());
        message.setSubject("Income Report");

        StringBuilder emailBody = createIncomeReportBody(incomesByMonth);

        message.setContent(emailBody.toString(), "text/html; charset=utf-8");
        mailSender.send(message);

        sendMessage(new TransactionLogDTO("Report created", LocalDateTime.now(), "income report",
                currentUser.getUsername(), currentUser.getRole().getName(),"n/a", null, null));
    }

    private StringBuilder createIncomeReportBody(Map<YearMonth, List<Income>> incomesByMonth){
        StringBuilder emailBody = new StringBuilder("<html><body><h3>Income Report</h3>");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        incomesByMonth.forEach((month, monthIncomes) -> {
            emailBody.append("<h4>").append(month.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH))
                    .append(" ").append(month.getYear()).append("</h4>");
            emailBody.append(createIncomeTable(monthIncomes, formatter));
        });
        emailBody.append("</body></html>");

        return emailBody;
    }


    private String createIncomeTable(List<Income> incomes, DateTimeFormatter formatter) {
        StringBuilder table = new StringBuilder();
        table.append("<table border='1'>");
        table.append("<tr><th>Description</th><th>Amount</th><th>Date</th><th>Expense Group</th></tr>");

        for (Income income : incomes) {
            table.append("<tr>")
                    .append("<td>").append(income.getDescription()).append("</td>")
                    .append("<td>").append(income.getAmount()).append("</td>")
                    .append("<td>").append(income.getDate().format(formatter)).append("</td>")
                    .append("<td>").append(income.getIncomeGroup().getName()).append("</td>")
                    .append("</tr>");
        }

        table.append("</table>");
        return table.toString();
    }

    @Async
    public void sendEmail(String email, String subject, String body) throws InterruptedException {
        SimpleMailMessage message = new SimpleMailMessage();

        Thread.sleep(10000);
        message.setFrom("borisgozdenovic@gmail.com");
        message.setTo(email);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
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
