package com.farmwisdom.service.impl;

import com.farmwisdom.config.EmailConfig;
import com.farmwisdom.entity.User;
import com.farmwisdom.service.EmailService;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final EmailConfig emailConfig;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    @Async
    public void sendPasswordResetEmail(User user, String token) {
        log.debug("Sending password reset email to: {}", user.getEmail());

        Map<String, Object> variables = new HashMap<>();
        variables.put("username", user.getUsername());
        variables.put("resetLink", emailConfig.getBaseUrl() + "/reset-password?token=" + token);
        variables.put("supportEmail", emailConfig.getSupportEmail());

        sendEmail(user.getEmail(), "密码重置", "mail/passwordResetEmail", variables);
    }

    @Override
    @Async
    public void sendAccountActivationEmail(User user, String token) {
        log.debug("Sending activation email to: {}", user.getEmail());

        Map<String, Object> variables = new HashMap<>();
        variables.put("username", user.getUsername());
        variables.put("activationLink", emailConfig.getBaseUrl() + "/activate?token=" + token);
        variables.put("supportEmail", emailConfig.getSupportEmail());

        sendEmail(user.getEmail(), "账号激活", "mail/accountActivationEmail", variables);
    }

    @Override
    @Async
    public void sendPasswordChangedEmail(User user) {
        log.debug("Sending password changed notification to: {}", user.getEmail());

        Map<String, Object> variables = new HashMap<>();
        variables.put("username", user.getUsername());
        variables.put("supportEmail", emailConfig.getSupportEmail());

        sendEmail(user.getEmail(), "密码修改通知", "mail/passwordChangedEmail", variables);
    }

    @Override
    @Async
    public void sendWelcomeEmail(User user) {
        log.debug("Sending welcome email to: {}", user.getEmail());

        Map<String, Object> variables = new HashMap<>();
        variables.put("username", user.getUsername());
        variables.put("supportEmail", emailConfig.getSupportEmail());

        sendEmail(user.getEmail(), "欢迎加入农智汇", "mail/welcomeEmail", variables);
    }

    @Override
    @Async
    public void sendExpertVerificationEmail(User user) {
        log.debug("Sending expert verification email to: {}", user.getEmail());

        Map<String, Object> variables = new HashMap<>();
        variables.put("username", user.getUsername());
        variables.put("supportEmail", emailConfig.getSupportEmail());

        sendEmail(user.getEmail(), "专家认证申请", "mail/expertVerificationEmail", variables);
    }

    public void sendEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        try {
            // 在本地开发环境中，只记录邮件内容而不实际发送
            log.info("模拟发送邮件 - 收件人: {}, 主题: {}, 模板: {}", to, subject, templateName);
            log.info("邮件变量: {}", variables);

            // 生成邮件内容用于日志记录
            Context context = new Context();
            context.setVariables(variables);
            String emailContent = templateEngine.process(templateName, context);
            log.info("邮件内容: {}", emailContent);

            // 注释掉实际的邮件发送代码
            /*
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(emailContent, true);
            mailSender.send(message);
            */
        } catch (Exception e) {
            log.error("发送邮件失败", e);
            // 在本地开发环境中不抛出异常
            // throw new RuntimeException("发送邮件失败", e);
        }
    }
}
