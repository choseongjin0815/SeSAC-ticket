package com.onspring.onspring_customer.domain.common;


import com.onspring.onspring_customer.domain.customer.dto.AdminDto;
import com.onspring.onspring_customer.domain.customer.service.AdminService;
import com.onspring.onspring_customer.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@RequiredArgsConstructor
@ControllerAdvice
public class GlobalControllerAdvice {

    private final AdminService adminService;

    @ModelAttribute
    public void addUserNameToModel(Model model) {
        Long adiminId = SecurityUtil.getCurrentUserId();

        AdminDto adminDto = adminService.findAdminById(adiminId);

        model.addAttribute("userName", adminDto.getUserName());
    }

}