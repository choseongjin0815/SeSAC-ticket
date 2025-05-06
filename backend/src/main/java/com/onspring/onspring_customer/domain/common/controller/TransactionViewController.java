package com.onspring.onspring_customer.domain.common.controller;

import com.onspring.onspring_customer.domain.common.dto.TransactionArchiveDto;
import com.onspring.onspring_customer.domain.common.dto.TransactionDto;
import com.onspring.onspring_customer.domain.common.service.TransactionArchiveService;
import com.onspring.onspring_customer.domain.common.service.TransactionService;
import com.onspring.onspring_customer.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/view/transactions")
public class TransactionViewController {
    private final TransactionService transactionService;
    private final TransactionArchiveService transactionArchiveService;

    private static Long getAdminId() {
        return SecurityUtil.getCurrentUserId();
    }

    @GetMapping("/by-franchise")
    public String getTransactionByFranchise(@RequestParam(value = "keyword", required = false) String keyword,
                                            @RequestParam(value = "after", required = false) LocalDate after,
                                            @RequestParam(value = "before", required = false) LocalDate before,
                                            @RequestParam(value = "page", defaultValue = "1") Integer page,
                                            @RequestParam(value = "size", defaultValue = "10") Integer size,
                                            Model model) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<TransactionDto> transactionDtoPage = transactionService.findAllTransactionByQuery(getAdminId(),
                "franchise", keyword, after, before, pageable);

        model.addAttribute("transactions", transactionDtoPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", transactionDtoPage.getTotalPages());

        return "transactions/by-franchise";
    }

    @GetMapping("/by-group")
    public String getTransactionByGroup(@RequestParam(value = "keyword", required = false) String keyword,
                                        @RequestParam(value = "after", required = false) LocalDate after,
                                        @RequestParam(value = "before", required = false) LocalDate before,
                                        @RequestParam(value = "page", defaultValue = "1") Integer page,
                                        @RequestParam(value = "size", defaultValue = "10") Integer size, Model model) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<TransactionDto> transactionDtoPage = transactionService.findAllTransactionByQuery(getAdminId(), "party",
                keyword, after, before, pageable);

        model.addAttribute("transactions", transactionDtoPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", transactionDtoPage.getTotalPages());

        return "transactions/by-group";
    }

    @GetMapping("/by-user")
    public String getTransactionByUser(@RequestParam(value = "keyword", required = false) String keyword,
                                       @RequestParam(value = "after", required = false) LocalDate after,
                                       @RequestParam(value = "before", required = false) LocalDate before,
                                       @RequestParam(value = "page", defaultValue = "1") Integer page,
                                       @RequestParam(value = "size", defaultValue = "10") Integer size, Model model) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<TransactionDto> transactionDtoPage = transactionService.findAllTransactionByQuery(getAdminId(), "user",
                keyword, after, before, pageable);

        model.addAttribute("transactions", transactionDtoPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", transactionDtoPage.getTotalPages());

        return "transactions/by-user";
    }

    @GetMapping("/close")
    public String getNotClosedTransaction(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                          @RequestParam(value = "size", defaultValue = "10") Integer size,
                                          Model model) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<TransactionDto> transactionDtoPage =
                transactionService.findAllAcceptedAndNotClosedTransaction(getAdminId(), pageable);

        model.addAttribute("transactions", transactionDtoPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", transactionDtoPage.getTotalPages());

        return "transactions/close";
    }

    @PatchMapping("/close")
    @ResponseBody
    public ResponseEntity<Void> settlement(@RequestParam List<Long> ids) {
        transactionService.saveFalseTransactions(ids);
        transactionArchiveService.closeTransactionById(getAdminId(), ids);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/list")
    public String getClosedTransaction(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                       @RequestParam(value = "size", defaultValue = "10") Integer size, Model model) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<TransactionArchiveDto> transactionArchiveDtoPage =
                transactionArchiveService.findAllTransactionArchive(getAdminId(), pageable);

        model.addAttribute("transactionArchives", transactionArchiveDtoPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", transactionArchiveDtoPage.getTotalPages());

        return "transactions/list";
    }
}
