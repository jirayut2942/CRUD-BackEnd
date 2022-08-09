package com.example.backend.Controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import com.example.backend.Model.User;
import com.example.backend.Repository.userRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

//	@MockBean
//	private userService userService;

	@MockBean
	private userRepository userRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@DisplayName("Create User")
	@Test
	public void givenUserObject_whenCreateUser_thenReturnSaveUser() throws Exception {
		// given - precondition or setup
		User user = User.builder().username("Jirayut").password("eiei123").email("Jirayut@gmail.com").build();

		// when - action or the behavior that we are going to test
		ResultActions response = mockMvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(user)));
		// then - verify the output
		response.andExpect(status().isCreated()).andDo(print())
				.andExpect(jsonPath("$.username", is(user.getUsername())))
				.andExpect(jsonPath("$.password", is(user.getPassword())))
				.andExpect(jsonPath("$.email", is(user.getEmail())));
	}

	@DisplayName("FindAll Users")
	@Test
	public void givenListUsers_whenFindAll_thenReturnUserList() throws Exception {
		// given - precondition or setup
		List<User> listUser = new ArrayList<>();
		listUser.add(User.builder().username("jirayut").password("eiei").email("jirayut@gmail.com").build());
		listUser.add(User.builder().username("mtt").password("eiei123").email("mtt@gmail.com").build());
		given(userRepository.findAll()).willReturn(listUser);

		// when - action or the behavior that we are going to test
		ResultActions response = mockMvc.perform(get("/api/v1/users"));

		// then - verify the output
		response.andExpect(status().isOk()).andDo(print()).andExpect(jsonPath("$.size()", is(listUser.size())));
	}

	@DisplayName("FindById User")
	@Test
	public void givenUserId_whenFindById_thenReturnUserObject() throws Exception {
		// given - precondition or setup
		int userId = 1;
		User user = User.builder().username("Jirayut").password("123").email("jirayut@gmail.com").build();

		given(userRepository.findById(userId)).willReturn(Optional.of(user));

		// when - action or the behavior that we are going to test
		ResultActions response = mockMvc.perform(get("/api/v1/users/{userId}", userId));

		// then - verify the output
		response.andExpect(status().isOk()).andDo(print()).andExpect(jsonPath("$.username", is(user.getUsername())))
				.andExpect(jsonPath("$.password", is(user.getPassword())))
				.andExpect(jsonPath("$.email", is(user.getEmail())));
	}

	@DisplayName("Update User")
	@Test
	public void givenUpdateUser_whenUpdateUser_thenReturnUserObject() throws Exception {
		// given - precondition or setup
//		int userId = 1;
		User saveUser = User.builder().id(1).username("Jirayut").password("eiei123").email("Jirayut@gmail.com").build();

		User uppdateUser = User.builder().id(1).username("Mtt").password("123").email("mtt@gmail.com").build();

		Mockito.when(userRepository.findById(saveUser.getId())).thenReturn(Optional.of(saveUser));
		Mockito.when(userRepository.save(uppdateUser)).thenReturn(uppdateUser);

		// when - action or the behavior that we are going to test
		ResultActions response = mockMvc.perform(put("/api/v1/users/{userId}", saveUser.getId())
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(uppdateUser)));

		// then - verify the output
		response.andExpect(status().isOk()).andDo(print())
				.andExpect(jsonPath("$.username", is(uppdateUser.getUsername())))
				.andExpect(jsonPath("$.password", is(uppdateUser.getPassword())))
				.andExpect(jsonPath("$.email", is(uppdateUser.getEmail())));
	}

	@DisplayName("Delete User")
	@Test
	public void givenUserId_whenDeleteUser_thenReturnHttpStatus200() throws Exception {
		// given - precondition or setup
		User user = User.builder().id(1).username("Jirayut").password("eiei123").email("Jirayut@gmail.com").build();

		Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

		// when - action or the behavior that we are going to test
		ResultActions response = mockMvc
				.perform(delete("/api/v1/users/{userId}", user.getId()).contentType(MediaType.APPLICATION_JSON));

		// then - verify the output
		response.andExpect(status().isOk());

	}
}
