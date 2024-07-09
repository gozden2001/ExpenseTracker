package internship.project.scheduling;

import internship.project.model.Reminder;
import internship.project.model.Role;
import internship.project.model.User;
import internship.project.repository.ExpenseRepository;
import internship.project.repository.ReminderRepository;
import internship.project.repository.RoleRepository;
import internship.project.repository.UserRepository;
import internship.project.service.EmailService;
import internship.project.utils.UserUtils;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ReminderScheduler implements SchedulingConfigurer {
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final ReminderRepository reminderRepository;
    private final RoleRepository roleRepository;
    private final EmailService emailService;
    private final UserUtils userUtils;
    private final ThreadPoolTaskScheduler taskScheduler;

//    @Scheduled(cron = "0 0 8 * * MON") //"0 * * * * *"// Every Monday at 8 AM //"0 0 8 * * MON"
//    public void sendWeeklyReminders() throws InterruptedException {
//        sendReminders("weekly", LocalDate.now().minus(Period.ofWeeks(1)));
//    }
//
//    @Scheduled(cron = "0 * * ? * *")
//    public void sendRemindersEveryMinute() throws InterruptedException {
//        sendRemindersByMinute();
//    }
//
//    @Scheduled(cron = "0 0 8 1 * ?") // 1st day of every month at 8 AM
//    public void sendMonthlyReminders() throws InterruptedException {
//        sendReminders("monthly", LocalDate.now().minus(Period.ofMonths(1)));
//    }

    public void sendReminders(String frequency, LocalDate startDate) throws InterruptedException {
        Role role = roleRepository.findByName("ROLE_PREMIUM").get();
        List<User> users = userRepository.findByRoleAndReminderFrequency(role, frequency);

        for (User user : users) {
            LocalDate endDate = LocalDate.now();
            double totalSpent = expenseRepository.sumAmountByUserAndDateBetween(user.getId(), startDate, endDate);
            String subject = "Your Spending Summary";
            String body = "Dear " + user.getUsername() + ",\n\n" +
                    "You have spent a total of " + totalSpent + " in the last " +
                    (Objects.equals(frequency, "weekly") ? "week" : "month") + ".\n\n" +
                    "Best regards,\nYour Expense Tracker Team";

            Reminder newReminder = new Reminder();
            newReminder.setReminderDay(LocalDate.now());
            newReminder.setActive(true);
            newReminder.setType(frequency);
            newReminder.setUser(userUtils.GetCurrentUser());
            reminderRepository.save(newReminder);
            emailService.sendEmail(user.getEmail(), subject, body);
        }
    }


    public void sendRemindersByMinute() throws InterruptedException {
        Role role = roleRepository.findByName("ROLE_PREMIUM").orElseThrow(() -> new IllegalArgumentException("Role not found"));
        List<User> users = userRepository.findAll();

        for (User user : users) {
            String subject = "Your Minute Reminder";
            String body = "This is a reminder for you, " + user.getUsername() + ", at minute " + LocalDateTime.now().getMinute() + ".";

            Reminder newReminder = new Reminder();
            newReminder.setReminderDay(LocalDate.now());
            newReminder.setActive(true);
            newReminder.setType("byMinute");
            newReminder.setUser(user);
            reminderRepository.save(newReminder);

            emailService.sendEmail(user.getEmail(), subject, body);
        }
        emailService.sendEmail("borisgozdenovic@gmail.com", "test", "body");
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setTaskScheduler(taskScheduler);
    }
}
