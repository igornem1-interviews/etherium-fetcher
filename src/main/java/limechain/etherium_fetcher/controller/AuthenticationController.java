package limechain.etherium_fetcher.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import limechain.etherium_fetcher.config.Constants;
import limechain.etherium_fetcher.dto.LoginDto;
import limechain.etherium_fetcher.dto.LoginResponseDto;
import limechain.etherium_fetcher.service.AuthenticationService;
import limechain.etherium_fetcher.service.JwtService;
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
        UserDetails authenticatedUser = authenticationService.authenticate(loginUserDto);
		String jwtToken = jwtService.generateToken(authenticatedUser);
		LoginResponseDto loginResponse = new LoginResponseDto(jwtToken, jwtService.getExpirationTime());
		return ResponseEntity.ok(loginResponse);
	}
}
