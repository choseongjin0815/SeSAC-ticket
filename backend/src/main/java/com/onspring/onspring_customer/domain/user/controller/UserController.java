package com.onspring.onspring_customer.domain.user.controller;

import com.onspring.onspring_customer.domain.auth.dto.PasswordUpdateRequest;
import com.onspring.onspring_customer.domain.customer.dto.PartyDto;
import com.onspring.onspring_customer.domain.customer.service.PartyService;
import com.onspring.onspring_customer.domain.user.dto.EndUserDto;
import com.onspring.onspring_customer.domain.user.service.EndUserService;
import com.onspring.onspring_customer.global.util.file.CustomFileUtil;
import com.onspring.onspring_customer.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Tag(name = "사용자 API", description = "사용자 정보 관련 API")
public class UserController {
    private final EndUserService endUserService;
    private final PartyService partyService;
    private final CustomFileUtil customFileUtil;

    @Operation(summary = "사용자 정보 조회", description = "사용자 상세 정보 조회")
    @GetMapping("/info")
    public ResponseEntity<EndUserDto> getUserInfo() {
        Long userId = SecurityUtil.getCurrentUserId();

        log.info("get user info: {}", userId);

        EndUserDto endUserDto = endUserService.findEndUserById(userId);

        log.info("end user info: {}", endUserDto);

        return ResponseEntity.ok(endUserDto);
    }

    @Operation(summary = "사용자가 속한 그룹 정보 조회", description = "사용자가 속한 그룹 정보 조회")
    @GetMapping("/party")
    public ResponseEntity<PartyDto> getUserParty() {
        Long userId = SecurityUtil.getCurrentUserId();

        PartyDto partyDto = partyService.findPartyByUserId(userId);

        return ResponseEntity.ok(partyDto);
    }

    @Operation(summary = "가맹점 이미지 조회", description = "사용자 앱에서 사용자가 선택한 가맹점의 이미지 조회")
    @GetMapping("/menu/{fileName}")
    public ResponseEntity<Resource> getMenu(@PathVariable String fileName) {

        log.info("get menu: {}", fileName);

        return customFileUtil.getFile(fileName);
    }

    @Operation(summary = "사용자 비밀번호 수정", description = "사용자 비밀번호 수정")
    @PutMapping("/password")
    public ResponseEntity<String> updateUserPassword(
            @RequestBody PasswordUpdateRequest request) {

        log.info("User password update request: {}", request);
        Long userId = SecurityUtil.getCurrentUserId();

        boolean success = endUserService.updateEndUserPasswordById(userId, request.getOldPassword(), request.getNewPassword());

        if (success) {
            log.info("User password updated successfully");
            return ResponseEntity.ok("비밀번호가 재설정 되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("비밀번호 재설정에 실패하였습니다.");
        }
    }
}
