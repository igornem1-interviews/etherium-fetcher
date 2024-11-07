package limechain.etherium.fetcher.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import limechain.etherium.fetcher.db.model.Account;
import limechain.etherium.fetcher.db.model.Otp;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    public String from;

    public void sendEmail(String email, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    public void sendOtpToEmail(Account user, Otp otp) {
        sendOtpToEmail(user.getUsername(), otp.getOtp());
    }

    public void sendResetPasswordToEmail(Account user, String resetPasswordLink) {
        sendEmail(user.getUsername(), "Reset password | 2FastSecurity.com", "Hello,\r\n" + "reset password link : " + resetPasswordLink + "\r\n" + "Our team");
    }

    public void sendOtpToEmail(String email, String otpCode) {
        sendEmail(email, "OTP Verification | Useracounts.com", "Hello,\r\n" + "OTP Verification code : " + otpCode + "\r\n" + "Our team");

    }
}
