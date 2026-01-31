package com.expensetracker.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailUtil {
    
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.from}")
    private String fromEmail;
    
    @Async("taskExecutor")
    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            
            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
        }
    }
    
    @Async("taskExecutor")
    public void sendWelcomeEmail(String to, String firstName) {
        String subject = "Welcome to Expense Tracker!";
        String body = String.format(
                "Hi %s,\n\n" +
                        "Welcome to Expense Tracker!\n\n" +
                        "Thank you for registering. You can now start tracking your expenses and managing your budget.\n\n" +
                        "Best regards,\n" +
                        "Expense Tracker Team",
                firstName
        );
        sendEmail(to, subject, body);
    }
    
    @Async("taskExecutor")
    public void sendBudgetAlertEmail(String to, String categoryName, double percentageUsed, double budgetAmount) {
        String subject = "Budget Alert: " + categoryName;
        String body = String.format(
                "Budget Alert!\n\n" +
                        "Your budget for '%s' has reached %.0f%% (₹%.2f).\n\n" +
                        "Please review your spending to stay within budget.\n\n" +
                        "Best regards,\n" +
                        "Expense Tracker Team",
                categoryName,
                percentageUsed,
                budgetAmount
        );
        sendEmail(to, subject, body);
    }
    
    @Async("taskExecutor")
    public void sendMonthlyReportEmail(String to, String reportSummary) {
        String subject = "Your Monthly Expense Report";
        String body = "Hi,\n\n" +
                "Here's your monthly expense report:\n\n" +
                reportSummary +
                "\n\nBest regards,\n" +
                "Expense Tracker Team";
        sendEmail(to, subject, body);
    }
}
