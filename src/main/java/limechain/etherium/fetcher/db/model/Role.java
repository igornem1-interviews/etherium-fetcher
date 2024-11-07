package limechain.etherium.fetcher.db.model;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
	ADMIN, OTP, SUBSCRIBE, PAID;

	@Override
	public String getAuthority() {
		return name();
	}
}