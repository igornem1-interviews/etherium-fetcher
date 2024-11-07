package limechain.etherium.fetcher.config.db;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import limechain.etherium.fetcher.db.model.Account;
import limechain.etherium.fetcher.db.model.Role;
import limechain.etherium.fetcher.db.repository.TransactionRepository;

@Configuration
class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(TransactionRepository repository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        return args -> {
            log.info("Preloading " + repository.save(new Account("bilbo@gmail.com", bCryptPasswordEncoder.encode("1234"), Set.of(Role.SUBSCRIBE))));
            log.info("Preloading " + repository.save(new Account("Frodo@gmail.com", bCryptPasswordEncoder.encode("1234"), Set.of(Role.SUBSCRIBE))));
        };
    }

}