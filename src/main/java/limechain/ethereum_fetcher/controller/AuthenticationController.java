package limechain.ethereum_fetcher.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import limechain.ethereum_fetcher.config.Constants;
import limechain.ethereum_fetcher.dto.LoginDto;
import limechain.ethereum_fetcher.dto.LoginResponseDto;
import limechain.ethereum_fetcher.service.AuthenticationService;
import limechain.ethereum_fetcher.service.JwtService;
import lombok.RequiredArgsConstructor;

@RequestMapping(Constants.URI_ROOT)
@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    static final String URI_AUTH = "/authenticate";

    @PostMapping(AuthenticationController.URI_AUTH)
	public ResponseEntity<LoginResponseDto> authenticate(@RequestBody LoginDto loginUserDto) {
        UserDetails authenticatedUser = null;
        try {
            authenticatedUser = authenticationService.authenticate(loginUserDto);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        LoginResponseDto loginResponse = new LoginResponseDto(jwtService.generateToken(authenticatedUser));
		return ResponseEntity.ok(loginResponse);
	}
}
