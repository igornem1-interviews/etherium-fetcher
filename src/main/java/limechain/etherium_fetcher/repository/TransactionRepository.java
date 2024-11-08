package limechain.etherium_fetcher.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import jakarta.transaction.Transactional;
import limechain.etherium_fetcher.model.EthereumTransaction;

public interface TransactionRepository extends JpaRepository<EthereumTransaction, Long> {

    public static final String P_DELIMETER = ",:";
    public static final String COMMA = ",";

    List<EthereumTransaction> findByTransactionHashIn(List<String> transactionHashes);
    
    @Query(value = "INSERT INTO " + EthereumTransaction.TABLE_NAME + " (" /* + EthereumTransaction.VALUE_ + "," */ + 
            EthereumTransaction.INPUT_ + COMMA
            + EthereumTransaction.LOGS_COUNT + COMMA
            + EthereumTransaction.CONTRACT_ADDRESS + COMMA 
            + EthereumTransaction.TO_  + COMMA 
            + EthereumTransaction.FROM_ + COMMA 
            + EthereumTransaction.BLOCK_HASH + COMMA
            /* + EthereumTransaction.TRANSACTION_STATUS + COMMA */ 
            + EthereumTransaction.TRANSACTION_HASH + ") " + "VALUES" 
            + " (:" /* + EthereumTransaction.VALUE_ + ",:" */
            + EthereumTransaction.INPUT_ + P_DELIMETER
            + EthereumTransaction.LOGS_COUNT + P_DELIMETER 
            + EthereumTransaction.CONTRACT_ADDRESS + P_DELIMETER 
            + EthereumTransaction.TO_ + P_DELIMETER 
            + EthereumTransaction.FROM_ + P_DELIMETER
            /* + EthereumTransaction.BLOCK_NUMBER + ",:" */
            + EthereumTransaction.BLOCK_HASH + P_DELIMETER 
            /*+ EthereumTransaction.TRANSACTION_STATUS + P_DELIMETER*/
            + EthereumTransaction.TRANSACTION_HASH + ") "
            + "ON CONFLICT (transaction_hash) DO NOTHING", nativeQuery = true)
    @Modifying
    @Transactional
    int saveAllIfNotExists(/* List<BigDecimal> value_, */ List<String> input_, List<Integer> logs_count, List<String> contract_address, List<String> to_, List<String> from_,
            /* List<BigDecimal> block_number, */ List<String> block_hash, /* List<Boolean> transaction_status, */ List<String> transaction_hash);
}
