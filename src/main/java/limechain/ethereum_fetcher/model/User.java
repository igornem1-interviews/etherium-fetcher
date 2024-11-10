package limechain.ethereum_fetcher.model;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = User.TABLE_NAME)
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity implements UserDetails {
    private static final String FIELD_ID = "id";
    private static final String FIELD_TRX_ID = "trx_id";
    private static final String FIELD_USER_ID = "user_id";
    private static final String TABLE_USER_TRANSACTIONS = "user_transactions";
    static final String TABLE_NAME = "users";

	@Column(unique = true, length = 100, nullable = false)
	private String username;

	@Column(nullable = false)
	private String password;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = TABLE_USER_TRANSACTIONS, 
               joinColumns        = @JoinColumn(name = FIELD_USER_ID, referencedColumnName = FIELD_ID), 
               inverseJoinColumns = @JoinColumn(name = FIELD_TRX_ID, referencedColumnName = FIELD_ID),
               indexes = {@Index(name = "idx_"+FIELD_USER_ID, columnList = FIELD_USER_ID)})
    private Set<Transaction> transactions;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}