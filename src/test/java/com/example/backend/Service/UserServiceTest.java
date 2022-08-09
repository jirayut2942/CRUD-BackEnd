package com.example.backend.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import org.mockito.junit.jupiter.MockitoExtension;

import com.example.backend.Model.User;
import com.example.backend.Repository.userRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
	@Mock
	private userRepository userRepository;

	@InjectMocks
	private userServiceImpl userService;

	private User user;

	@BeforeEach
	public void setup() {
		user = User.builder().id(1).username("jirayut jompong").password("eiei123").email("Jirayut@gmail.com").build();
	}

	@DisplayName("JUnit test for save User")
	@Test
	public void givenUserObject_whenSave_thenReturnUserObject() {
		// given - precondition or setup
		given(userRepository.findById(user.getId())).willReturn(Optional.empty());

		given(userRepository.save(user)).willReturn(user);

		// when - action or the behavior that we are going to test
		User saveUser = userService.save(user);

		// then - verify the output
		assertThat(saveUser).isNotNull();
	}

	@DisplayName("JUnit test for get all User")
	@Test
	public void givenUserList_whenFindAll_thenreturnList() {
		// given - precondition or setup
		User user2 = User.builder().id(2).username("mtt").password("eiei12345").email("mtt@gmail.com").build();

		given(userRepository.findAll()).willReturn(List.of(user, user2));

		// when - action or the behavior that we are going to test
		List<User> userList = userService.findAll();

		// then - verify the output
		assertThat(userList).isNotNull();
		assertThat(userList.size()).isEqualTo(2);
	}

	@DisplayName("JUnit test for get User By Id")
	@Test
	public void givenUserId_whenFindById_thenReturnUserObject() {
		// given - precondition or setup
		given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

		// when - action or the behavior that we are going to test
		User userDB = userService.findById(user.getId());

		// then - verify the output
		assertThat(userDB).isNotNull();
	}

	@DisplayName("JUnit test for delete User")
	@Test
	public void givenUserId_whenDelete_thenDoNoting() {
		// given - precondition or setup
		int UserId = 1;
		willDoNothing().given(userRepository).deleteById(UserId);

		// when - action or the behavior that we are going to test
		userService.deleteById(UserId);

		// then - verify the output
		verify(userRepository, times(1)).deleteById(UserId);
	}
}
