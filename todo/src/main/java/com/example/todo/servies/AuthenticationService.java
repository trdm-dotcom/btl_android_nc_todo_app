package com.example.todo.servies;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

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
    @Autowired
    private RedisDao redisDao;
    private static final String PASSWORD_REGEX = "^(?<!\\.)(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[\\W,_])[.!-~]{6,}$(?<!\\.)";
    private static final String USERNAME_REGEX = "^(?!\\.)[a-z0-9.]*$(?<!\\.)";

    @Transactional
    public AuthenticationResponse login(LoginRequest request) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, IOException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
        LocalDateTime now = LocalDateTime.now();
        if (!StringUtils.hasText(request.getClientSecret()) || !appConfig.getClientSecretLogin().equals(request.getClientSecret())) {
            throw new RuntimeException(Constants.INVALID_CLIENT_SECRET);
        }
        if (!StringUtils.hasText(request.getUsername()) || !StringUtils.hasText(request.getPassword())) {
            throw new RuntimeException(Constants.USER_INFO_REQUIRED);
        }
        User user = null;
        try {
            user = this.userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new RuntimeException(Constants.INVALID_USER));
            LoginValidate loginValidate = this.redisDao.findLoginValidate(request.getUsername());
            if (loginValidate.getFailCount() >= this.appConfig.getLoginTemporarilyLocked()) {
                if (loginValidate.getLatestRequest().plusSeconds(this.appConfig.getLoginTemporarilyLockedTime()).isAfter(now))
                    throw new RuntimeException(Constants.LOGIN_TEMPORARILY_LOCKED);
                loginValidate.setFailCount(1);
            }
            loginValidate.setFailCount(user == null ? loginValidate.getFailCount() + 1 : 0);
            loginValidate.setLatestRequest(now);
            this.redisDao.addLoginValidate(loginValidate);
        } catch (RuntimeException ex) {
            if (!ex.getMessage().equals(Constants.OBJECT_NOT_FOUND)) {
                throw ex;
            }
            LoginValidate loginValidate = new LoginValidate(request.getUsername(), user == null ? 1 : 0, now);
            this.redisDao.addLoginValidate(loginValidate);
        }
        String password = this.appConfig.getEncryptPassword() ? Util.enCryptRSA(appConfig.getPrivateKey(), request.getPassword()) : request.getPassword();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException(Constants.INVALID_CLIENT_CREDENTIAL);
        }
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        Pair<String, String> pairToken = this.tokenService.generateToken(customUserDetails);
        UserData userData = new UserData(user.getId(), user.getName(), user.getUsername(), user.getStatus());
        return new AuthenticationResponse(pairToken.getSecond(),
                pairToken.getFirst(),
                userData,
                Date.from(now.atZone(ZoneId.systemDefault()).toInstant()).getTime() + this.appConfig.getAccessTokenExpirationInMs(),
                Date.from(now.atZone(ZoneId.systemDefault()).toInstant()).getTime() + this.appConfig.getRefreshTokenExpirationInMs());
    }

    @Transactional
    public void register(RegisterRequest request) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, IOException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
        if (!StringUtils.hasText(request.getUsername()) || !StringUtils.hasText(request.getPassword())) {
            throw new RuntimeException(Constants.USER_INFO_REQUIRED);
        }
        if (this.userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException(Constants.USERNAME_EXISTED);
        }
        if (!request.getUsername().matches(USERNAME_REGEX)) {
            throw new RuntimeException(Constants.USER_NOT_MATCHED_POLICY);
        }
        String password = this.appConfig.getEncryptPassword() ? Util.enCryptRSA(appConfig.getPrivateKey(), request.getPassword()) : request.getPassword();
        if (!password.matches(PASSWORD_REGEX)) {
            throw new RuntimeException(Constants.PASSWORD_NOT_MATCHED_POLICY);
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setStatus(UserStatus.ACTIVE);
        user.setPassword(passwordEncoder.encode(password));
        user.setName(request.getName());
        this.userRepository.saveAndFlush(user);
    }
}
