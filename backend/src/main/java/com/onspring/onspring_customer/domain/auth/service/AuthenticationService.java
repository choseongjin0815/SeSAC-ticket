package com.onspring.onspring_customer.domain.auth.service;

import com.onspring.onspring_customer.domain.franchise.dto.FranchiseDto;
import com.onspring.onspring_customer.domain.franchise.entity.Franchise;
import com.onspring.onspring_customer.domain.franchise.repository.FranchiseRepository;
import com.onspring.onspring_customer.domain.user.entity.EndUser;
import com.onspring.onspring_customer.domain.user.repository.EndUserRepository;
import com.onspring.onspring_customer.domain.auth.dto.LoginResponseDto;
import com.onspring.onspring_customer.global.util.jwt.JwtTokenProvider;
import com.onspring.onspring_customer.security.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final FranchiseRepository franchiseRepository;
    private final EndUserRepository endUserRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final RefreshTokenService refreshTokenService;

    public LoginResponseDto franchiseLogin(String userName, String password) {

        Franchise franchise = franchiseRepository.findByUserName(userName);


        if (!passwordEncoder.matches(password, franchise.getPassword())) {
            throw new BadCredentialsException("잘못된 비밀번호");
        }

        String accessToken = jwtTokenProvider.createToken(
                franchise.getId(),
                franchise.getUserName(),
                "ROLE_FRANCHISE"
        );
        String refreshToken = jwtTokenProvider.createRefreshToken(
                franchise.getId(),
                franchise.getUserName(),
                "ROLE_FRANCHISE"
        );
        refreshTokenService.saveRefreshToken(franchise.getId(), refreshToken);


        FranchiseDto franchiseDto = modelMapper.map(franchise, FranchiseDto.class);

        return new LoginResponseDto(accessToken, refreshToken, franchise.getId());
    }

    public LoginResponseDto userLogin(String phone, String password) {

        EndUser endUser = endUserRepository.findByPhone(phone);

        if (!passwordEncoder.matches(password, endUser.getPassword())) {
            throw new BadCredentialsException("잘못된 비밀번호");
        }

        String accessToken = jwtTokenProvider.createToken(
                endUser.getId(),
                endUser.getPhone(),
                "ROLE_USER"
        );
        String refreshToken = jwtTokenProvider.createRefreshToken(
                endUser.getId(),
                endUser.getPhone(),
                "ROLE_USER"
        );

        refreshTokenService.saveRefreshToken(endUser.getId(), refreshToken);

        return new LoginResponseDto(accessToken, refreshToken, endUser.getId());
    }


}