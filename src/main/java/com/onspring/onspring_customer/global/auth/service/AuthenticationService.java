package com.onspring.onspring_customer.global.auth.service;

import com.onspring.onspring_customer.domain.franchise.entity.Franchise;
import com.onspring.onspring_customer.domain.franchise.repository.FranchiseRepository;
import com.onspring.onspring_customer.domain.user.entity.EndUser;
import com.onspring.onspring_customer.domain.user.repository.EndUserRepository;
import com.onspring.onspring_customer.global.util.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final FranchiseRepository franchiseRepository;
    private final EndUserRepository endUserRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public String franchiseLogin(String userName, String password) {
        Franchise franchise = franchiseRepository.findByUserName(userName);


        if (!passwordEncoder.matches(password, franchise.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return jwtTokenProvider.createToken(userName, "ROLE_FRANCHISE");
    }

    public String userLogin(String phone, String password) {
        EndUser endUser = endUserRepository.findByPhone(phone);

        if (!passwordEncoder.matches(password, endUser.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return jwtTokenProvider.createToken(phone, "ROLE_USER");
    }
}
