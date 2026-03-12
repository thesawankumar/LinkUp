package com.chatapp.chat_backend.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    // @Async — yeh background mein chalega
    // User ko turant response milega, email baad mein jayegi
    @Async
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("ChatApp Login Code: " + otp);
            helper.setText(buildEmailHtml(otp), true); // true = HTML

            mailSender.send(message);
            log.info("OTP email sent to: {}", toEmail);

        } catch (Exception e) {
            log.error("Email send failed: {}", e.getMessage());
            throw new RuntimeException("Email bhejne mein error aaya!");
        }
    }

    private String buildEmailHtml(String otp) {
        return """
            <div style="font-family:Arial,sans-serif;max-width:480px;
                        margin:auto;padding:40px;background:#f4f6f9;
                        border-radius:16px;">
              <h2 style="color:#1E3A5F;">Your ChatApp Login Code</h2>
              <p style="color:#666;">
                Yeh code 10 minutes mein expire ho jayega.
              </p>
              <div style="background:#fff;border-radius:12px;padding:30px;
                          text-align:center;margin:24px 0;">
                <span style="font-size:48px;font-weight:bold;
                             color:#2E86AB;letter-spacing:12px;">
                  %s
                </span>
              </div>
              <p style="color:#999;font-size:13px;">
                Agar tumne request nahi kiya toh ignore karo.
              </p>
            </div>
            """.formatted(otp);
    }
}
