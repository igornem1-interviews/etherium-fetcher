package limechain.ethereum_fetcher.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigInteger;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import limechain.ethereum_fetcher.model.Transaction;

@DataJpaTest
public class TransactionRepositoryTest {

    private static final String HASH1 = "hash1";
    private static final String HASH2 = "hash2";
    private static final String HASH3 = "hash3";
    private static final int logsCount = 5;
    @Autowired
    private TransactionRepository transactionRepository;

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
    public void testNotFindByHash() {
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

    private Transaction createTransaction(String hash) {
        return new Transaction(hash, Boolean.TRUE, hash, BigInteger.TWO, hash, hash, hash, logsCount, hash, BigInteger.TEN, null);
    }

}
