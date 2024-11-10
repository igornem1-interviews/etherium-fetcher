package limechain.ethereum_fetcher.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import limechain.ethereum_fetcher.model.User;

@DataJpaTest
public class UserRepositoryTest {

    private static final String ALICE = "alice";
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindByUsername() {
        userRepository.save(new User(ALICE, ALICE, null));
        User foundUser = userRepository.findByUsername(ALICE).orElseThrow();
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo(ALICE);
    }

    @Test
    public void testSaveAndFindUser() {
        User savedUser = userRepository.save(new User(ALICE, ALICE, null));
        assertThat(savedUser.getId()).isNotNull();
        User foundUser = userRepository.findById(savedUser.getId()).orElse(null);
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo(ALICE);
    }

    @Test
    public void testSaveFailDuplicate() {
        userRepository.saveAndFlush(new User(ALICE, ALICE, null));
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(new User(ALICE, ALICE, null));
        });
    }
}
