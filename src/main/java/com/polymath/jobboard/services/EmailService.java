package com.polymath.jobboard.services;

import com.polymath.jobboard.models.Jobs;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmailService {
    private final   JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendJobAlertsEmail(String to, String subject, String jobSeekerName, List<Jobs> jobs, String searchedQuery,String unsubscribeLink) {
        try {
            Context context = new Context();
            context.setVariable("JobSeekerName", jobSeekerName);
            context.setVariable("Jobs", jobs);
            context.setVariable("SearchedQuery", searchedQuery);
            context.setVariable("Year", LocalDate.now().getYear());
            context.setVariable("WebsiteLink","http://localhost:8081");
            context.setVariable("UnsubscribeLink",unsubscribeLink);
            String htmlContent = templateEngine.process("jobAlertsTemplate", context);
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);
            helper.setFrom("olosanyusuf19@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent,true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
