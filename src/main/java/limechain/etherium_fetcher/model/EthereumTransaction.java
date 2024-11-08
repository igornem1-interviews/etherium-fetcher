package limechain.etherium_fetcher.model;

import java.math.BigInteger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = EthereumTransaction.TABLE_NAME, uniqueConstraints = {
        @UniqueConstraint(name = EthereumTransaction.UQ_TRANSACTION_HASH, columnNames = { EthereumTransaction.TRANSACTION_HASH }) })
@EqualsAndHashCode(of = "id")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EthereumTransaction {

    private static final String FROM_ = "from_";
    private static final String TO_ = "to_";
    private static final String TABLE_NAME = "ethereum_transaction";
    static final String TRANSACTION_HASH = "transaction_hash";
    public static final String UQ_TRANSACTION_HASH = "UQ_" + EthereumTransaction.TRANSACTION_HASH;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(columnDefinition = "serial")
    private Long id;

    @Column(name = TRANSACTION_HASH, columnDefinition = "TEXT") private String transactionHash;
    private Boolean transactionStatus;
    @Column(columnDefinition = "TEXT") private String blockHash;
    private BigInteger blockNumber;
    @Column(name = FROM_, columnDefinition = "TEXT") private String from;
    @Column(name = TO_, columnDefinition = "TEXT") private String to;
    @Column(columnDefinition = "TEXT") private String contractAddress;
    private Integer logsCount;
    @Column(columnDefinition = "TEXT") private String input;
    private BigInteger value;

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return super.toString();
        }
    }
}