package limechain.etherium_fetcher.model;

import java.math.BigInteger;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = Transaction.TABLE_NAME, uniqueConstraints = { @UniqueConstraint(name = Transaction.UQ_TRANSACTION_HASH, columnNames = { Transaction.TRANSACTION_HASH }) })
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Transaction extends BaseEntity {

    private static final String FROM_ = "from_";
    private static final String TO_ = "to_";
    private static final String TABLE_NAME = "transaction";
    static final String TRANSACTION_HASH = "hash";
    public static final String UQ_TRANSACTION_HASH = "UQ_" + Transaction.TRANSACTION_HASH;

    @Column(name = TRANSACTION_HASH, columnDefinition = "TEXT")
    private String hash;

    private Boolean status;
    @Column(columnDefinition = "TEXT")
    private String blockHash;
    private BigInteger blockNumber;
    @Column(name = FROM_, columnDefinition = "TEXT")
    private String from;
    @Column(name = TO_, columnDefinition = "TEXT")
    private String to;
    @Column(columnDefinition = "TEXT")
    private String contractAddress;
    private Integer logsCount;
    @Column(columnDefinition = "TEXT")
    private String input;
    private BigInteger value;

    @ManyToMany(mappedBy = "transactions", fetch = FetchType.EAGER)
    private Set<User> users;

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return super.toString();
        }
    }
}