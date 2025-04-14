package com.onspring.onspring_customer.domain.common.controller;

import com.onspring.onspring_customer.domain.common.service.TransactionArchiveService;
import com.onspring.onspring_customer.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/view/transactions")
public class TransactionRestController {
    private final TransactionArchiveService transactionArchiveService;

    private static Long getAdminId() {
        return SecurityUtil.getCurrentUserId();
    }

    @Autowired
    public TransactionRestController(TransactionArchiveService transactionArchiveService) {
        this.transactionArchiveService = transactionArchiveService;
    }

    @PatchMapping("/close")
    public void closeTransaction(@RequestParam(value = "ids") List<Long> ids) {
        transactionArchiveService.closeTransactionById(getAdminId(), ids);
    }
}
