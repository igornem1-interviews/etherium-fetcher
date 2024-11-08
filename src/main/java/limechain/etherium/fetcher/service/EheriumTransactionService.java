package limechain.etherium.fetcher.service;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.rlp.RlpDecoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;
import org.web3j.rlp.RlpType;

import limechain.etherium.fetcher.db.model.EthereumTransaction;
import limechain.etherium.fetcher.db.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EheriumTransactionService {

    private final Web3j web3j;

    private static final int HEXAVAL_BEGIN_2 = 2;
    private static final int RADIX_16 = 16;

    private final TransactionRepository repository;

    public Collection<EthereumTransaction> findAll() {
        return repository.findAll();
    }

    public Collection<EthereumTransaction> findByHashList(List<String> transactionHashes) throws IOException, TransactionException {
        Set<BigInteger> sourceTransactions = toBigIntegers(transactionHashes);
        Set<EthereumTransaction> existingTransactions = repository.findByTransactionHashIn(sourceTransactions);
        if (existingTransactions.size() != sourceTransactions.size()) {
            existingTransactions.stream().forEach(t -> sourceTransactions.remove(t.getTransactionHash()));
            Set<EthereumTransaction> remainTransactions = getFromBlockChain(sourceTransactions);
            repository.saveAll(remainTransactions);
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

    private Set<EthereumTransaction> getFromBlockChain(Set<BigInteger> transactionHashes) throws IOException, TransactionException {
        Set<EthereumTransaction> transactions = new HashSet<>();
        for (BigInteger txHash : transactionHashes) {
            Transaction tx = web3j.ethGetTransactionByHash(txHash.toString()).send().getTransaction().orElse(null);
            if (tx != null) {
                TransactionReceipt txReceipt = web3j.ethGetTransactionReceipt(tx.getHash()).send().getTransactionReceipt().orElse(null);
                transactions.add(toEthereumTransaction(tx, txReceipt));
            }
        }
        return transactions;
    }

    public EthereumTransaction toEthereumTransaction(Transaction tx, TransactionReceipt txReceipt) throws IOException, TransactionException {
        BigInteger transactionHash = new BigInteger(tx.getHash().substring(2), 16);
        boolean transactionStatus = txReceipt != null && txReceipt.isStatusOK() ? true : false;
        BigInteger blockHash = tx.getBlockHash() != null ? new BigInteger(tx.getBlockHash().substring(2), 16) : null;
        BigInteger blockNumber = tx.getBlockNumber() != null ? tx.getBlockNumber() : BigInteger.ZERO;
        BigInteger from = tx.getFrom() != null ? new BigInteger(tx.getFrom().substring(2), 16) : null;
        BigInteger to = tx.getTo() != null ? new BigInteger(tx.getTo().substring(2), 16) : null;
        BigInteger contractAddress = tx.getCreates() != null ? new BigInteger(tx.getCreates().substring(2), 16) : null;
        int logsCount = txReceipt != null ? txReceipt.getLogs().size() : 0;
        BigInteger input = tx.getInput() != null ? new BigInteger(tx.getInput().substring(2), 16) : null;
        int value = tx.getValue() != null ? tx.getValue().intValue() : 0;
        return new EthereumTransaction(null, transactionHash, transactionStatus, blockHash, blockNumber, from, to, contractAddress, logsCount, input, value);
    }

    private Set<BigInteger> toBigIntegers(List<String> transactionHashes) {
        return transactionHashes.stream().map(hash -> new BigInteger(hash.substring(HEXAVAL_BEGIN_2), RADIX_16)).collect(Collectors.toSet());
    }

}
