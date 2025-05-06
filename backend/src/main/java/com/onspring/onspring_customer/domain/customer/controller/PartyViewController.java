package com.onspring.onspring_customer.domain.customer.controller;

import com.onspring.onspring_customer.domain.customer.dto.PartyDto;
import com.onspring.onspring_customer.domain.customer.dto.PartyEndUserRelationDto;
import com.onspring.onspring_customer.domain.customer.service.AdminService;
import com.onspring.onspring_customer.domain.customer.service.PartyService;
import com.onspring.onspring_customer.domain.user.dto.EndUserDto;
import com.onspring.onspring_customer.domain.user.service.EndUserService;
import com.onspring.onspring_customer.domain.user.service.PointService;
import com.onspring.onspring_customer.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/view/parties")
public class PartyViewController {
    private final PartyService partyService;
    private final AdminService adminService;
    private final EndUserService endUserService;
    private final PointService pointService;

    private static Long getAdminId() {
        return SecurityUtil.getCurrentUserId();
    }

    @GetMapping("/add")
    public String showSaveParty() {
        return "parties/add";
    }

    @PostMapping("/add")
    public String saveParty(@RequestParam(value = "name") String name,
                            @RequestParam(value = "period") LocalDateTime period,
                            @RequestParam(value = "amount") BigDecimal amount, @RequestParam(value =
                    "allowedTimeStart") LocalTime allowedTimeStart,
                            @RequestParam(value = "allowedTimeEnd") LocalTime allowedTimeEnd, @RequestParam(value =
                    "validThru") Long validThru,
                            @RequestParam(value = "sunday", defaultValue = "false") Boolean sunday,
                            @RequestParam(value = "monday", defaultValue = "false") Boolean monday,
                            @RequestParam(value = "tuesday", defaultValue = "false") Boolean tuesday,
                            @RequestParam(value = "wednesday", defaultValue = "false") Boolean wednesday,
                            @RequestParam(value = "thursday", defaultValue = "false") Boolean thursday,
                            @RequestParam(value = "friday", defaultValue = "false") Boolean friday,
                            @RequestParam(value = "saturday", defaultValue = "false") Boolean saturday,
                            @RequestParam(value = "maximumAmount") BigDecimal maximumAmount, @RequestParam(value =
                    "maximumTransaction") Long maximumTransaction) {
        Long customerId = adminService.findAdminById(getAdminId()).getCustomerId();
        PartyDto partyDto = new PartyDto(null, customerId, name, period, amount, allowedTimeStart, allowedTimeEnd,
                validThru, sunday, monday, tuesday, wednesday, thursday, friday, saturday, maximumAmount,
                maximumTransaction, true, null);
        partyService.saveParty(partyDto);

        return "redirect:list";
    }

    @GetMapping("/list")
    public String getParties(@RequestParam(value = "searchType", required = false) String searchType,
                             @RequestParam(value = "keyword", required = false) String keyword, @RequestParam(value =
                    "page", defaultValue = "1") Integer page,
                             @RequestParam(value = "size", defaultValue = "10") Integer size, Model model) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<PartyDto> partyDtoPage;
        if (searchType != null) {
            partyDtoPage = switch (searchType) {
                case "name" ->
                        partyService.findAllPartyByQuery(getAdminId(), keyword, null, null, false, false, false,
                                false, false, false, false, null, null, pageable);
                default -> throw new IllegalStateException("Unexpected value: " + searchType);
            };
        } else {
            partyDtoPage = partyService.findAllPartyByQuery(getAdminId(), null, null, null, false, false, false,
                    false, false, false, false, null, null, pageable);
        }

        model.addAttribute("parties", partyDtoPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", partyDtoPage.getTotalPages());

        return "parties/list";
    }

    @GetMapping("/activate")
    public String getDeactivatedParties(@RequestParam(value = "searchType", required = false) String searchType,
                                        @RequestParam(value = "keyword", required = false) String keyword, @RequestParam(value =
                    "page", defaultValue = "1") Integer page,
                                        @RequestParam(value = "size", defaultValue = "10") Integer size, Model model) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<PartyDto> partyDtoPage;
        if (searchType != null) {
            partyDtoPage = switch (searchType) {
                case "name" ->
                        partyService.findAllDeActivatePartyByQuery(getAdminId(), keyword, null, null, false, false, false,
                                false, false, false, false, null, null, pageable);
                default -> throw new IllegalStateException("Unexpected value: " + searchType);
            };
        } else {
            partyDtoPage = partyService.findAllDeActivatePartyByQuery(getAdminId(), null, null, null, false, false, false,
                    false, false, false, false, null, null, pageable);
        }

        model.addAttribute("parties", partyDtoPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", partyDtoPage.getTotalPages());

        return "parties/activate";

    }


    @RequestMapping(value = "/update", method = {RequestMethod.PUT, RequestMethod.POST})
    public String updateParty(@RequestParam(value = "id") Long id,
                              @RequestParam(value = "customerId") Long customerId,
                              @RequestParam(value = "name") String name,
                              @RequestParam(value = "period") String periodStr,
                              @RequestParam(value = "amount") BigDecimal amount, @RequestParam(value =
                    "allowedTimeStart") LocalTime allowedTimeStart,
                              @RequestParam(value = "allowedTimeEnd") LocalTime allowedTimeEnd, @RequestParam(value =
                    "validThru") Long validThru,
                              @RequestParam(value = "sunday", defaultValue = "false") Boolean sunday,
                              @RequestParam(value = "monday", defaultValue = "false") Boolean monday,
                              @RequestParam(value = "tuesday", defaultValue = "false") Boolean tuesday,
                              @RequestParam(value = "wednesday", defaultValue = "false") Boolean wednesday,
                              @RequestParam(value = "thursday", defaultValue = "false") Boolean thursday,
                              @RequestParam(value = "friday", defaultValue = "false") Boolean friday,
                              @RequestParam(value = "saturday", defaultValue = "false") Boolean saturday,
                              @RequestParam(value = "maximumAmount") BigDecimal maximumAmount, @RequestParam(value =
                    "maximumTransaction") Long maximumTransaction) {

        // 기존 Party 객체를 가져와서 업데이트
        PartyDto existingParty = partyService.findPartyById(id);

        // 문자열을 LocalDateTime으로 변환
        LocalDateTime period = LocalDate.parse(periodStr)
                .atStartOfDay();

        PartyDto partyDto = PartyDto.builder()
                .id(id)
                .customerId(customerId) // customerId 유지
                .name(name)
                .period(period)
                .amount(amount)
                .allowedTimeStart(allowedTimeStart)
                .allowedTimeEnd(allowedTimeEnd)
                .validThru(validThru)
                .sunday(sunday)
                .monday(monday)
                .tuesday(tuesday)
                .wednesday(wednesday)
                .thursday(thursday)
                .friday(friday)
                .saturday(saturday)
                .maximumAmount(maximumAmount)
                .maximumTransaction(maximumTransaction)
                .isActivated(existingParty.isActivated()) // 활성화 상태 유지
                .build();
        partyService.updateParty(partyDto);

        return "redirect:list";
    }

    @PatchMapping("/activate")
    @ResponseBody
    public ResponseEntity<Void> activateParty(@RequestParam(value = "ids") List<Long> ids) {
        partyService.activatePartyById(ids);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/edit/{id}")
    public String showEditParty(@PathVariable("id") Long id, Model model) {
        PartyDto partyDto = partyService.findPartyById(id);
        model.addAttribute("party", partyDto);

        return "parties/edit";
    }

    @PatchMapping("/deactivate")
    @ResponseBody
    public ResponseEntity<Void> deactivateParty(@RequestParam(value = "ids") List<Long> ids) {
        System.out.println("ids: " + ids);
        partyService.deactivatePartyById(ids);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/users")
    public String getPartyEndUserRelation(@RequestParam(value = "searchType", required = false) String searchType,
                            @RequestParam(value = "keyword", required = false) String keyword, @RequestParam(value =
                    "page", defaultValue = "1") Integer page,
                            @RequestParam(value = "size", defaultValue = "10") Integer size, Model model) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<PartyEndUserRelationDto> partyEndUserRelationDtoPage;

        if (searchType != null) {
            partyEndUserRelationDtoPage = switch (searchType) {
                case "name" -> partyService.findAllPartyEndUserRelationByQuery(getAdminId(), keyword, pageable);
                default -> throw new IllegalStateException("Unexpected value: " + searchType);
            };
        } else {
            partyEndUserRelationDtoPage = partyService.findAllPartyEndUserRelationByQuery(getAdminId(), null, pageable);
        }

        model.addAttribute("models", partyEndUserRelationDtoPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", partyEndUserRelationDtoPage.getTotalPages());

        return "parties/users";
    }

    @GetMapping("users/edit/{partyId}")
    public String getAddOrRemovePartyEndUserRelation(@PathVariable Long partyId,
                                                  @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                  @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                  Model model) {
        Pageable pageable = PageRequest.of(page - 1, size);
        List<EndUserDto> associatedEndUserDto = endUserService.findEndUserByPartyId(partyId);
        Page<EndUserDto> unassociatedEndUserDto = endUserService.findEndUserByPartyId_Not(partyId, pageable);

        model.addAttribute("associatedUsers", associatedEndUserDto);
        model.addAttribute("unassociatedUsers", unassociatedEndUserDto);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", unassociatedEndUserDto.getTotalPages());

        return "parties/users_edit";
    }

    @PostMapping("users/edit/{partyId}")
    public String addPartyEndUserRelation(@PathVariable Long partyId,
                                          @RequestParam(value = "userIds") List<Long> userIds) {
        partyService.addEndUserToParty(partyId, userIds);

        return "redirect:users/edit/{partyId}";
    }

    @DeleteMapping("users/edit/{partyId}")
    public String removePartyEncUserRelation(@PathVariable Long partyId,
                                             @RequestParam(value = "userIds") List<Long> userIds) {
        pointService.deletePointByPartyIdAndEndUserId(partyId, userIds);

        return "redirect:users/edit/{partyId}";
    }
}
