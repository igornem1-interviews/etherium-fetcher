package limechain.etherium.fetcher.config.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Deprecated TODO: Consider to use:
 * https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter
 * 
 * @return
 * @throws Exception
 */
@Deprecated
@Configuration
//@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests().antMatchers("/*", "/tip/**", "/reset-password-set/*", "/.well-known/**").permitAll().anyRequest().authenticated().and()
                .formLogin().defaultSuccessUrl("/accounts", true).loginPage("/login").permitAll().loginProcessingUrl("/login").permitAll().usernameParameter("user_name")
                .passwordParameter("password").and().logout().logoutRequestMatcher(new AntPathRequestMatcher("/accounts/logout")).logoutSuccessUrl("/").permitAll().and()
                .exceptionHandling().accessDeniedPage("/login").and().addFilterBefore(new SessionFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/menu/**", "/logo/**", "/tip/**", "/static/**", "/css/**", "/js/**", "/images/**", "/_files/**");
    }

    /**
     * TODO: Consider to use:
     * https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter
     * 
     * @return
     * @throws Exception
     */
    @Bean
    public AuthenticationManager customAuthenticationManager() throws Exception {
        return authenticationManager();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }

}