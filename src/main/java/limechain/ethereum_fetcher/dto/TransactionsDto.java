package limechain.ethereum_fetcher.dto;

import java.util.Collection;

import limechain.ethereum_fetcher.model.Transaction;
import lombok.Data;

@Data
public class TransactionsDto {
    private Collection<Transaction> transactions;

    public TransactionsDto(Collection<Transaction> transactions) {
        this.transactions = transactions;
    }
}
