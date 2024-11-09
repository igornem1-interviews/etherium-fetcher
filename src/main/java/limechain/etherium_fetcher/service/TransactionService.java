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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.rlp.RlpDecoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;
import org.web3j.rlp.RlpType;

import jakarta.transaction.Transactional;
import limechain.etherium_fetcher.model.Transaction;
import limechain.etherium_fetcher.model.User;
import limechain.etherium_fetcher.repository.TransactionRepository;
import limechain.etherium_fetcher.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TransactionService {

    private static final String ETHEREUM_NODE_URL = "${ethereum.node.url}";
    private final Web3j web3j;
    private final TransactionRepository repository;
    private final UserRepository userRepository;

    public TransactionService(@Value(ETHEREUM_NODE_URL) String ethereumNodeUrl, TransactionRepository transactionRecordRepository, UserRepository userRepository) {
        this.web3j = Web3j.build(new HttpService(ethereumNodeUrl));
        this.repository = transactionRecordRepository;
        this.userRepository = userRepository;
    }

    public Collection<Transaction> findAll() {
        return repository.findAll();
    }

    @Transactional
    public Collection<Transaction> findByHashList(List<String> hashes) throws IOException, TransactionException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final User user = authentication.isAuthenticated() ? (User) authentication.getPrincipal() : null;

        Set<String> sourceTransactions = new HashSet<>(hashes);
        log.debug("Looking transactions at DB for hashes: {}", sourceTransactions);
		List<Transaction> existingTransactions = repository.findByHashIn(hashes);
        log.debug("Found transactions at DB for hashes: {}", existingTransactions);
        if (user != null) {
            existingTransactions.forEach(trx -> trx.getUsers().add(user));
            user.getTransactions().addAll(existingTransactions);
        } else {
            log.debug("User is not authorized");
        }
        if (existingTransactions.size() != sourceTransactions.size()) {
            existingTransactions.forEach(t -> sourceTransactions.remove(t.getHash()));
            log.debug("Transactions not in DB for hashes: {}", sourceTransactions);
            List<Transaction> remainTransactions = getFromBlockChain(sourceTransactions, user);

            remainTransactions.forEach(transaction -> {
                try {
                    repository.saveOne(transaction);
                } catch (DataIntegrityViolationException de) {
                    Throwable cause = de.getCause();
                    if (cause == null || cause.getClass() != ConstraintViolationException.class
                            || !Transaction.UQ_TRANSACTION_HASH.equals(((ConstraintViolationException) cause).getConstraintName())) {
                        log.error("Failed to store transaction due to: {}", de, ", transaction: {}", transaction);
                        throw new RuntimeException("Failed to store transaction at db. Transaction: " + transaction, de);
                    }
                }
                log.debug("Transaction was stored to DB: {}", transaction);
            });
            existingTransactions.addAll(remainTransactions);
        }
        if (user != null) {
            userRepository.save(user);
        }
        return existingTransactions;
    }

    public Collection<Transaction> findByRlphex(String rlphexHashes) throws IOException, TransactionException {
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

    private List<Transaction> getFromBlockChain(Set<String> transactionHashes, User user) throws IOException, TransactionException {
        log.debug("Looking transactions from blockchain for list:" + transactionHashes);
        List<Transaction> transactions = new ArrayList<>();
        for (String txHash : transactionHashes) {
            org.web3j.protocol.core.methods.response.Transaction tx = web3j.ethGetTransactionByHash(txHash).send().getTransaction().orElse(null);
            if (tx != null) {
                TransactionReceipt txReceipt = web3j.ethGetTransactionReceipt(tx.getHash()).send().getTransactionReceipt().orElse(null);
                Transaction ethereumTransaction = toEthereumTransaction(tx, txReceipt, user);
                transactions.add(ethereumTransaction);
                log.debug("Got transaction via web3: {}", ethereumTransaction);
            }
        }
        return transactions;
    }

    private static Transaction toEthereumTransaction(org.web3j.protocol.core.methods.response.Transaction tx, TransactionReceipt txReceipt, User user)
            throws IOException, TransactionException {
        boolean transactionStatus = txReceipt != null && txReceipt.isStatusOK() ? true : false;
        int logsCount = txReceipt != null ? txReceipt.getLogs().size() : 0;
        return new Transaction(tx.getHash(), transactionStatus, tx.getBlockHash(), tx.getBlockNumber(), tx.getFrom(), tx.getTo(), tx.getCreates(), logsCount,
                tx.getInput(), tx.getValue(), Set.of(user));
    }

}
