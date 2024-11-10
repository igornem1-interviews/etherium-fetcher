package limechain.ethereum_fetcher;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import limechain.ethereum_fetcher.model.User;
import limechain.ethereum_fetcher.repository.UserRepository;

@DataJpaTest
public class UserRepositoryTest {

    private static final String ALICE = "alice";
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindByUsername() {
        User user = new User(ALICE, ALICE, null);
        userRepository.save(user);
        User foundUser = userRepository.findByUsername(ALICE).orElseThrow();

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo(ALICE);
    }

    @Test
    public void testSaveAndFindUser() {
        User user = new User(ALICE, ALICE, null);
        User savedUser = userRepository.save(user);
        assertThat(savedUser.getId()).isNotNull();
        User foundUser = userRepository.findById(savedUser.getId()).orElse(null);
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo(ALICE);
    }
}
