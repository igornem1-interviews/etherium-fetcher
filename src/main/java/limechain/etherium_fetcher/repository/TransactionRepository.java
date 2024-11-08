package limechain.etherium_fetcher.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.transaction.Transactional;
import limechain.etherium_fetcher.model.EthereumTransaction;

public interface TransactionRepository extends JpaRepository<EthereumTransaction, Long> {

    public static final String P_DELIMETER = ",:";
    public static final String COMMA = ",";

    List<EthereumTransaction> findByTransactionHashIn(List<String> transactionHashes);
    
    @Transactional
    default void saveOne(EthereumTransaction tx) {
        this.save(tx);
    }

}
