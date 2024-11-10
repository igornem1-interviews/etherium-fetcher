package limechain.ethereum_fetcher.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import limechain.ethereum_fetcher.dto.LoginDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;

    public UserDetails authenticate(LoginDto input) {
        return (UserDetails) (authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(input.getUsername(), input.getPassword()))).getPrincipal();
    }
}
