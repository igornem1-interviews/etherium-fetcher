package limechain.etherium.fetcher.db.repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import limechain.etherium.fetcher.db.model.EthereumTransaction;

public interface TransactionRepository extends JpaRepository<EthereumTransaction, Long> {

    Set<EthereumTransaction> findByTransactionHashIn(Set<BigInteger> transactionHashes);
    
    @Query(value = "INSERT INTO " + EthereumTransaction.TABLE_NAME + " (" + EthereumTransaction.ROLE + ",:" + EthereumTransaction.NAME + ",:" + EthereumTransaction.VALUE + ",:"
            + EthereumTransaction.INPUT + ",:" + EthereumTransaction.LOGS_COUNT + ",:" + EthereumTransaction.CONTRACT_ADDRESS + ",:" + EthereumTransaction.TO + ",:"
            + EthereumTransaction.FROM + ",:" + EthereumTransaction.BLOCK_NUMBER + ",:" + EthereumTransaction.BLOCK_HASH + ",:" + EthereumTransaction.TRANSACTION_STATUS + ",:"
            + EthereumTransaction.TRANSACTION_HASH
            + ") " + 
            "VALUES" + " (:" + EthereumTransaction.ROLE + ",:" + EthereumTransaction.NAME + ",:" + EthereumTransaction.VALUE + ",:" + EthereumTransaction.INPUT + ",:"
            + EthereumTransaction.LOGS_COUNT + ",:" + EthereumTransaction.CONTRACT_ADDRESS + ",:" + EthereumTransaction.TO + ",:" + EthereumTransaction.FROM + ",:"
            + EthereumTransaction.BLOCK_NUMBER + ",:" + EthereumTransaction.BLOCK_HASH + ",:" + EthereumTransaction.TRANSACTION_STATUS + ",:" + EthereumTransaction.TRANSACTION_HASH
            + ") "
            + "ON CONFLICT (transaction_hash) DO NOTHING", nativeQuery = true)
    void saveAllIfNotExists(List<BigInteger> transactionHashes, List<Integer> transactionStatuses);

}
