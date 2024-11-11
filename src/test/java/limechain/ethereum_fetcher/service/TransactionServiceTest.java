package limechain.ethereum_fetcher.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;

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
    @Mock
    private Authentication authentication;

    private TransactionService transactionService;

    @BeforeAll
    public static void setUpBefore() {
        mockStatic(SecurityContextHolder.class);
        mockStatic(Web3j.class);
    }

    @BeforeEach
    public void setUpBeforeEach() {
        MockitoAnnotations.openMocks(this);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(SecurityContextHolder.getContext()).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(Web3j.build(any(HttpService.class))).thenReturn(web3j);
        this.transactionService = new TransactionService("", transactionRepository, userRepository);
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

        when(authentication.isAuthenticated()).thenReturn(false);

        List<String> hashes = Collections.singletonList(HASH1);
        when(transactionRepository.findByHashIn(hashes)).thenReturn(new ArrayList<Transaction>());

        TransactionReceipt receipt = mock(TransactionReceipt.class);
        when(receipt.isStatusOK()).thenReturn(true);

        EthGetTransactionReceipt ethGetTransactionReceipt = mock(EthGetTransactionReceipt.class);
        when(ethGetTransactionReceipt.getTransactionReceipt()).thenReturn(Optional.of(receipt));

        org.web3j.protocol.core.methods.response.Transaction web3Transaction = mock(org.web3j.protocol.core.methods.response.Transaction.class);
        org.web3j.protocol.core.methods.response.EthTransaction ethTransaction = mock(EthTransaction.class);
        when(ethTransaction.getTransaction()).thenReturn(Optional.of(web3Transaction));

        when(web3Transaction.getHash()).thenReturn(HASH1);
        when(web3Transaction.getFrom()).thenReturn("from");
        when(web3Transaction.getTo()).thenReturn("to");

        Request requestTransaction = mock(Request.class);
        when(web3j.ethGetTransactionByHash(HASH1)).thenReturn(requestTransaction);
        when(requestTransaction.send()).thenReturn(ethTransaction);

        Request requestTransactionReceipt = mock(Request.class);
        when(web3j.ethGetTransactionReceipt(HASH1)).thenReturn(requestTransactionReceipt);
        when(requestTransactionReceipt.send()).thenReturn(ethGetTransactionReceipt);

        Collection<Transaction> result = transactionService.findByHashList(hashes);

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getHash()).isEqualTo(HASH1);
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

