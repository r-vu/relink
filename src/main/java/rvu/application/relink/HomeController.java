package rvu.application.relink;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

    @Autowired
    private ShortURLRepository shortURLRepo;

    @Autowired
    private RelinkUserRepository relinkUserRepo;

    @GetMapping(value = "/")
    public String home(Model model) {
        model.addAttribute("shortURLFormData", new ShortURLFormData());
        return "home";
    }

    @PostMapping(value = "/")
    public String createShortURL(@ModelAttribute ShortURLFormData shortURLFormData, Model model) {
        try {

            String dest = shortURLFormData.getDest();
            if (!dest.toLowerCase().startsWith("http")) {
                dest = "https://".concat(dest);
            }

            String[] schemes = {"http", "https"};
            UrlValidator validator = new UrlValidator(schemes);
            if (!validator.isValid(dest)) {
                model.addAttribute("invalidURL", true);
                // System.out.printf("An invalid URL was entered: %s \n", dest);
                return "home";
            }

            ShortURL shortURL = shortURLFormData.toShortURL();
            shortURL.setDest(dest);
            shortURLRepo.save(shortURL);
            model.addAttribute("shortURLData", shortURL);

        } catch (Exception e) {
            model.addAttribute("exceptionInfo", e.getMessage());
        }
        return "success";
    }

    @GetMapping(value = "/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping(value = "/signup")
    public String signupPage(Model model) {
        model.addAttribute("newUser", new RelinkUser());
        return "signup";
    }

    @PostMapping(value = "/signup")
    public String signup(@ModelAttribute(name = "newUser") RelinkUser newUser, Model model,
        RedirectAttributes rAttributes) {

        if (relinkUserRepo.findByName(newUser.getName()) == null) {
            newUser.setRoles(new String[] {"ROLE_USER"});
            relinkUserRepo.save(newUser);
            rAttributes.addFlashAttribute("createSuccess", true);
            return "redirect:/login";
        } else {
            return "error";
        }
    }


    @GetMapping(value = "/table")
    public String table() {
        return "table";
    }

    @RequestMapping(value = "/to/{name}")
    public String redirect(@PathVariable String name) {
        ShortURL shortURL = shortURLRepo.findByName(name);
        if (shortURL == null) {
            return "redirect:/table";
        } else {
            shortURL.incrementUseCount();
            shortURLRepo.save(shortURL);
            return "redirect:".concat(shortURL.getDest());
        }
    }
}
