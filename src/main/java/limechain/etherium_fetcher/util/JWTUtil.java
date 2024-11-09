package limechain.etherium_fetcher.util;

import java.util.Date;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JWTUtil {

	private static final String SECRET_KEY = "secretkey"; // Ваш секретный ключ

	public static String generateToken(String username) {
		return Jwts.builder().setSubject(username).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + 86400000)) // Срок действия токена - 24 часа
				.signWith(SignatureAlgorithm.HS512, SECRET_KEY).compact();
	}

	public static String getUsernameFromToken(String token) {
		return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getSubject();
	}
}

