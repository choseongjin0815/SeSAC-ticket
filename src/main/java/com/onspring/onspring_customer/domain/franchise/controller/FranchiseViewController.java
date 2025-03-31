package com.onspring.onspring_customer.domain.franchise.controller;

import com.onspring.onspring_customer.domain.franchise.dto.FranchiseDto;
import com.onspring.onspring_customer.domain.franchise.service.FranchiseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/view/franchises")
public class FranchiseViewController {
    private final FranchiseService franchiseService;

    @Autowired
    public FranchiseViewController(FranchiseService franchiseService) {
        this.franchiseService = franchiseService;
    }

    @GetMapping("/add")
    String showSaveFranchise() {
        return "franchises/add";
    }

    @PostMapping("/add")
    String saveFranchise(@RequestParam(value = "userName", required = false) String userName, @RequestParam(value =
            "name", required = false) String name,
                         @RequestParam(value = "ownerName", required = false) String ownerName, @RequestParam(value =
                    "businessNumber", required = false) String businessNumber, @RequestParam(value = "address",
                    required = false) String address, @RequestParam(value = "phone", required = false) String phone) {
        FranchiseDto franchiseDto = FranchiseDto.builder()
                .userName(userName)
                .name(name)
                .ownerName(ownerName)
                .businessNumber(businessNumber)
                .address(address)
                .phone(phone)
                .build();
        franchiseService.saveFranchise(franchiseDto);

        return "redirect:list";
    }

    @GetMapping("/list")
    String getFranchises(@RequestParam(value = "userName", required = false) String userName, @RequestParam(value =
            "name", required = false) String name,
                         @RequestParam(value = "ownerName", required = false) String ownerName, @RequestParam(value =
                    "businessNumber", required = false) String businessNumber, @RequestParam(value = "address",
                    required = false) String address, @RequestParam(value = "phone", required = false) String phone,
                         @RequestParam(value = "customerName", required = false) String customerName,
                         @RequestParam(value = "showDeactivated", defaultValue = "false") boolean showDeactivated,
                         @RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "size"
                    , defaultValue = "10") Integer size, Model model) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<FranchiseDto> franchiseDtoPage = franchiseService.findAllFranchiseByQuery(userName, name, ownerName,
                businessNumber, address, phone, customerName, showDeactivated, pageable);

        model.addAttribute("franchises", franchiseDtoPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", franchiseDtoPage.getTotalPages());

        return "franchises/list";
    }

    @GetMapping("/update/{id}")
    public String showUpdateFranchise(@PathVariable Long id, Model model) {
        FranchiseDto franchise = franchiseService.findFranchiseById(id);

        model.addAttribute("franchise", franchise);

        return "franchises/update";
    }

    @PutMapping("/update/{id}")
    String updateFranchise(@PathVariable Long id, @RequestParam(value = "name") String name,
                           @RequestParam(value = "ownerName") String ownerName, @RequestParam(value = "address",
                    required = false) String address, @RequestParam(value = "phone") String phone) {
        FranchiseDto franchiseDto = FranchiseDto.builder()
                .id(id)
                .name(name)
                .ownerName(ownerName)
                .address(address)
                .phone(phone)
                .build();
        franchiseService.updateFranchise(id, franchiseDto);

        return "redirect:list";
    }

    @PatchMapping("/activate")
    String activateFranchise(@RequestParam(value = "id") Long id) {
        franchiseService.activateFranchiseById(id);

        return "redirect:list";
    }

    @PatchMapping("/deactivate")
    String deactivateFranchise(@RequestParam(value = "id") Long id) {
        franchiseService.deactivateFranchiseById(id);

        return "redirect:list";
    }
}
