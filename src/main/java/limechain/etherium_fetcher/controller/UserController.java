package limechain.etherium_fetcher.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import limechain.etherium_fetcher.config.Constants;
import limechain.etherium_fetcher.model.Transaction;
import limechain.etherium_fetcher.model.User;

@RequestMapping(Constants.URI_ROOT)
@RestController
public class UserController {

    @GetMapping("/my")
    public ResponseEntity<List<Transaction>> usersTransactions() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
		User currentUser = (User) authentication.getPrincipal();

        return ResponseEntity.ok(List.copyOf(currentUser.getTransactions()));
	}

}
