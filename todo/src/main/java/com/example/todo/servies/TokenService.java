package com.example.todo.servies;

import com.example.todo.common.exception.GeneralException;
import com.example.todo.config.AppConfig;
import com.example.todo.constants.Constants;
import com.example.todo.models.db.RefreshToken;
import com.example.todo.models.response.RefreshTokenResponse;
import com.example.todo.repositories.RefreshTokenRepository;
import com.example.todo.security.CustomUserDetails;
import com.example.todo.security.JwtUtilities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.UUID;
@Slf4j
@Service
public class TokenService {
    @Autowired
    private AppConfig appConfig;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private JwtUtilities jwtUtilities;

    public Pair<String, String> generateToken(CustomUserDetails customUserDetails) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        Date now = new Date();
        Date expiredAt = new Date(now.getTime() + this.appConfig.getRefreshTokenExpirationInMs());
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUser(customUserDetails.getUser());
        refreshToken.setExpiredAt(expiredAt);
        refreshToken = this.refreshTokenRepository.save(refreshToken);
        String accessToken = this.jwtUtilities.generateToken(customUserDetails, refreshToken.getToken());
        return Pair.of(refreshToken.getToken(), accessToken);
    }

    public RefreshTokenResponse refreshToken(String token) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        Date now = new Date();
        RefreshToken refreshToken = this.refreshTokenRepository.findByToken(token).orElseThrow(() -> new GeneralException(Constants.INVALID_REFRESH_TOKEN));
        if(refreshToken.getExpiredAt().before(now)) {
            throw new GeneralException(Constants.REFRESH_TOKEN_EXPIRED);
        }
        CustomUserDetails customUserDetails = new CustomUserDetails(refreshToken.getUser());
        String accessToken = this.jwtUtilities.generateToken(customUserDetails, refreshToken.getToken());
        return new RefreshTokenResponse(accessToken, new Date(now.getTime() + this.appConfig.getAccessTokenExpirationInMs()).getTime());
    }
}
