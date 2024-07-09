package internship.project.controller;

import internship.project.scheduling.ReminderScheduler;
import internship.project.service.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.Period;

@RestController
@RequiredArgsConstructor
@RequestMapping("/send-email")
public class EmailController {

    private final EmailService emailService;
    private final ReminderScheduler reminderScheduler;

    @PostMapping("/expense-report")
    public String SendExpenseReport() throws MessagingException {
        emailService.sendExpenseReport();
        return "Email sent successfully";
    }

    @PostMapping("/income-report")
    public String SendIncomeReport() throws MessagingException {
        emailService.sendIncomeReport();
        return "Email sent successfully";
    }

    @PostMapping("/scheduled-emails")
    @Scheduled(cron = "0 0 8 * * MON") //"0 * * * * *"// Every Monday at 8 AM //"0 0 8 * * MON"
    public void sendWeeklyReminders() throws InterruptedException {
        reminderScheduler.sendReminders("weekly", LocalDate.now().minus(Period.ofWeeks(1)));
    }

    @PostMapping("/scheduled-emails-test")
    public void sendRemindersEveryMinute() throws InterruptedException {
        reminderScheduler.sendRemindersByMinute();
    }

    @PostMapping("/scheduled-emails/weekly")
    public void sendRemindersEveryWeek() throws InterruptedException {
        reminderScheduler.sendReminders("weekly", LocalDate.now().minus(Period.ofWeeks(1)));
    }

    @PostMapping("/scheduled-emails/monthly")
    public void sendRemindersEveryMonth() throws InterruptedException {
        reminderScheduler.sendReminders("monthly", LocalDate.now().minus(Period.ofMonths(1)));
    }
}
