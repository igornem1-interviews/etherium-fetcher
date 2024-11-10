package limechain.ethereum_fetcher.service;

import java.util.List;

import org.springframework.stereotype.Service;

import limechain.ethereum_fetcher.model.Transaction;
import limechain.ethereum_fetcher.model.User;
import limechain.ethereum_fetcher.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final TransactionRepository transactionRepository;

    public List<Transaction> usersTransactions(User user) {
        return transactionRepository.findTransactionsByUser(user);
    }
}
