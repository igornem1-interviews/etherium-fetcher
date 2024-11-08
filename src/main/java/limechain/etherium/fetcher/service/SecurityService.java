package limechain.etherium.fetcher.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import limechain.etherium.fetcher.model.User;

@Service
public class SecurityService {

    @Autowired
    private UserDetailsService userDetailsService;
    /*
     * @Autowired
     * 
     * @Qualifier("sessionRegistry") private SessionRegistry sessionRegistry;
     */

    private static final Logger logger = LoggerFactory.getLogger(SecurityService.class);

    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || AnonymousAuthenticationToken.class.isAssignableFrom(authentication.getClass())) {
            return false;
        }
        return authentication.isAuthenticated();
    }



    public void logout(HttpServletRequest request) {
        new SecurityContextLogoutHandler().logout(request, null, null);
    }

    public void replaceAuthorities(SimpleGrantedAuthority authority) {
        List<SimpleGrantedAuthority> updatedAuthorities = new ArrayList<SimpleGrantedAuthority>();
        updatedAuthorities.add(authority);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(SecurityContextHolder.getContext().getAuthentication().getPrincipal(),
                SecurityContextHolder.getContext().getAuthentication().getCredentials(), updatedAuthorities));
    }

    public void replaceUserDetails(User user) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, SecurityContextHolder.getContext().getAuthentication().getCredentials(),
                SecurityContextHolder.getContext().getAuthentication().getAuthorities()));
    }
    /*
     * public Map<String, User> getLoggedInPrincipals() { List<?> all =
     * sessionRegistry.getAllPrincipals(); if (all.size() > 0) { Map<String, User>
     * principals = new HashMap<>(); for (Object principal : all) { User user =
     * (User) principal; principals.put(user.getUsername(), user); } return
     * principals; } return null; }
     */
}
