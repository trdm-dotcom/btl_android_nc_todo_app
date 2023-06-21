package com.example.todo.servies;

import com.example.todo.common.exception.GeneralException;
import com.example.todo.config.AppConfig;
import com.example.todo.constants.Constants;
import com.example.todo.models.db.User;
import com.example.todo.models.dto.UserData;
import com.example.todo.models.request.ConfirmRequest;
import com.example.todo.models.request.DataRequest;
import com.example.todo.models.request.UpdatePasswordRequest;
import com.example.todo.models.request.UserRequest;
import com.example.todo.repositories.UserRepository;
import com.example.todo.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AppConfig appConfig;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserData getUser(Long id) {
        User user = this.userRepository.findById(id).orElseThrow(() -> new GeneralException(Constants.USER_NOT_FOUND));
        return new UserData(user.getId(), user.getName(), user.getEmail(), user.getStatus());
    }

    public Object updateUser(DataRequest dataRequest, UserRequest request) {
        request.validate();
        if (!request.getName().matches(Constants.NAME_REGEX)) {
            new GeneralException(Constants.NAME_NOT_MATCHED_POLICY);
        }
        User user = this.userRepository.findById(dataRequest.getUserData().getId()).orElseThrow(() -> new GeneralException(Constants.USER_NOT_FOUND));
        user.setName(request.getName());
        this.userRepository.save(user);
        return new HashMap<>();
    }

    public Object changePassword(DataRequest dataRequest, UpdatePasswordRequest request) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, IOException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
        request.validate();
        User user = this.userRepository.findById(dataRequest.getUserData().getId()).orElseThrow(() -> new GeneralException(Constants.USER_NOT_FOUND));
        String oldPassword = this.appConfig.getEncryptPassword() ? Util.enCryptRSA(appConfig.getPrivateKey(), request.getOldPassword()) : request.getOldPassword();
        String newPassword = this.appConfig.getEncryptPassword() ? Util.enCryptRSA(appConfig.getPrivateKey(), request.getNewPassword()) : request.getNewPassword();
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new GeneralException(Constants.WRONG_PASSWORD);
        }
        if (newPassword.equals(oldPassword)) {
            throw new GeneralException(Constants.PASSWORD_HAS_NOT_BEEN_CHANGED);
        }
        if (!newPassword.matches(Constants.PASSWORD_REGEX)) {
            throw new GeneralException(Constants.PASSWORD_NOT_MATCHED_POLICY);
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        this.userRepository.save(user);
        return new HashMap<>();
    }

    public Object confirm(DataRequest dataRequest, ConfirmRequest request) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, IOException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
        request.validate();
        User user = this.userRepository.findById(dataRequest.getUserData().getId()).orElseThrow(() -> new GeneralException(Constants.USER_NOT_FOUND));
        String password = this.appConfig.getEncryptPassword() ? Util.enCryptRSA(appConfig.getPrivateKey(), request.getPassword()) : request.getPassword();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new GeneralException(Constants.WRONG_PASSWORD);
        }
        return new HashMap<>();
    }

    public Object deleteUser(Long id) {
        User user = this.userRepository.findById(id).orElseThrow(() -> new GeneralException(Constants.USER_NOT_FOUND));
        this.userRepository.save(user);
        return new HashMap<>();
    }

    public Set<UserData> findUser(DataRequest request, String search) {
        return this.userRepository.findByEmailLike(search)
                .stream()
                .filter(user -> user.getId() != request.getUserData().getId())
                .map(user -> new UserData(user.getId(), user.getName(), user.getEmail(), user.getStatus()))
                .collect(Collectors.toSet());
    }
}
