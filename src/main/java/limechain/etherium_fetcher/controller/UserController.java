package limechain.etherium_fetcher.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import limechain.etherium_fetcher.config.Constants;
import limechain.etherium_fetcher.model.User;

@RequestMapping(Constants.URI_ROOT)
@RestController
public class UserController {

    @GetMapping("/my")
	public ResponseEntity<User> authenticatedUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		User currentUser = (User) authentication.getPrincipal();

		return ResponseEntity.ok(currentUser);
	}

}
