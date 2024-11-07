package limechain.etherium.fetcher.web.controller.login;

import java.security.Principal;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import limechain.etherium.fetcher.db.model.Account;
import limechain.etherium.fetcher.db.model.Otp;
import limechain.etherium.fetcher.db.model.Role;
import limechain.etherium.fetcher.service.EmailService;
import limechain.etherium.fetcher.service.SecurityService;
import limechain.etherium.fetcher.service.UserService;
import limechain.etherium.fetcher.web.controller.account.AccountsController;

@Controller
@RequestMapping(OtpController.ROOT)
@PreAuthorize(OtpController.HAS_AUTHORITY_OTP)
public class OtpController {
    static final SimpleGrantedAuthority AUTHORITY_SUBSCRIBE = new SimpleGrantedAuthority(Role.SUBSCRIBE.name());

    protected static final String ROOT = "/otp";
    static final String HAS_AUTHORITY_OTP = "hasAuthority('OTP')";;
    public static final String REDIRECT_ROOT = "redirect:" + ROOT;

    public static final SimpleGrantedAuthority AUTHORITY_OTP = new SimpleGrantedAuthority(Role.OTP.name());

    static final Set<GrantedAuthority> AUTHORITY_SET_OTP = Stream.of(AUTHORITY_OTP).collect(Collectors.toSet());

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SecurityService securityService;

    @GetMapping
    public String confirmEmailForm(Model model) {
        model.addAttribute("userForm", new Account());
        return "otp";
    }

    @PostMapping
    public String confirmEmail(@RequestParam("otp") String otp, Principal principal) {
        if (!userService.confirmOtp(principal.getName(), otp)) {
            return REDIRECT_ROOT;
        }
        securityService.replaceAuthorities(AUTHORITY_SUBSCRIBE);
        return AccountsController.REDIRECT_ROOT;
    }

    @PostMapping("/resend-otp")
    public String resendOtp(Principal principal) {
        Account user = (Account) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        Otp otp = userService.generateAndSetOtp(user);
        emailService.sendOtpToEmail(user, otp);
        return REDIRECT_ROOT;
    }
}
