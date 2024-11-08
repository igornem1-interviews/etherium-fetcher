package limechain.etherium.fetcher.web.controller.account;

import java.security.Principal;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import limechain.etherium.fetcher.db.model.User;

@Controller
@RequestMapping(ProfileController.ROOT_URL)
@PreAuthorize("hasAnyAuthority('" + ProfileController.MERCHANT + "','" + ProfileController.SUBSCRIBE + "')")
public class ProfileController {

    static final String ROOT_URL = "/accounts/profile";
    private static final String ROOT_REDIRECT = "redirect:" + ROOT_URL;
    static final String MERCHANT = "MERCHANT";
    static final String SUBSCRIBE = "SUBSCRIBE";

    @GetMapping
    public String profile(Model model, Principal principal) {
        User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        model.addAttribute("user", user);
        return "profile/profile";
    }

}
