package com.example.todo;

import com.example.todo.constants.enums.Priority;
import com.example.todo.models.request.*;
import com.example.todo.models.response.AuthenticationResponse;
import com.example.todo.models.response.RefreshTokenResponse;
import com.example.todo.security.JwtUtilities;
import com.example.todo.servies.AuthenticationService;
import com.example.todo.servies.OrganizationService;
import com.example.todo.servies.TaskService;
import com.example.todo.servies.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

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
	static final String rf = "155dd3d5-ebcb-4562-b8c1-6092bf315721";

	@Test
	public void testRegister() throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, IOException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
		RegisterRequest request = new RegisterRequest();
		request.setEmail("test1@gmail.com");
		request.setName("huy");
		request.setPassword("P@ssW0$d");
		authenticationService.register(request);
	}

	@Test
	public void testAuthentication() throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, IOException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
		LoginRequest request = new LoginRequest();
		request.setEmail("test1@gmail.com");
		request.setPassword("P@ssW0$d");
		request.setClientSecret("8VT9s8b0vX");
		AuthenticationResponse response = authenticationService.login(request);
		log.info("{}", response);
	}

	@Test
	public void createOrganization() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		RefreshTokenResponse response = tokenService.refreshToken(rf);
		String acc = response.getAccessToken();
		DataRequest dataRequest =  jwtUtilities.getDataRequest(acc);
		OrganizationRequest request = new OrganizationRequest();
		request.setName("android-nc");
		organizationService.create(dataRequest, request);
	}

	@Test
	public void addMember() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		RefreshTokenResponse response = tokenService.refreshToken(rf);
		String acc = response.getAccessToken();
		DataRequest dataRequest =  jwtUtilities.getDataRequest(acc);
		OrganizationMemberRequest request = new OrganizationMemberRequest();
		request.setOrganizationId(1L);
		request.setUserId(2L);
		organizationService.addMember(dataRequest, request);
	}

	@Test
	public void createTask() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		RefreshTokenResponse response = tokenService.refreshToken(rf);
		String acc = response.getAccessToken();
		DataRequest dataRequest =  jwtUtilities.getDataRequest(acc);
		TaskRequest request = new TaskRequest();
		request.setTitle("test");
		request.setDescription("test");
		request.setRemind(true);
		request.setPriority(Priority.HIGH.name());
		request.setOrganizationId(2L);
		taskService.addTask(dataRequest, request);
	}

	@Test
	public void testAssigneeTask() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		RefreshTokenResponse response = tokenService.refreshToken(rf);
		String acc = response.getAccessToken();
		DataRequest dataRequest =  jwtUtilities.getDataRequest(acc);
		AssigneeRequest request = new AssigneeRequest();
		request.setTask(1L);
		request.setAssignee(2L);
		taskService.assigneeTask(dataRequest, request);
	}

	@Test
	public void testComment() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		RefreshTokenResponse response = tokenService.refreshToken(rf);
		String acc = response.getAccessToken();
		DataRequest dataRequest =  jwtUtilities.getDataRequest(acc);
		CommentRequest request = new CommentRequest();
		request.setContent("ble ble");
		request.setTask(2L);
		taskService.addComment(dataRequest, request);
	}
}
