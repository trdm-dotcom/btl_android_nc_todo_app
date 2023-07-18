package com.example.todo.servies;

import com.example.todo.common.exception.GeneralException;
import com.example.todo.config.AppConfig;
import com.example.todo.constants.Constants;
import com.example.todo.constants.enums.UserStatus;
import com.example.todo.dao.RedisDao;
import com.example.todo.models.db.User;
import com.example.todo.models.dto.LoginValidate;
import com.example.todo.models.dto.UserData;
import com.example.todo.models.request.LoginRequest;
import com.example.todo.models.request.RegisterRequest;
import com.example.todo.models.response.AuthenticationResponse;
import com.example.todo.repositories.UserRepository;
import com.example.todo.security.CustomUserDetails;
import com.example.todo.utils.Util;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;

@Slf4j
@Service
public class AuthenticationService {
    @Autowired
    private AppConfig appConfig;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private PasswordEncoder passwordEncoder;
//    @Autowired
//    private RedisDao redisDao;

    public AuthenticationResponse login(LoginRequest request) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, IOException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
        request.validate();
        LocalDateTime now = LocalDateTime.now();
        if (!appConfig.getClientSecretLogin().equals(request.getClientSecret())) {
            throw new GeneralException(Constants.INVALID_CLIENT_SECRET);
        }
        User user = this.findAndValidateUser(request, now);
        String password = this.appConfig.getEncryptPassword() ? Util.enCryptRSA(appConfig.getPrivateKey(), request.getPassword()) : request.getPassword();
        if (!passwordEncoder.matches(password.trim(), user.getPassword())) {
            throw new GeneralException(Constants.INVALID_CLIENT_CREDENTIAL);
        }
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        Pair<String, String> pairToken = this.tokenService.generateToken(customUserDetails);
        UserData userData = new UserData(user.getId(), user.getName(), user.getEmail(), user.getStatus());
        return new AuthenticationResponse(pairToken.getSecond(),
                pairToken.getFirst(),
                userData,
                Date.from(now.atZone(ZoneId.systemDefault()).toInstant()).getTime() + this.appConfig.getAccessTokenExpirationInMs(),
                Date.from(now.atZone(ZoneId.systemDefault()).toInstant()).getTime() + this.appConfig.getRefreshTokenExpirationInMs());
    }

    private User findAndValidateUser(LoginRequest request, LocalDateTime now) throws JsonProcessingException {
        User user = null;
        try {
            user = this.userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new GeneralException(Constants.INVALID_USER));
//            LoginValidate loginValidate = this.redisDao.findLoginValidate(request.getEmail());
//            if (loginValidate.getFailCount() >= appConfig.getLoginTemporarilyLocked()) {
//                if (loginValidate.getLatestRequest().plusSeconds(appConfig.getLoginTemporarilyLockedTime()).isAfter(now))
//                    throw new GeneralException(Constants.LOGIN_TEMPORARILY_LOCKED);
//                loginValidate.setFailCount(1);
//            }
//            loginValidate.setFailCount(user == null ? loginValidate.getFailCount() + 1 : 0);
//            loginValidate.setLatestRequest(now);
//            this.redisDao.addLoginValidate(loginValidate);
        } catch (Exception ex) {
            if (!ex.getMessage().equals(Constants.OBJECT_NOT_FOUND)) {
                throw ex;
            }
            LoginValidate loginValidate = new LoginValidate(request.getEmail(), user == null ? 1 : 0, now);
//            this.redisDao.addLoginValidate(loginValidate);
        }
        if(user == null) {
            throw new GeneralException(Constants.INVALID_CLIENT_CREDENTIAL);
        }
        return user;
    }

    public Object register(RegisterRequest request) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, IOException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
        request.validate();
        if (this.userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new GeneralException(Constants.MAIL_ALREADY_EXISTS);
        }
        if (!request.getEmail().matches(Constants.EMAIL_REGEX)) {
            throw new GeneralException(Constants.EMAIL_NOT_MATCHED_POLICY);
        }
        if (!request.getName().matches(Constants.NAME_REGEX)) {
            throw new GeneralException(Constants.NAME_NOT_MATCHED_POLICY);
        }
        String password = this.appConfig.getEncryptPassword() ? Util.enCryptRSA(appConfig.getPrivateKey(), request.getPassword()) : request.getPassword();
        if (!password.matches(Constants.PASSWORD_REGEX)) {
            throw new GeneralException(Constants.PASSWORD_NOT_MATCHED_POLICY);
        }
        User user = new User();
        user.setStatus(UserStatus.ACTIVE);
        user.setPassword(passwordEncoder.encode(password.trim()));
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        this.userRepository.saveAndFlush(user);
        return new HashMap<>();
    }
}
