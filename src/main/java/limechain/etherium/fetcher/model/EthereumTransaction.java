package limechain.etherium.fetcher.model;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Entity
@Table(name = EthereumTransaction.TABLE_NAME)
@EqualsAndHashCode(of = "id")
@Getter
@AllArgsConstructor
public class EthereumTransaction {
    public static final String VALUE = "value";
    public static final String INPUT = "input";
    public static final String LOGS_COUNT = "logs_count";
    public static final String CONTRACT_ADDRESS = "contract_address";
    public static final String TO = "to";
    public static final String FROM = "from";
    public static final String BLOCK_NUMBER = "block_number";
    public static final String BLOCK_HASH = "block_hash";
    public static final String TRANSACTION_STATUS = "transaction_status";
    public static final String TRANSACTION_HASH = "transaction_hash";
    public static final String TABLE_NAME = "ethereum_transaction";

    private @Id @GeneratedValue Long id;

    @Column(name = TRANSACTION_HASH, unique = true) private BigInteger transactionHash;// :"0xcbc920e7bb89cbcb540a469a16226bf1057825283ab8eac3f45d00811eef8a64",
    @Column(name = TRANSACTION_STATUS) private boolean transactionStatus; // ":1,
    @Column(name = BLOCK_HASH) private BigInteger blockHash; // ":"0xc5a3664f031da2458646a01e18e6957fd1f43715524d94b7336a004b5635837d",
    @Column(name = BLOCK_NUMBER) private BigInteger blockNumber; // ":5702816,
    @Column(name = FROM) private BigInteger from; // ":"0xd5e6f34bbd4251195c03e7bf3660677ed2315f70",
    @Column(name = TO) private BigInteger to; // ":"0x4c16d8c078ef6b56700c1be19a336915962df072",
    @Column(name = CONTRACT_ADDRESS) private BigInteger contractAddress; // :"0xb5679de944a79732a75ce556191df11f489448d5",
    @Column(name = LOGS_COUNT) private int logsCount; // ":1,
    @Column(name = INPUT) private BigInteger input; // ":"0x6a627842000000000000000000000000d5e6f34bbd4251195c03e7bf3660677ed2315f70",
    @Column(name = VALUE) private int value; // ":"0"
}