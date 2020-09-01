package rvu.application.relink;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;


@Controller
public class HomeController {

    private final ShortURLRepository repo;

    HomeController(ShortURLRepository repo) {
        this.repo = repo;
    }
    
    @RequestMapping(value = "/")
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/to/{name}")
    public String redirect(@PathVariable String name) {
        ShortURL shortURL = repo.findByName(name);
        if (shortURL == null) {
            return "redirect:/";
        } else {
            return "redirect:".concat(shortURL.getDest());
        }
    }
}
