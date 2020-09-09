package rvu.application.relink;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
            ShortURL shortURL = shortURLFormData.toShortURL();
            shortURLRepo.save(shortURL);
            model.addAttribute("shortURLData", shortURL);
        } catch (Exception e) {
            model.addAttribute("exceptionInfo", e.getMessage());
        }
        return "success";
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
            return "redirect:".concat(shortURL.getDest());
        }
    }
}
