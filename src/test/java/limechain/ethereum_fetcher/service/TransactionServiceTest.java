package limechain.ethereum_fetcher.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;

import limechain.ethereum_fetcher.model.Transaction;
import limechain.ethereum_fetcher.model.User;
import limechain.ethereum_fetcher.repository.TransactionRepository;
import limechain.ethereum_fetcher.repository.UserRepository;

public class TransactionServiceTest {
    private static final String HASH1 = "hash1";
    private static final String HASH2 = "hash2";
    private static final String HASH3 = "hash3";

    @Mock
    private Web3j web3j;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindByHashList_ExistingTransactions() throws IOException, TransactionException {
        List<String> hashes = Arrays.asList(HASH1, HASH2);

        Transaction existingTransaction1 = createTransaction(HASH1);
        Transaction existingTransaction2 = createTransaction(HASH2);
        when(transactionRepository.findByHashIn(hashes)).thenReturn(Arrays.asList(existingTransaction1, existingTransaction2));

        Collection<Transaction> result = transactionService.findByHashList(hashes);

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(existingTransaction1, existingTransaction2);
    }

    @Test
    public void testFindByHashList_NewTransactionsFromBlockchain() throws IOException, TransactionException {
        String transactionHash = HASH1;
        List<String> hashes = Collections.singletonList(transactionHash);

        when(transactionRepository.findByHashIn(hashes)).thenReturn(Collections.emptyList());

        TransactionReceipt receipt = mock(TransactionReceipt.class);
        when(receipt.isStatusOK()).thenReturn(true);

        EthGetTransactionReceipt ethGetTransactionReceipt = mock(EthGetTransactionReceipt.class);
        when(ethGetTransactionReceipt.getTransactionReceipt()).thenReturn(Optional.of(receipt));

        org.web3j.protocol.core.methods.response.Transaction web3Transaction = mock(org.web3j.protocol.core.methods.response.Transaction.class);
        org.web3j.protocol.core.methods.response.EthTransaction ethTransaction = mock(EthTransaction.class);
        when(ethTransaction.getTransaction()).thenReturn(Optional.of(web3Transaction));

        when(web3Transaction.getHash()).thenReturn(transactionHash);
        when(web3Transaction.getFrom()).thenReturn("from");
        when(web3Transaction.getTo()).thenReturn("to");

        when(web3j.ethGetTransactionByHash(transactionHash).send()).thenReturn(ethTransaction);
        when(web3j.ethGetTransactionReceipt(transactionHash).send()).thenReturn(ethGetTransactionReceipt);

        Collection<Transaction> result = transactionService.findByHashList(hashes);

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getHash()).isEqualTo(transactionHash);
    }

    @Test
    public void testFindByHashList_UserAuthenticated() throws IOException, TransactionException {
        // Мокаем данные
        String transactionHash = "0x123";
        List<String> hashes = Collections.singletonList(transactionHash);
        User user = new User();
        user.setId(1L);
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        TransactionReceipt receipt = mock(TransactionReceipt.class);
        when(receipt.isStatusOK()).thenReturn(true);
        EthGetTransactionReceipt ethGetTransactionReceipt = mock(EthGetTransactionReceipt.class);
        when(ethGetTransactionReceipt.getTransactionReceipt()).thenReturn(Optional.of(receipt));

        org.web3j.protocol.core.methods.response.Transaction web3Transaction = mock(org.web3j.protocol.core.methods.response.Transaction.class);
        org.web3j.protocol.core.methods.response.EthTransaction ethTransaction = mock(EthTransaction.class);
        when(ethTransaction.getTransaction()).thenReturn(Optional.of(web3Transaction));

        Collection<Transaction> result = transactionService.findByHashList(hashes);

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getHash()).isEqualTo(transactionHash);
    }

    @Test
    public void testDecodeRlpAndGetTransactions() {
        String rlpHex = "0x80";
        List<String> decodedHashes = Arrays.asList("0x123", "0x456");
        List<String> result = transactionService.decodeRlpAndGetTransactions(rlpHex);
        assertThat(result).isEqualTo(decodedHashes);
    }

    private Transaction createTransaction(String hash) {
        return new Transaction(hash, Boolean.TRUE, hash, BigInteger.TWO, hash, hash, hash, 5, hash, BigInteger.TEN, null);
    }

}

