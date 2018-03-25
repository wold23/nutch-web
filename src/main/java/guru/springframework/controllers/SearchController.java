package guru.springframework.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SearchController {
    @RequestMapping("/search")
    public String search(String wd){
        System.out.println(wd);
        return "index";
    }
}
