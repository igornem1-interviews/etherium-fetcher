package limechain.etherium_fetcher.config;

import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import limechain.etherium_fetcher.model.User;
import limechain.etherium_fetcher.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class DBLoader {
	private final UserRepository repository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	@Bean
	CommandLineRunner initDatabase() {
		return args -> {
			log.info("Preloading " + fetchOrCreate("alice"));
			log.info("Preloading " + fetchOrCreate("aob"));
			
		};
	}

	private User fetchOrCreate(String username) {
		Optional<User> byUsername = repository.findByUsername(username);
		return byUsername
				.orElseGet(() ->
						repository.save(new User(username, bCryptPasswordEncoder.encode(username))));
	}
}
