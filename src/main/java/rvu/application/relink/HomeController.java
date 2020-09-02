package rvu.application.relink;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @Autowired
    private ShortURLRepository repo;
    
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
