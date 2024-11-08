package limechain.etherium_fetcher.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.rlp.RlpDecoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;
import org.web3j.rlp.RlpType;

import limechain.etherium_fetcher.model.EthereumTransaction;
import limechain.etherium_fetcher.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EheriumTransactionService {

    private final Web3j web3j;
    private final TransactionRepository repository;

    public EheriumTransactionService(@Value("${ethereum.node.url}") String ethereumNodeUrl, TransactionRepository transactionRecordRepository) {
        this.web3j = Web3j.build(new HttpService(ethereumNodeUrl));
        this.repository = transactionRecordRepository;
    }

    public Collection<EthereumTransaction> findAll() {
        return repository.findAll();
    }

    public Collection<EthereumTransaction> findByHashList(List<String> hashes) throws IOException, TransactionException {
        Set<String> sourceTransactions = new HashSet<>(hashes);
        log.debug("Looking transactions at DB for hashes: {}", sourceTransactions);
        List<EthereumTransaction> existingTransactions = repository.findByTransactionHashIn(hashes);
        log.debug("Found transactions at DB for hashes: {}", existingTransactions);
        if (existingTransactions.size() != sourceTransactions.size()) {
            existingTransactions.stream().forEach(t -> sourceTransactions.remove(t.getTransactionHash()));
            log.debug("Transactions not in DB for hashes: {}", sourceTransactions);
            List<EthereumTransaction> remainTransactions = getFromBlockChain(sourceTransactions);

            remainTransactions.forEach(transaction -> {
                try {
                repository.saveOne(transaction);
                } catch (DataIntegrityViolationException de) {
                    Throwable cause = de.getCause();
                    if (cause == null || cause.getClass() != ConstraintViolationException.class
                            || !EthereumTransaction.UQ_TRANSACTION_HASH.equals(((ConstraintViolationException) cause).getConstraintName())) {
                        log.error("Failed to store transaction due to: {}", de, ", transaction: {}", transaction);
                    }
                }
                log.debug("Inserted to DB the transaction: {}", transaction);
            });
            existingTransactions.addAll(remainTransactions);
        }
        return existingTransactions;
    }

    public Collection<EthereumTransaction> findByRlphex(String rlphexHashes) throws IOException, TransactionException {
        return findByHashList(decodeRlpAndGetTransactions(rlphexHashes));
    }

    private byte[] hexStringToByteArray(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    private List<String> decodeRlpAndGetTransactions(String rlphex) {
        byte[] rlpEncodedBytes = hexStringToByteArray(rlphex);

        RlpList rlpList = RlpDecoder.decode(rlpEncodedBytes);
        List<String> transactionHashes = new ArrayList<>();

        RlpType mainElement = rlpList.getValues().get(0);
        if (mainElement instanceof RlpList) {
            RlpList mainList = (RlpList) mainElement;
            for (RlpType rlpType : mainList.getValues()) {
                if (rlpType instanceof RlpString) {
                    RlpString rlpString = (RlpString) rlpType;
                    transactionHashes.add(rlpString.asString());
                }
            }
        } else {
            throw new IllegalArgumentException("Wrong RLP format, exepected RlpList but got " + mainElement);
        }

        return transactionHashes;
    }

    private List<EthereumTransaction> getFromBlockChain(Set<String> transactionHashes) throws IOException, TransactionException {
        log.debug("Looking transactions from blockchain for list:" + transactionHashes);
        List<EthereumTransaction> transactions = new ArrayList<>();
        for (String txHash : transactionHashes) {
            Transaction tx = web3j.ethGetTransactionByHash(txHash).send().getTransaction().orElse(null);
            if (tx != null) {
                TransactionReceipt txReceipt = web3j.ethGetTransactionReceipt(tx.getHash()).send().getTransactionReceipt().orElse(null);
                EthereumTransaction ethereumTransaction = toEthereumTransaction(tx, txReceipt);
                transactions.add(ethereumTransaction);
                log.debug("Got transaction via web3: {}", ethereumTransaction);
            }
        }
        return transactions;
    }

    public EthereumTransaction toEthereumTransaction(Transaction tx, TransactionReceipt txReceipt) throws IOException, TransactionException {
        boolean transactionStatus = txReceipt != null && txReceipt.isStatusOK() ? true : false;
        int logsCount = txReceipt != null ? txReceipt.getLogs().size() : 0;
        return new EthereumTransaction(null, tx.getHash(), transactionStatus, tx.getBlockHash(), tx.getBlockNumber(), tx.getFrom(), tx.getTo(), tx.getCreates(), logsCount,
                tx.getInput(), tx.getValue());
    }

}
