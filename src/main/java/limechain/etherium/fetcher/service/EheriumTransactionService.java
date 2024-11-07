package limechain.etherium.fetcher.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Transaction;
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

    public Collection<EthereumTransaction> findByHashList(List<String> transactionHashes) {
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

    public Collection<EthereumTransaction> findByRlphex(String rlphexHashes) {
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

    private Set<EthereumTransaction> getFromBlockChain(Set<BigInteger> transactionHashes) {
        List<EthereumTransaction> transactions = new ArrayList<>();
        for (BigInteger txHash : transactionHashes) {
            Transaction transaction = web3j.ethGetTransactionByHash(txHash.toString()).send().getTransaction().orElse(null);
            if (transaction != null) {
                transactions.add(toEthereumTransaction(transaction)));
            }
        }
    }

    private Set<BigInteger> toBigIntegers(List<String> transactionHashes) {
        return transactionHashes.stream().map(hash -> new BigInteger(hash.substring(HEXAVAL_BEGIN_2), RADIX_16)).collect(Collectors.toSet());
    }

}
