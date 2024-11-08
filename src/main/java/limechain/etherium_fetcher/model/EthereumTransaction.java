package limechain.etherium_fetcher.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = EthereumTransaction.TABLE_NAME)
@EqualsAndHashCode(of = "id")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EthereumTransaction {
    public static final String VALUE_ = "value_";
    public static final String INPUT_ = "input_";
    public static final String LOGS_COUNT = "logs_count";
    public static final String CONTRACT_ADDRESS = "contract_address";
    public static final String TO_ = "to_";
    public static final String FROM_ = "from_";
    public static final String BLOCK_NUMBER = "block_number";
    public static final String BLOCK_HASH = "block_hash";
    public static final String TRANSACTION_STATUS = "transaction_status";
    public static final String TRANSACTION_HASH = "transaction_hash";
    public static final String TABLE_NAME = "ethereum_transaction";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private Long id;

    @Column(name = TRANSACTION_HASH, unique = true, columnDefinition = "TEXT") private String transactionHash;
    @Column(name = TRANSACTION_STATUS) private Boolean transactionStatus;
    @Column(name = BLOCK_HASH, columnDefinition = "TEXT") private String blockHash;
    // @Column(name = BLOCK_NUMBER, precision = 40, scale = 0) private BigDecimal
    // blockNumber;
    @Column(name = FROM_, columnDefinition = "TEXT") private String from;
    @Column(name = TO_, columnDefinition = "TEXT") private String to;
    @Column(name = CONTRACT_ADDRESS, columnDefinition = "TEXT") private String contractAddress;
    @Column(name = LOGS_COUNT) private Integer logsCount;
    @Column(name = INPUT_, columnDefinition = "TEXT") private String input;
    // @Column(name = VALUE_, precision = 40, scale = 0) private BigDecimal value;

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return super.toString();
        }
    }
}