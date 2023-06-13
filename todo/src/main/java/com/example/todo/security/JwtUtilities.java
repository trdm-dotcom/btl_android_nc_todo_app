package com.example.todo.security;

import com.example.todo.config.AppConfig;
import com.example.todo.models.dto.DataRequest;
import com.example.todo.models.dto.UserData;
import com.example.todo.utils.Util;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
@Component
public class JwtUtilities {
    @Autowired
    private AppConfig appConfig;
    @Autowired
    private ObjectMapper objectMapper;

    public String generateToken(CustomUserDetails userDetails, String rfId) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + this.appConfig.getAccessTokenExpirationInMs());
        UserData userData = new UserData(userDetails.getUser().getId(), userDetails.getUser().getName(),
                userDetails.getUser().getUsername(), userDetails.getUser().getStatus());
        DataRequest dataRequest = new DataRequest();
        dataRequest.setUd(userData);
        dataRequest.setRId(rfId);

        return Jwts.builder()
                .setHeaderParam("kid", UUID.randomUUID().toString())
                .setClaims(this.objectMapper.convertValue(dataRequest, new TypeReference<Map<String, Object>>() {}))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.RS256, Util.getPrivateKey(this.appConfig.getPrivateJwtKey()))
                .compact();
    }

    public Boolean isTokenExpired(String token) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        return extractExpiration(token).before(new Date());
    }

    public DataRequest getDataRequest(String token) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        Claims claims = extractAllClaims(token);
        return this.objectMapper.readValue(claims.get("payload").toString(), DataRequest.class);
    }

    public Date extractExpiration(String token) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        return Jwts.parser().setSigningKey(Util.getPrivateKey(this.appConfig.getPrivateJwtKey())).parseClaimsJws(token).getBody();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(Util.getPrivateKey(this.appConfig.getPrivateJwtKey())).parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            log.info("Invalid JWT signature.");
            log.error("Invalid JWT signature error: ", e);
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT token.");
            log.error("Invalid JWT token error: ", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
            log.error("Expired JWT token error: ", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
            log.error("Unsupported JWT token error: ", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
            log.error("JWT token compact of handler are invalid error: ", e);
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return false;
    }
}
