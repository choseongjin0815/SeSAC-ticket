package com.onspring.onspring_customer.domain.user.controller;

import com.onspring.onspring_customer.domain.user.dto.EndUserDto;
import com.onspring.onspring_customer.domain.user.service.EndUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/view/users")
public class UserViewController {
    private final EndUserService endUserService;

    @Autowired
    public UserViewController(EndUserService endUserService) {
        this.endUserService = endUserService;
    }

    @GetMapping("/add")
    String showSaveUser() {
        return "users/add";
    }

    @PostMapping("/add")
    String saveUser(@RequestParam(value = "name") String name, @RequestParam(value = "phone") String phone,
                    @RequestParam(value = "partyId") Long partyId) {
        EndUserDto endUserDto = new EndUserDto();
        endUserDto.setName(name);
        endUserDto.setPhone(phone);
        endUserDto.setPartyId(partyId);
        endUserDto.setPassword("*");
        endUserDto.setActivated(true);
        endUserService.saveEndUser(endUserDto);

        return "redirect:list";
    }

    @GetMapping("/list")
    String getUsers(@RequestParam(value = "name", required = false) String name, @RequestParam(value = "partyName",
                            required = false) String partyName,
                    @RequestParam(value = "phone", required = false) String phone,
                    @RequestParam(value = "showDeactivated", defaultValue = "true", required = false) Boolean showDeactivated, @RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "size", defaultValue = "10") Integer size, Model model) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<EndUserDto> endUserDtoPage = endUserService.findAllEndUserByQuery(name, partyName, phone,
                showDeactivated, pageable);

        model.addAttribute("users", endUserDtoPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", endUserDtoPage.getTotalPages());

        return "users/list";
    }

    @PatchMapping("/activate")
    String activateUser(@RequestParam(value = "id") Long id) {
        endUserService.activateEndUserById(id);

        return "redirect:list";
    }

    @PatchMapping("/deactivate")
    String deactivateUser(@RequestParam(value = "id") Long id) {
        endUserService.deactivateEndUserById(id);

        return "redirect:list";
    }
}
