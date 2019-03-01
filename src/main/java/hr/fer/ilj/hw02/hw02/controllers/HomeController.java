package hr.fer.ilj.hw02.hw02.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping(value = "")
    public String home(){
        return "courses.html";
    }
}
