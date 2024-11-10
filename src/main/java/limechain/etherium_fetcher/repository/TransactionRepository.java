package limechain.etherium_fetcher.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import limechain.etherium_fetcher.model.Transaction;
import limechain.etherium_fetcher.model.User;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    public static final String P_DELIMETER = ",:";
    public static final String COMMA = ",";

	List<Transaction> findByHashIn(List<String> hashes);

    @Transactional(value = TxType.REQUIRES_NEW)
    default Transaction saveOne(Transaction tx) {
        return this.save(tx);
    }

    @Query("SELECT u.transactions FROM User u WHERE u = :user")
    List<Transaction> findTransactionsByUser(@Param("user") User user);

}
