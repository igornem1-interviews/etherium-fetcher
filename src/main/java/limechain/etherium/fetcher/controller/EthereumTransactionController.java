package limechain.etherium.fetcher.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.protocol.exceptions.TransactionException;

import limechain.etherium.fetcher.db.model.EthereumTransaction;
import limechain.etherium.fetcher.service.EheriumTransactionService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(EthereumTransactionController.URI_ROOT)
@RequiredArgsConstructor
public class EthereumTransactionController {

    static final String URI_ROOT = "/lime";
    private static final String URI_ALL = "/all";
	private static final String URI_ETH = "/eth";
    private static final String PARAM_RLPHEX = "rlphex";
	private static final String PARAM_TRANSACTION_HASHES = "transactionHashes";

    private final EheriumTransactionService service;

    @GetMapping(URI_ALL)
    ResponseEntity<Collection<EthereumTransaction>> findAll() {
        return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
    }

    @GetMapping(URI_ETH)
    ResponseEntity<Collection<EthereumTransaction>> findByHashList(@RequestParam(value = PARAM_TRANSACTION_HASHES) List<String> transactionHashes)
            throws IOException, TransactionException {
        return new ResponseEntity<>(service.findByHashList(transactionHashes), HttpStatus.OK);
    }

    @GetMapping(URI_ETH + "/{" + PARAM_RLPHEX + "}")
    ResponseEntity<Collection<EthereumTransaction>> findByRlphex(@PathVariable String rlphexHashes) throws IOException, TransactionException {
        return new ResponseEntity<>(service.findByRlphex(rlphexHashes), HttpStatus.OK);
    }
}