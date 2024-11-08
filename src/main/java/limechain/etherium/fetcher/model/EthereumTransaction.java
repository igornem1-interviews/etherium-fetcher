package limechain.etherium.fetcher.model;

import java.math.BigInteger;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = EthereumTransaction.TABLE_NAME)
@EqualsAndHashCode(of = "id")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EthereumTransaction {
    public static final String SEQUENCE_NAME = "sequence_generator";
    public static final String VALUE = "value";
    public static final String INPUT = "input";
    public static final String LOGS_COUNT = "logs_count";
    public static final String CONTRACT_ADDRESS = "contract_address";
    public static final String TO_ = "to_";
    public static final String FROM_ = "from_";
    public static final String BLOCK_NUMBER = "block_number";
    public static final String BLOCK_HASH = "block_hash";
    public static final String TRANSACTION_STATUS = "transaction_status";
    public static final String TRANSACTION_HASH = "transaction_hash";
    public static final String TABLE_NAME = "ethereum_transaction";

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 50)
    @Id
    @Column(columnDefinition = "serial")
    private Long id;

    @Column(name = TRANSACTION_HASH, unique = true, columnDefinition = "TEXT") private String transactionHash;// :"0xcbc920e7bb89cbcb540a469a16226bf1057825283ab8eac3f45d00811eef8a64",
    @Column(name = TRANSACTION_STATUS) private Boolean transactionStatus; // ":1,
    @Column(name = BLOCK_HASH, columnDefinition = "TEXT") private String blockHash; // ":"0xc5a3664f031da2458646a01e18e6957fd1f43715524d94b7336a004b5635837d",
    @Column(name = BLOCK_NUMBER) private BigInteger blockNumber; // ":5702816,
    @Column(name = FROM_, columnDefinition = "TEXT") private String from; // ":"0xd5e6f34bbd4251195c03e7bf3660677ed2315f70",
    @Column(name = TO_, columnDefinition = "TEXT") private String to; // ":"0x4c16d8c078ef6b56700c1be19a336915962df072",
    @Column(name = CONTRACT_ADDRESS, columnDefinition = "TEXT") private String contractAddress; // :"0xb5679de944a79732a75ce556191df11f489448d5",
    @Column(name = LOGS_COUNT) private int logsCount; // ":1,
    @Column(name = INPUT, columnDefinition = "TEXT") private String input; // ":"0x6a627842000000000000000000000000d5e6f34bbd4251195c03e7bf3660677ed2315f70",
    @Column(name = VALUE) private BigInteger value; // ":"0"
}