package limechain.etherium_fetcher.service;

import java.util.List;

import org.springframework.stereotype.Service;

import limechain.etherium_fetcher.model.Transaction;
import limechain.etherium_fetcher.model.User;
import limechain.etherium_fetcher.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final TransactionRepository transactionRepository;

    public List<Transaction> usersTransactions(User user) {
        return transactionRepository.findTransactionsByUser(user);
    }
}
