package za.co.datacentrix.taskservice.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;
import za.co.datacentrix.taskservice.entities.RefreshToken;
import za.co.datacentrix.taskservice.payload.request.RefreshTokenRequest;
import za.co.datacentrix.taskservice.payload.response.RefreshTokenResponse;


import java.util.Optional;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(Long userId);
    RefreshToken verifyExpiration(RefreshToken token);
    Optional<RefreshToken> findByToken(String token);
    RefreshTokenResponse generateNewToken(RefreshTokenRequest request);
    ResponseCookie generateRefreshTokenCookie(String token);
    String getRefreshTokenFromCookies(HttpServletRequest request);
    void deleteByToken(String token);
    ResponseCookie getCleanRefreshTokenCookie();
}
