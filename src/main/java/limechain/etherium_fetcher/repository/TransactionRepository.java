package limechain.etherium_fetcher.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import limechain.etherium_fetcher.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    public static final String P_DELIMETER = ",:";
    public static final String COMMA = ",";

	List<Transaction> findByHashIn(List<String> hashes);
    
    @Transactional(value = TxType.REQUIRES_NEW)
    default void saveOne(Transaction tx) {
        this.save(tx);
    }

}
