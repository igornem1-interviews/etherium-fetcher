package limechain.ethereum_fetcher.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import limechain.ethereum_fetcher.model.Transaction;
import limechain.ethereum_fetcher.model.User;

@DataJpaTest
public class TransactionRepositoryTest {

    private static final String HASH1 = "hash1";
    private static final String HASH2 = "hash2";
    private static final String HASH3 = "hash3";
    private static final int logsCount = 5;
    private static final String ALICE = "alice";
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void clean() {
        transactionRepository.deleteAll();
        transactionRepository.flush();
    }

    @Test
    public void testFindByHash() {
        transactionRepository.save(createTransaction(HASH1));
        transactionRepository.save(createTransaction(HASH2));
        transactionRepository.save(createTransaction(HASH3));
                
        List<Transaction> transactions = transactionRepository.findByHashIn(List.of(HASH1, HASH2));

        assertThat(transactions).isNotNull();
        assertThat(transactions).hasSize(2);

        assertThat(transactions).extracting(Transaction::getHash).containsExactlyInAnyOrder(HASH1, HASH2);
    }

    @Test
    public void testNotFindByWrongHash() {
        transactionRepository.save(createTransaction(HASH1));
        transactionRepository.save(createTransaction(HASH2));
        transactionRepository.save(createTransaction(HASH3));

        List<Transaction> transactions = transactionRepository.findByHashIn(List.of(HASH1 + HASH2));

        assertThat(transactions).isNotNull();
        assertThat(transactions).hasSize(0);

    }

    @Test
    public void testSaveFailDuplicate() {
        transactionRepository.saveOne(createTransaction(HASH1));
        assertThrows(DataIntegrityViolationException.class, () -> {
            transactionRepository.saveOne(createTransaction(HASH1));
        });
    }

    @Test
    public void testFoundTransactionsByUser() {
        User user = userRepository.save(new User(ALICE, ALICE, Set.of(
                        transactionRepository.save(createTransaction(HASH1)),
                        transactionRepository.save(createTransaction(HASH2))     
                    )));
        transactionRepository.saveAndFlush(createTransaction(HASH3));

        List<Transaction> transactions = transactionRepository.findTransactionsByUser(user);

        assertThat(transactions).isNotNull();
        assertThat(transactions).hasSize(2);

        assertThat(transactions).extracting(Transaction::getHash).containsExactlyInAnyOrder(HASH1, HASH2);
    }

    @Test
    public void testNotFoundTransactionsByWongUser() {
        User alice = userRepository.save(
                new User(ALICE, ALICE, Set.of(
                        transactionRepository.save(createTransaction(HASH1)), 
                        transactionRepository.save(createTransaction(HASH2)))));
        User bob = userRepository.save(new User("bob", "bob", null));

        List<Transaction> transactions = transactionRepository.findTransactionsByUser(bob);

        assertThat(transactions).isNotNull();
        assertThat(transactions).hasSize(0);

    }

    private Transaction createTransaction(String hash) {
        return new Transaction(hash, Boolean.TRUE, hash, BigInteger.TWO, hash, hash, hash, logsCount, hash, BigInteger.TEN, null);
    }

}
