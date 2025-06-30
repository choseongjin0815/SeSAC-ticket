package com.onspring.onspring_customer.controller;

import com.onspring.onspring_customer.domain.customer.dto.AdminDto;
import com.onspring.onspring_customer.domain.customer.service.AdminService;
import com.onspring.onspring_customer.security.CustomUserDetails;
import com.onspring.onspring_customer.security.SecurityUtil;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
@Log4j2
public class HomeController {

    private final AdminService adminService;

    @GetMapping("/")
    public String root() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("authentication: {}", authentication); // 객체 자체 확인
        if (authentication != null &&
                authentication.isAuthenticated() &&
                authentication.getPrincipal() instanceof CustomUserDetails) {

            return "redirect:/view/home"; // 로그인된 상태
        } else {
            return "auth/login"; // 로그인 안 된 상태
        }
    }

    @GetMapping("/view/home")
    public String home(Model model) {
        Long adminId = SecurityUtil.getCurrentUserId();

        AdminDto adminDto = adminService.findAdminById(adminId);
        log.info(adminDto.getUserName());

        model.addAttribute("userName", adminDto.getUserName());
        return "home";
    }




}
