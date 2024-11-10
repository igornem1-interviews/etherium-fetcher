package limechain.ethereum_fetcher;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import limechain.ethereum_fetcher.model.User;
import limechain.ethereum_fetcher.repository.UserRepository;

//@ContextConfiguration(initializers = { UserRepositoryIntegrationTest.Initializer.class })
@SpringBootTest
public class UserRepositoryIntegrationTest {

    private static final String ALICE = "alice";


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    public void testSaveNewUser() {
        userRepository.deleteAll();

        User user = userRepository.save(User.builder().username(ALICE).password(bCryptPasswordEncoder.encode(ALICE)).build());

        assertThat(user).matches(u -> u.getId() != null && u.getUsername().equals(ALICE) && u.getPassword().equals(bCryptPasswordEncoder.encode(ALICE)));
        assertThat(userRepository.findAll().size() == 1);
    }

    /*
     * static class Initializer implements
     * ApplicationContextInitializer<ConfigurableApplicationContext> { public void
     * initialize(ConfigurableApplicationContext configurableApplicationContext) {
     * TestPropertyValues.of("spring.datasource.url=" + dbContainer.getJdbcUrl(),
     * "spring.datasource.username=" + dbContainer.getUsername(),
     * "spring.datasource.password=" +
     * dbContainer.getPassword()).applyTo(configurableApplicationContext.
     * getEnvironment()); } }
     */
}
