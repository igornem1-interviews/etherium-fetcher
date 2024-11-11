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
import java.util.HashSet;
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
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;

import limechain.ethereum_fetcher.model.Transaction;
import limechain.ethereum_fetcher.model.User;
import limechain.ethereum_fetcher.repository.TransactionRepository;
import limechain.ethereum_fetcher.repository.UserRepository;

public class TransactionServiceTest {
    private static final int LOGS_COUNT = 5;
    private static final String ALICE = "alice";
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
        when(receipt.getLogs()).thenReturn(Collections.nCopies(LOGS_COUNT, new Log()));

        EthGetTransactionReceipt ethGetTransactionReceipt = mock(EthGetTransactionReceipt.class);
        when(ethGetTransactionReceipt.getTransactionReceipt()).thenReturn(Optional.of(receipt));

        org.web3j.protocol.core.methods.response.Transaction web3Transaction = mock(org.web3j.protocol.core.methods.response.Transaction.class);
        when(web3Transaction.getHash()).thenReturn(HASH1);
        int i = 0;
        when(web3Transaction.getBlockHash()).thenReturn(HASH1 + i++);
        when(web3Transaction.getBlockNumber()).thenReturn(BigInteger.ONE);
        when(web3Transaction.getFrom()).thenReturn(HASH1 + i++);
        when(web3Transaction.getTo()).thenReturn(HASH1 + i++);
        when(web3Transaction.getCreates()).thenReturn(HASH1 + i++);
        when(web3Transaction.getInput()).thenReturn(HASH1 + i++);
        when(web3Transaction.getValue()).thenReturn(BigInteger.TWO);
        when(web3Transaction.getValue()).thenReturn(BigInteger.TWO);

        mockWeb3j(web3Transaction);

        Request requestTransactionReceipt = mock(Request.class);
        when(web3j.ethGetTransactionReceipt(HASH1)).thenReturn(requestTransactionReceipt);
        when(requestTransactionReceipt.send()).thenReturn(ethGetTransactionReceipt);

        Collection<Transaction> result = transactionService.findByHashList(hashes);

        assertThat(result).hasSize(1);

        Transaction trx = result.iterator().next();
        assertThat(trx.getHash()).isEqualTo(HASH1);
        i = 0;
        assertThat(web3Transaction.getBlockHash()).isEqualTo(HASH1 + i++);
        assertThat(trx.getStatus()).isEqualTo(Boolean.TRUE);
        assertThat(trx.getLogsCount()).isEqualTo(LOGS_COUNT);
        assertThat(trx.getBlockNumber()).isEqualTo(BigInteger.ONE);
        assertThat(trx.getFrom()).isEqualTo(HASH1 + i++);
        assertThat(trx.getTo()).isEqualTo(HASH1 + i++);
        assertThat(trx.getContractAddress()).isEqualTo(HASH1 + i++);
        assertThat(trx.getInput()).isEqualTo(HASH1 + i++);
        assertThat(trx.getValue()).isEqualTo(BigInteger.TWO);
    }

    @Test
    public void testFindByHashList_NoTransactionsFromDbNeitherBlockchain() throws IOException, TransactionException {

        when(authentication.isAuthenticated()).thenReturn(false);

        List<String> hashes = Collections.singletonList(HASH1);
        when(transactionRepository.findByHashIn(hashes)).thenReturn(new ArrayList<Transaction>());

        mockWeb3j(null);

        Collection<Transaction> result = transactionService.findByHashList(hashes);

        assertThat(result).hasSize(0);
    }

    @Test
    public void testFindByHashList_UserAuthenticated() throws IOException, TransactionException {
        when(authentication.isAuthenticated()).thenReturn(true);
        User user = new User(ALICE, ALICE, new HashSet<Transaction>());
        when(authentication.getPrincipal()).thenReturn(true);

        List<String> hashes = Collections.singletonList(HASH1);
        when(transactionRepository.findByHashIn(hashes)).thenReturn(new ArrayList<Transaction>());

        TransactionReceipt receipt = mock(TransactionReceipt.class);
        when(receipt.isStatusOK()).thenReturn(true);

        EthGetTransactionReceipt ethGetTransactionReceipt = mock(EthGetTransactionReceipt.class);
        when(ethGetTransactionReceipt.getTransactionReceipt()).thenReturn(Optional.of(receipt));

        org.web3j.protocol.core.methods.response.Transaction web3Transaction = mock(org.web3j.protocol.core.methods.response.Transaction.class);
        when(web3Transaction.getHash()).thenReturn(HASH1);

        mockWeb3j(web3Transaction);

        Request requestTransactionReceipt = mock(Request.class);
        when(web3j.ethGetTransactionReceipt(HASH1)).thenReturn(requestTransactionReceipt);
        when(requestTransactionReceipt.send()).thenReturn(ethGetTransactionReceipt);

        Collection<Transaction> result = transactionService.findByHashList(hashes);

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getHash()).isEqualTo(HASH1);
    }

    @Test
    public void testDecodeRlpAndGetTransactions() {
        String rlpHex = "f884a0fc2b3b6db38a51db3b9cb95de29b719de8deb99630626e4b4b99df056ffb7f2ea048603f7adff7fbfc2a10b22a6710331ee68f2e4d1cd73a584d57c8821df79356a0cbc920e7bb89cbcb540a469a16226bf1057825283ab8eac3f45d00811eef8a64a06d604ffc644a282fca8cb8e778e1e3f8245d8bd1d49326e3016a3c878ba0cbbd";
        List<String> decodedHashes = Arrays.asList("0xfc2b3b6db38a51db3b9cb95de29b719de8deb99630626e4b4b99df056ffb7f2e", 
                "0xcbc920e7bb89cbcb540a469a16226bf1057825283ab8eac3f45d00811eef8a64",
                "0x6d604ffc644a282fca8cb8e778e1e3f8245d8bd1d49326e3016a3c878ba0cbbd",
                "0x48603f7adff7fbfc2a10b22a6710331ee68f2e4d1cd73a584d57c8821df79356");
        List<String> result = transactionService.decodeRlpAndGetTransactions(rlpHex);
        assertThat(result).containsExactlyInAnyOrderElementsOf(decodedHashes);
    }

    private Transaction createTransaction(String hash) {
        int i = 0;
        return new Transaction(hash, Boolean.TRUE, hash + i++, BigInteger.ONE, hash + i++, hash + i++, hash + i++, LOGS_COUNT, hash + i++, BigInteger.TWO, null);
    }

    private void mockWeb3j(org.web3j.protocol.core.methods.response.Transaction web3Transaction) throws IOException {
        Request requestTransaction = mock(Request.class);
        when(web3j.ethGetTransactionByHash(HASH1)).thenReturn(requestTransaction);
        org.web3j.protocol.core.methods.response.EthTransaction ethTransaction = mock(EthTransaction.class);
        when(ethTransaction.getTransaction()).thenReturn(Optional.ofNullable(web3Transaction));
        when(requestTransaction.send()).thenReturn(ethTransaction);
    }
}

