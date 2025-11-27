package com.epam.travel_agency.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HomeController {

    @GetMapping("/")
    public String index(Model model) {
        //model.addAttribute("tours", tourService.getAllTours());
        return "index";
    }

    @GetMapping("/dashboard")
    public String getDashboard() {
        return "dashboard";
    }
}
