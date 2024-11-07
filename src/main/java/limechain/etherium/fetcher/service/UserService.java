package limechain.etherium.fetcher.service;

import java.util.Random;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import limechain.etherium.fetcher.db.model.Account;
import limechain.etherium.fetcher.db.model.Otp;
import limechain.etherium.fetcher.db.model.ResetPassword;
import limechain.etherium.fetcher.db.model.Role;
import limechain.etherium.fetcher.db.repository.TransactionRepository;
import limechain.etherium.fetcher.db.repository.OtpRepository;
import limechain.etherium.fetcher.db.repository.ResetPasswordRepository;

@Service
public class UserService {

    @Autowired
    private UserService self;
    @Autowired
    private TransactionRepository accountRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private OtpRepository otpRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ResetPasswordRepository resetPasswordRepository;

    final static Random random = new Random();

    public void signup(Account account) {
        Otp otp = self.createAccount(account);
        emailService.sendOtpToEmail(account, otp);
    }

    @Transactional
    public Otp createAccount(Account account) {
        account.setPassword(bCryptPasswordEncoder.encode(account.getPassword()));
        account.setRoles(Role.OTP);
        account = accountRepository.save(account);
        return generateAndSetOtp(account);
    }

    @Transactional
    public Otp generateAndSetOtp(Account account) {
        Otp otp = otpRepository.findByUserUsername(account.getUsername());
        if (otp == null) {
            otp = new Otp();
        }
        otp.setUser(account);
        otp.setOtp(String.valueOf((int) Math.floor(Math.random() * (999999 - 100000 + 1) + 100000)));
        return otpRepository.save(otp);
    }

    @Transactional
    public boolean confirmOtp(String email, String otpKey) {
        Otp otp = otpRepository.findByUserUsernameAndOtp(email, otpKey);
        if (otp != null && otpKey.trim().equals(otp.getOtp())) {
            Account account = otp.getUser();
            account.setRoles(Role.SUBSCRIBE);
            account = accountRepository.save(account);
            otpRepository.delete(otp);
            return true;
        }
        return false;
    }

    @Transactional
    public void confirmSubscription(Long userId) {
        Account user = accountRepository.getOne(userId);
        user.setRoles(Role.PAID);
        user = accountRepository.save(user);
    }

    public Account findByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    public void resetPassword(String email, HttpServletRequest request) {
        Account user = accountRepository.findByUsername(email);
        if (user != null) {
            String domain = request.getRequestURL().toString().replace(request.getRequestURI(), "");
            UUID key = UUID.randomUUID();
            self.setResetPassword(user, key);
            String resetPasswordLink = domain + "/reset-password-set/" + key;
            emailService.sendResetPasswordToEmail(user, resetPasswordLink);
        }
    }

    @Transactional
    public void setResetPassword(Account user, UUID resetPasswordKey) {
        ResetPassword resetPassword = user.getResetPassword();
        if (resetPassword != null) {
            resetPassword.setResetKey(resetPasswordKey);
        } else {
            resetPassword = new ResetPassword(user, resetPasswordKey);
            user.setResetPassword(resetPassword);
            accountRepository.save(user);
        }
        resetPasswordRepository.save(resetPassword);
    }

    @Transactional
    public void updatePassword(ResetPassword resetPassword, String password) {
        Account user = resetPassword.getUser();
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setResetPassword(null);
        resetPasswordRepository.delete(resetPassword);
        accountRepository.save(user);
    }
}
