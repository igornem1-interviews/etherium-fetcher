package limechain.ethereum_fetcher.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.ExpiredJwtException;

public class JwtServiceTest {

    private JwtService jwtService;
    
    private String secretKey = "39c2ee9efbf57ade91d9736ec8a4b59d80be8fb50c936747cfae5fe9cb25873e";
    private long jwtExpiration = 1000L;

    @BeforeEach
    public void setUp() {
        jwtService = new JwtService();
        jwtService.secretKey = secretKey;
        jwtService.jwtExpiration = jwtExpiration;
    }

    @Test
    public void testGenerateAndValidateToken() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testUser");

        String token = jwtService.generateToken(userDetails);

        String username = jwtService.extractUsername(token);
        assertEquals("testUser", username);
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    public void testTokenExpiration() throws InterruptedException {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testUser");

        String token = jwtService.generateToken(userDetails);
        Thread.sleep(jwtExpiration + 1000);
        assertThrows(ExpiredJwtException.class, () -> {
            jwtService.isTokenValid(token, userDetails);
        });
    }
}

