package limechain.etherium.fetcher.service;

import java.util.Random;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import limechain.etherium.fetcher.db.model.Role;
import limechain.etherium.fetcher.db.model.User;
import limechain.etherium.fetcher.db.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserService self;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    final static Random random = new Random();


    @Transactional
    public User createAccount(User account) {
        account.setPassword(bCryptPasswordEncoder.encode(account.getPassword()));
        account.setRoles(Role.OTP);
        return userRepository.save(account);
    }

    @Transactional
    public boolean confirmOtp(String email, String otpKey) {
        User account = self.findByUsername(email);
            account.setRoles(Role.SUBSCRIBE);
            account = userRepository.save(account);
            return true;
    }

    @Transactional
    public void confirmSubscription(Long userId) {
        User user = userRepository.getOne(userId);
        user.setRoles(Role.PAID);
        user = userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

}
