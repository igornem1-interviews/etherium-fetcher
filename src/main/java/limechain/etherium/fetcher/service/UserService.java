package limechain.etherium.fetcher.service;

import java.util.Random;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import limechain.etherium.fetcher.model.Role;
import limechain.etherium.fetcher.model.User;
import limechain.etherium.fetcher.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    final static Random random = new Random();


    @Transactional
    public User createAccount(User account) {
        account.setPassword(bCryptPasswordEncoder.encode(account.getPassword()));
        account.setRoles(Role.OTP);
        return userRepository.save(account);
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
