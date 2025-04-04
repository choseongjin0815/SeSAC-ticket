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

import java.util.Collections;

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
        endUserDto.setPartyIds(Collections.singletonList(partyId));
        endUserDto.setPassword("*");
        endUserDto.setActivated(true);

        endUserService.saveEndUser(endUserDto);

        return "redirect:list";
    }

    @GetMapping("/list")
    String getUsers(@RequestParam(value = "searchType", required = false) String searchType, @RequestParam(value =
            "keyword", required = false) String keyword,
                    @RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "size",
                    defaultValue = "10") Integer size, Model model) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<EndUserDto> endUserDtoPage;
        if (searchType == null) {
            endUserDtoPage = endUserService.findAllEndUserByQuery(null, null, null, true, pageable);
        } else {
            endUserDtoPage = switch (searchType) {
                case "name" -> endUserService.findAllEndUserByQuery(keyword, null, null, true, pageable);
                case "partyName" -> endUserService.findAllEndUserByQuery(null, keyword, null, true, pageable);
                case "phone" -> endUserService.findAllEndUserByQuery(null, null, keyword, true, pageable);
                default -> throw new IllegalStateException("Unexpected value: " + searchType);
            };
        }

        model.addAttribute("users", endUserDtoPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", endUserDtoPage.getTotalPages());

        return "users/list";
    }

    @GetMapping("/activate")
    String getDeactivatedUsers(@RequestParam(value = "searchType", required = false) String searchType,
                               @RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value =
                    "size", defaultValue = "10") Integer size, Model model) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<EndUserDto> endUserDtoPage;
        if (searchType == null) {
            endUserDtoPage = endUserService.findAllEndUserByQuery(null, null, null, false, pageable);
        } else {
            endUserDtoPage = switch (searchType) {
                case "name" -> endUserService.findAllEndUserByQuery(keyword, null, null, false, pageable);
                case "partyName" -> endUserService.findAllEndUserByQuery(null, keyword, null, false, pageable);
                case "phone" -> endUserService.findAllEndUserByQuery(null, null, keyword, false, pageable);
                default -> throw new IllegalStateException("Unexpected value: " + searchType);
            };
        }

        model.addAttribute("users", endUserDtoPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", endUserDtoPage.getTotalPages());

        return "users/activate";
    }
}
