package limechain.etherium.fetcher.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import limechain.etherium.fetcher.db.model.Account;
import limechain.etherium.fetcher.db.model.Role;
import limechain.etherium.fetcher.db.repository.TransactionRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private TransactionRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        Account user = userRepository.findByUsername(username);

        if (user == null)
            throw new UsernameNotFoundException(username);

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        for (Role role : user.getRoles()) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role.name()));
        }

        user.setSecurityDetails(new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), grantedAuthorities));
        return user;
    }
}
