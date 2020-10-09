package rvu.application.relink;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.data.rest.core.event.AfterCreateEvent;
import org.springframework.data.rest.core.event.BeforeCreateEvent;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController implements ApplicationEventPublisherAware {

    @Autowired
    private ShortURLRepository shortURLRepo;

    @Autowired
    private RelinkUserRepository userRepo;

    private ApplicationEventPublisher publisher;

    @GetMapping(value = "/")
    public String home(Model model) {
        model.addAttribute("shortURLFormData", new ShortURLFormData());
        return "home";
    }

    @PostMapping(value = "/")
    public String createShortURL(@ModelAttribute ShortURLFormData shortURLFormData, Model model) {
        try {

            ShortURL shortURL = shortURLFormData.toShortURL();

            if (!shortURL.validateDestination()) {
                model.addAttribute("invalidURL", true);
                return "home";
            }

            publisher.publishEvent(new BeforeCreateEvent(shortURL));
            shortURLRepo.save(shortURL);
            publisher.publishEvent(new AfterCreateEvent(shortURL));
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

        if (userRepo.findByNameLocal(newUser.getName()) == null) {
            newUser.setRoles(new String[] { "ROLE_USER" });
            userRepo.save(newUser);
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

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }
}
