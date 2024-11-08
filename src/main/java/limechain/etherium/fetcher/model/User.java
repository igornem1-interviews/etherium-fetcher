package limechain.etherium.fetcher.model;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

@SuppressWarnings("serial")
@Entity(name = "users")
@SequenceGenerator(name = BaseEntity.ID_GEN, sequenceName = "user_seq", allocationSize = 1)
public class User extends BaseEntity implements UserDetails {

    @Transient
    private org.springframework.security.core.userdetails.User securityDetails;

    public User() {

    }

    public User(@Email(message = "*Please provide a valid Email") @NotEmpty(message = "*Please provide an email") String username,
            @Length(min = 5, message = "*Your password must have at least 5 characters") @NotEmpty(message = "*Please provide your password") String password, Set<Role> roles) {
        super();
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    @Email(message = "*Please provide a valid Email")
    @NotEmpty(message = "*Please provide an email")
    private String username;

    @Length(min = 5, message = "*Your password must have at least 5 characters")
    @NotEmpty(message = "*Please provide your password")
    private String password;

    @Transient
    private String passwordConfirm;

    @ElementCollection(targetClass = Role.class)
    @CollectionTable(name = "USER_ROLE", joinColumns = @JoinColumn(name = "USER_ID"))
    @Column(name = "ROLE_ID")
    private Set<Role> roles;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Role... roles) {
        setRoles(Stream.of(roles).collect(Collectors.toSet()));
    }

    private void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void setSecurityDetails(org.springframework.security.core.userdetails.User securityDetails) {
        this.securityDetails = securityDetails;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return securityDetails.getAuthorities();
    }

    @Override
    public boolean isAccountNonExpired() {
        return securityDetails.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return securityDetails.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return securityDetails.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return securityDetails.isEnabled();
    }

    public org.springframework.security.core.userdetails.User getSecurityDetails() {
        return securityDetails;
    }

}
