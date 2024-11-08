package limechain.etherium.fetcher.config.web;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.filter.GenericFilterBean;

import limechain.etherium.fetcher.db.model.User;

public class SessionFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            if (authentication.isAuthenticated()) {
                if (authentication.getPrincipal() instanceof User) {
                    User user = (User) ((UsernamePasswordAuthenticationToken) authentication).getPrincipal();
                    if (user.getId() == null) {
                        new SecurityContextLogoutHandler().logout((HttpServletRequest) request, (HttpServletResponse) response, authentication);
                    }
                }
            }
        }
        chain.doFilter(request, response);
    }
}