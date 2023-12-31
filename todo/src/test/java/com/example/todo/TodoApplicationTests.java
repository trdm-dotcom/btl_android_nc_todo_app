package com.example.todo;

import com.example.todo.constants.enums.Priority;
import com.example.todo.models.dto.TaskDto;
import com.example.todo.models.request.*;
import com.example.todo.models.response.AuthenticationResponse;
import com.example.todo.models.response.ListUserResponse;
import com.example.todo.models.response.RefreshTokenResponse;
import com.example.todo.security.JwtUtilities;
import com.example.todo.servies.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Set;

@SpringBootTest
@Slf4j
class TodoApplicationTests {
    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    OrganizationService organizationService;
    @Autowired
    TokenService tokenService;
    @Autowired
    JwtUtilities jwtUtilities;
    @Autowired
    TaskService taskService;
    @Autowired
    UserService userService;
    static final String rf = "a4e11070-21c0-463f-abc1-a23a35bc4bdb";

    @Test
    public void testRegister() throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, IOException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("tranfminh.3@gmail.com");
        request.setName("test");
        request.setPassword("P@ssW0$d");
        authenticationService.register(request);
    }

    @Test
    public void testAuthentication() throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, IOException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
        LoginRequest request = new LoginRequest();
        request.setEmail("tranfminh@gmail.com");
        request.setPassword("P@s$1234");
        request.setClientSecret("8VT9s8b0vX");
        AuthenticationResponse response = authenticationService.login(request);
        log.info("{}", response);
    }

    @Test
    public void createOrganization() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        RefreshTokenResponse response = tokenService.refreshToken(rf);
        String acc = response.getAccessToken();
        DataRequest dataRequest = jwtUtilities.getDataRequest(acc);
        OrganizationRequest request = new OrganizationRequest();
        request.setName("android-nc");
        organizationService.create(dataRequest, request);
    }

    @Test
    public void addMember() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        RefreshTokenResponse response = tokenService.refreshToken(rf);
        String acc = response.getAccessToken();
        DataRequest dataRequest = jwtUtilities.getDataRequest(acc);
        OrganizationMemberRequest request = new OrganizationMemberRequest();
        request.setOrganizationId(4L);
        request.setUserId(Arrays.asList(2L, 3L));
        organizationService.addMember(dataRequest, request);
    }

    @Test
    public void createTask() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        RefreshTokenResponse response = tokenService.refreshToken(rf);
        String acc = response.getAccessToken();
        DataRequest dataRequest = jwtUtilities.getDataRequest(acc);
        TaskRequest request = new TaskRequest();
        request.setTitle("test screen home");
        request.setDescription("");
        request.setRemind(true);
        request.setPriority(Priority.HIGH.name());
        request.setOrganizationId(1L);
        taskService.addTask(dataRequest, request);
    }

    @Test
    public void testAssigneeTask() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        RefreshTokenResponse response = tokenService.refreshToken(rf);
        String acc = response.getAccessToken();
        DataRequest dataRequest = jwtUtilities.getDataRequest(acc);
        AssigneeRequest request = new AssigneeRequest();
        request.setTask(1L);
        request.setAssignee(Arrays.asList(2L, 3L));
        taskService.assigneeTask(dataRequest, request);
    }
//
//    @Test
//    public void testComment() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
//        RefreshTokenResponse response = tokenService.refreshToken(rf);
//        String acc = response.getAccessToken();
//        DataRequest dataRequest = jwtUtilities.getDataRequest(acc);
//        CommentRequest request = new CommentRequest();
//        request.setContent("ble ble");
//        request.setTask(2L);
//        taskService.addComment(dataRequest, request);
//    }
//
//    @Test
//    public void testGetTask() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
//        RefreshTokenResponse response = tokenService.refreshToken(rf);
//        String acc = response.getAccessToken();
//        DataRequest dataRequest = jwtUtilities.getDataRequest(acc);
//        Set<TaskDto> set = taskService.findTaskBy(dataRequest, 100, 0, "20230630", "20230707", null, null, 1L);
//        System.out.println(set);
//    }
//
//    @Test
//    public void testFindAllUser() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
//        RefreshTokenResponse response = tokenService.refreshToken(rf);
//        String acc = response.getAccessToken();
//        DataRequest dataRequest = jwtUtilities.getDataRequest(acc);
//        ListUserResponse result = userService.findAllUser(dataRequest, 1L, null);
//        System.out.println(result);
//    }
}
