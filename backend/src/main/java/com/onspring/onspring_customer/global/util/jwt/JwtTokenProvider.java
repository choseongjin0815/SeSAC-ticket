package com.onspring.onspring_customer.global.util.jwt;

import com.onspring.onspring_customer.security.CustomUserDetails;
import com.onspring.onspring_customer.security.RefreshTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

@Log4j2
@Component
public class JwtTokenProvider {

    private final Key secretKey;
    private final long tokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;
    private final RefreshTokenService refreshTokenService;


    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKeyString,
            @Value("${jwt.token-validity-in-seconds}") long tokenValidityInSeconds,
            @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValidityInSeconds, RefreshTokenService refreshTokenService
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes());
        this.tokenValidityInMilliseconds = tokenValidityInSeconds * 1000;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInSeconds * 1000;
        this.refreshTokenService = refreshTokenService;
    }

    public String createToken(Long id, String username, String role) { // userId 파라미터 추가
        Date now = new Date();
        Date validity = new Date(now.getTime() + tokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .claim("id", id) // userId 클레임 추가
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(Long id, String username, String role) { // userId 파라미터 추가
        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshTokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .claim("id", id) // userId 클레임 추가
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }



    public String reissueAccessToken(String refreshToken) {
        if (validateToken(refreshToken)) {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody();

            Long id = claims.get("id", Long.class); // userId 추출
            String username = claims.getSubject();
            String role = claims.get("role", String.class);

            try {
                if(role.equals("ROLE_USER")) {
                    String savedRefreshToken = refreshTokenService.getRefreshToken("user" + id);

                } else {
                    String savedRefreshToken = refreshTokenService.getRefreshToken("franchise" + id);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }



            return createToken(id, username, role); // userId 포함 생성
        }
        throw new RuntimeException("Invalid Refresh Token");
    }


    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Long id = claims.get("id", Long.class); // 클레임에서 id 추출
        String username = claims.getSubject(); // 클레임에서 username 추출
        String role = claims.get("role", String.class); // 클레임에서 role 추출

        Collection<? extends GrantedAuthority> authorities =
                Arrays.asList(new SimpleGrantedAuthority(role));

        // CustomUserDetails 객체 생성
        CustomUserDetails customUserDetails = new CustomUserDetails(id, username, "", role, authorities);
        log.info(customUserDetails);
        return new UsernamePasswordAuthenticationToken(
                customUserDetails, // principal에 CustomUserDetails 설정
                null,
                authorities
        );
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}