package limechain.ethereum_fetcher.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDto {
	private String token;
    private long expiresInMs;
}
