package limechain.ethereum_fetcher.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.protocol.exceptions.TransactionException;

import limechain.ethereum_fetcher.config.Constants;
import limechain.ethereum_fetcher.model.Transaction;
import limechain.ethereum_fetcher.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(Constants.URI_ROOT)
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private static final String URI_ALL = "/all";
    private static final String URI_ETH = "/eth";
    private static final String PARAM_RLPHEX = "rlphex";
    private static final String PARAM_TRANSACTION_HASHES = "transactionHashes";

    private final TransactionService service;

    @GetMapping(URI_ALL)
    ResponseEntity<Collection<Transaction>> findAll() {
        return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
    }

    @GetMapping(URI_ETH)
    ResponseEntity<Collection<Transaction>> findByHashList(@RequestParam(value = PARAM_TRANSACTION_HASHES) List<String> transactionHashes) {
        if (CollectionUtils.isEmpty(transactionHashes)) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        } else {
            try {
                return new ResponseEntity<>(service.findByHashList(transactionHashes), HttpStatus.OK);
            } catch (IOException | TransactionException e) {
                log.error(e.getMessage(), e);
                return new ResponseEntity<>(Collections.emptyList(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @GetMapping(URI_ETH + "/{" + PARAM_RLPHEX + "}")
    ResponseEntity<Collection<Transaction>> findByRlphex(@PathVariable(name = PARAM_RLPHEX) String rlphexHashes) throws IOException, TransactionException {
        if (ObjectUtils.isEmpty(rlphexHashes)) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        } else {
            try {
                return new ResponseEntity<>(service.findByRlphex(rlphexHashes), HttpStatus.OK);
            } catch (IOException | TransactionException e) {
                log.error(e.getMessage(), e);
                return new ResponseEntity<>(Collections.emptyList(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

    }
}