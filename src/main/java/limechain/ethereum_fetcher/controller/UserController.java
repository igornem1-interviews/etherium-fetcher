package limechain.ethereum_fetcher.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import limechain.ethereum_fetcher.config.Constants;
import limechain.ethereum_fetcher.dto.TransactionsDto;
import limechain.ethereum_fetcher.model.Transaction;
import limechain.ethereum_fetcher.model.User;
import limechain.ethereum_fetcher.service.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(Constants.URI_ROOT)
public class UserController {
    private static final String URI_MY = "/my";
    private final UserService userService;

    @GetMapping(URI_MY)
    public ResponseEntity<List<Transaction>> usersTransactions() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return new ResponseEntity(new TransactionsDto(userService.usersTransactions((User) authentication.getPrincipal())), HttpStatus.OK);
	}
}
