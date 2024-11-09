package limechain.etherium_fetcher.config;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import limechain.etherium_fetcher.model.User;
import limechain.etherium_fetcher.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DBLoader {
    private final UserRepository repository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final List<String> users = List.of("alice", "bob", "carol", "dave");

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            users.forEach(user -> fetchOrCreate(user));
            log.info(System.lineSeparator() + "**************************************************************"
                   + System.lineSeparator() + "* Users credentials loaded into system(username / password): *"
                   + System.lineSeparator() + "**************************************************************"
                   + System.lineSeparator()+users.stream().map(name -> name + " / " + name).collect(Collectors.joining(System.lineSeparator())));
        };
    }

    private User fetchOrCreate(String username) {
        Optional<User> byUsername = repository.findByUsername(username);
        return byUsername.orElseGet(() -> repository.save(new User(username, bCryptPasswordEncoder.encode(username))));
    }
}
