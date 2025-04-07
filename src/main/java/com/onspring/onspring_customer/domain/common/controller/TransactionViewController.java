package com.onspring.onspring_customer.domain.common.controller;

import com.onspring.onspring_customer.domain.common.dto.TransactionArchiveDto;
import com.onspring.onspring_customer.domain.common.dto.TransactionDto;
import com.onspring.onspring_customer.domain.common.service.TransactionArchiveService;
import com.onspring.onspring_customer.domain.common.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/view/transactions")
public class TransactionViewController {
    private final TransactionService transactionService;
    private final TransactionArchiveService transactionArchiveService;

    @Autowired
    public TransactionViewController(TransactionService transactionService,
                                     TransactionArchiveService transactionArchiveService) {
        this.transactionService = transactionService;
        this.transactionArchiveService = transactionArchiveService;
    }

    @GetMapping("/close")
    public String getNotClosedTransaction(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                          @RequestParam(value = "size", defaultValue = "10") Integer size,
                                          Model model) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<TransactionDto> transactionDtoPage = transactionService.findAllAcceptedAndNotClosedTransaction(pageable);

        model.addAttribute("transactions", transactionDtoPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", transactionDtoPage.getTotalPages());

        return "transactions/close";
    }

    @GetMapping("/list")
    public String getClosedTransaction(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                       @RequestParam(value = "size", defaultValue = "10") Integer size, Model model) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<TransactionArchiveDto> transactionArchiveDtoPage =
                transactionArchiveService.findAllTransactionArchive(pageable);

        model.addAttribute("transactionArchives", transactionArchiveDtoPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", transactionArchiveDtoPage.getTotalPages());

        return "transactions/list";
    }
}
