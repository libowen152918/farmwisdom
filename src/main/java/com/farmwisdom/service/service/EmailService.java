package com.farmwisdom.service;

import com.farmwisdom.entity.User;
import java.util.Map;

public interface EmailService {
    void sendPasswordResetEmail(User user, String token);
    void sendAccountActivationEmail(User user, String token);
    void sendPasswordChangedEmail(User user);
    void sendWelcomeEmail(User user);
    void sendExpertVerificationEmail(User user);
    void sendEmail(String to, String subject, String templateName, Map<String, Object> variables);
}
