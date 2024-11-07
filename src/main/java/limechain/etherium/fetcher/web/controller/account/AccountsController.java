package limechain.etherium.fetcher.web.controller.account;

import java.security.Principal;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(AccountsController.ROOT)
@PreAuthorize("hasAnyAuthority('" + ProfileController.MERCHANT + "','" + ProfileController.SUBSCRIBE + "')")
public class AccountsController {

    public static final String ROOT = "/accounts";
    public static final String REDIRECT_ROOT = "redirect:" + ROOT;

    @GetMapping
    public String dashboard(Model model, Principal principal) {
        return "dashboard/dashboard";
    }

}
