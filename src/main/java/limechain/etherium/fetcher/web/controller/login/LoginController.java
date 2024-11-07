package limechain.etherium.fetcher.web.controller.login;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import limechain.etherium.fetcher.db.model.Account;
import limechain.etherium.fetcher.db.model.ResetPassword;
import limechain.etherium.fetcher.db.repository.ResetPasswordRepository;
import limechain.etherium.fetcher.service.SecurityService;
import limechain.etherium.fetcher.service.UserService;
import limechain.etherium.fetcher.web.controller.account.AccountsController;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private ResetPasswordRepository resetPasswordRepository;

    @GetMapping("/.well-known/pki-validation/1185372B6DB79E8F40B969D8512DA429.txt")
    public String zeroSsl() {
        return "1185372B6DB79E8F40B969D8512DA429";
    }

    static String redirectLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().contains(OtpController.AUTHORITY_OTP)) {
            return OtpController.REDIRECT_ROOT;
        }
        return "redirect:" + AccountsController.ROOT;
    }

    @GetMapping("/login")
    public String loginForm(Model model, String error, String logout) {
        if (securityService.isAuthenticated()) {
            return redirectLoggedIn();
        }
        if (error != null) {
            model.addAttribute("error", "Your username and password is invalid.");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully.");
        }
        return "login";
    }

    @GetMapping("/signup")
    public String registrationForm(Model model, @ModelAttribute("errors") Object errors) {
        if (securityService.isAuthenticated()) {
            return redirectLoggedIn();
        }
        model.addAttribute("userForm", new Account());
        if (errors instanceof List && ((List<?>) errors).size() > 0) {
            model.addAttribute("errorMessages", errors);
        }
        return "signup";
    }

    @PostMapping("/signup")
    public String registration(Model model, @ModelAttribute("userForm") Account userForm, BindingResult bindingResult, RedirectAttributes attributes) {

        if (securityService.isAuthenticated()) {
            return redirectLoggedIn();
        }

        validate(userForm, bindingResult);
        if (bindingResult.hasErrors()) {
            attributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/signup";
        }

        if (userService.findByUsername(userForm.getUsername()) != null) {
            model.addAttribute("userForm", new Account());
            return "redirect:/login";
        }
        String notEncryptedPassword = userForm.getPassword();
        userService.signup(userForm);
        securityService.autoLogin(userForm.getUsername(), notEncryptedPassword, OtpController.AUTHORITY_SET_OTP);

        model.addAttribute("userForm", new Account());
        return OtpController.REDIRECT_ROOT;

    }

    @GetMapping("/")
    public String home(Model model) {
        return "homepage/home";
    }

    @GetMapping("/reset-password")
    public String resetPassword(Model model, String error, String logout) {
        if (securityService.isAuthenticated()) {
            return redirectLoggedIn();
        }
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(Model model, HttpServletRequest request, String email, String error, String logout) {
        if (securityService.isAuthenticated()) {
            return redirectLoggedIn();
        }
        userService.resetPassword(email, request);
        return "redirect:/reset-password-sent";
    }

    @GetMapping("/reset-password-sent")
    public String resetPasswordSent(Model model, HttpServletRequest request, String email, String error, String logout) {
        return "reset-password-sent";
    }

    @GetMapping("/reset-password-set/{key}")
    public String resetPasswordSetNew(Model model, @PathVariable(name = "key") String key, String error, String logout) {
        if (securityService.isAuthenticated()) {
            return redirectLoggedIn();
        }
        ResetPassword resetPassword = resetPasswordRepository.findByResetKey(UUID.fromString(key));
        if (resetPassword != null) {
            return "set-new-password";
        } else {
            return "redirect:/user-not-found";
        }
    }

    @PostMapping("/reset-password-set/{key}")
    public String setPassword(Model model, @PathVariable(name = "key") String key, String password, String error, String logout) {
        if (securityService.isAuthenticated()) {
            return redirectLoggedIn();
        }

        ResetPassword resetPassword = resetPasswordRepository.findByResetKey(UUID.fromString(key));
        if (resetPassword != null) {
            userService.updatePassword(resetPassword, password);
        } else {
            return "redirect:/user-not-found";
        }
        return "redirect:/login";
    }

    @GetMapping("/user-not-found")
    public String userNotFound() {
        return "user-not-found";
    }

    private void validate(Account o, Errors errors) {
        Account user = (Account) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "", "Email should be filled");
        /*
         * if (user.getUsername().length() < 6 || user.getUsername().length() > 32) {
         * errors.rejectValue("username", "Size.userForm.username"); }
         */
        if (userService.findByUsername(user.getUsername()) != null) {
            errors.rejectValue("username", "", "User with this email already registered");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "", "Password should be filled");
        /*
         * if (user.getPassword().length() < 8 || user.getPassword().length() > 32) {
         * errors.rejectValue("password", "Size.userForm.password"); }
         */

        if (!user.getPassword().equals(user.getPasswordConfirm())) {
            errors.rejectValue("passwordConfirm", "", "Passwords not match");
        }
    }
}
