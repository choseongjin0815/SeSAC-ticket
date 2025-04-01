package com.onspring.onspring_customer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("userName", "홍길동");
        return "home";
    }

    @GetMapping("/members/add")
    public String addMember(Model model) {

        return "members/add";
    }

    @GetMapping("/members/manage")
    public String memberManagePage(Model model) {
        model.addAttribute("activeMenu", "member-manage");
        return "members/manage";
    }

    @GetMapping("/members/activated")
    public String activatedMember(Model model) {
        model.addAttribute("activeMenu", "member-activated");
        return "members/activated";
    }

    @GetMapping("/manager/changepassword")
    public String changePassword(Model model) {
        return "manager/changepassword";
    }



}
