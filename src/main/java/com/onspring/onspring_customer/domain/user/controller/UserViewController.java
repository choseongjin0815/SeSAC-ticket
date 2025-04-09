package com.onspring.onspring_customer.domain.user.controller;

import com.onspring.onspring_customer.domain.customer.dto.PartyDto;
import com.onspring.onspring_customer.domain.customer.service.PartyService;
import com.onspring.onspring_customer.domain.user.dto.EndUserDto;
import com.onspring.onspring_customer.domain.user.service.EndUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/view/users")
public class UserViewController {
    private final EndUserService endUserService;
    private final PartyService partyService;
    private final PasswordEncoder passwordEncoder;


    @GetMapping("/add")
    String showSaveUser(Model model) {
        List<PartyDto> partyList = partyService.findAllParty();

        model.addAttribute("parties", partyList);

        return "users/add";
    }

    @PostMapping("/add")
    String saveUser(@RequestParam(value = "name") String name, @RequestParam(value = "phone") String phone,
                    @RequestParam(value = "password") String password, @RequestParam(value = "partyId") Long partyId) {
        EndUserDto endUserDto = new EndUserDto();
        endUserDto.setName(name);
        endUserDto.setPhone(phone);
        endUserDto.setPartyIds(Collections.singletonList(partyId));
        endUserDto.setPassword(passwordEncoder.encode(password));
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
