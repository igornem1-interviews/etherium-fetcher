package limechain.etherium.fetcher.repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import limechain.etherium.fetcher.model.EthereumTransaction;

public interface TransactionRepository extends JpaRepository<EthereumTransaction, Long> {

    Set<EthereumTransaction> findByTransactionHashIn(Set<BigInteger> transactionHashes);
    
    @Query(value = "INSERT INTO " + EthereumTransaction.TABLE_NAME + " (" + EthereumTransaction.VALUE + "," + EthereumTransaction.INPUT + "," + EthereumTransaction.LOGS_COUNT + ","
            + EthereumTransaction.CONTRACT_ADDRESS + "," + EthereumTransaction.TO_ + "," + EthereumTransaction.FROM_ + "," + EthereumTransaction.BLOCK_NUMBER + ","
            + EthereumTransaction.BLOCK_HASH + "," + EthereumTransaction.TRANSACTION_STATUS + "," + EthereumTransaction.TRANSACTION_HASH + ") " +
            "VALUES" + " (:" + EthereumTransaction.VALUE + ",:" + EthereumTransaction.INPUT + ",:"
            + EthereumTransaction.LOGS_COUNT + ",:" + EthereumTransaction.CONTRACT_ADDRESS + ",:" + EthereumTransaction.TO_ + ",:" + EthereumTransaction.FROM_ + ",:"
            + EthereumTransaction.BLOCK_NUMBER + ",:" + EthereumTransaction.BLOCK_HASH + ",:" + EthereumTransaction.TRANSACTION_STATUS + ",:" + EthereumTransaction.TRANSACTION_HASH
            + ") "
            + "ON CONFLICT (transaction_hash) DO NOTHING", nativeQuery = true)
    void saveAllIfNotExists(List<Integer> value, List<BigInteger> input, List<Integer> logs_count, List<BigInteger> contract_address, List<BigInteger> to, List<BigInteger> from_,
            List<BigInteger> block_number, List<BigInteger> block_hash, List<Boolean> transaction_status, List<BigInteger> transaction_hash);

}
