package limechain.etherium.fetcher.model;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
	ADMIN, OTP, SUBSCRIBE, PAID;

	@Override
	public String getAuthority() {
		return name();
	}
}