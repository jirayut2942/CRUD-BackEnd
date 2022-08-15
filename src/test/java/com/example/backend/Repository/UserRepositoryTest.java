package com.example.backend.Repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.backend.Model.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UserRepositoryTest {

	@Autowired
	private userRepository userRepository;

	@DisplayName("JUnit test for save User")
	@Test
	public void givenUserObject_whenSave_thenReturnSavedUser() {
		// given
		User user = User.builder().username("Jirayut Jompong").password("eiei").email("armjirayut@gmail.com").build();

		// when
		User saveUser = userRepository.save(user);

		// then
		assertThat(saveUser).isNotNull();
		assertThat(saveUser.getId()).isGreaterThan(0);
	}

	@DisplayName("JUnit test for get All User")
	@Test
	public void givenUserList_whenFindAll_thenUserList() {
		// given
		User user = User.builder().username("Jirayut Jompong").password("eiei").email("armjirayut@gmail.com").build();
		User user2 = User.builder().username("Mtt").password("eiei123").email("Mtt@gmail.com").build();

		// when
		userRepository.save(user);
		userRepository.save(user2);

		List<User> userList = userRepository.findAll();

		// then
		assertThat(userList).isNotNull();
		// base n in data rows
		assertThat(userList.size()).isEqualTo(3);
	}

	@DisplayName("JUnit test for get User By Id")
	@Test
	public void givenUserObject_whenFindById_thenReturnUserObject() {
		// given
		User user = User.builder().username("Jirayut Jompong").password("eiei").email("armjirayut@gmail.com").build();

		userRepository.save(user);

		// when
		User userDB = userRepository.findById(user.getId()).get();

		// then
		assertThat(userDB).isNotNull();
	}

	@DisplayName("JUnit test for update User")
	@Test
	public void givenUserObject_whenUpdateUser_thenReturnUpdateUser() {
		// given
		User user = User.builder().username("Jirayut Jompong").password("eiei").email("armjirayut@gmail.com").build();

		userRepository.save(user);

		// when
		User saveUser = userRepository.findById(user.getId()).get();
		saveUser.setUsername("Mtt");
		saveUser.setPassword("mtteiei");
		saveUser.setEmail("mtt@gmail.com");

		User updateUser = userRepository.save(saveUser);

		// then
		assertThat(updateUser.getUsername()).isEqualTo("Mtt");
		assertThat(updateUser.getPassword()).isEqualTo("mtteiei");
		assertThat(updateUser.getEmail()).isEqualTo("mtt@gmail.com");
	}

	@DisplayName("JUnit test for delete User")
	@Test
	public void given_when_then() {
		// given - precondition or setup
		User user = User.builder().username("Jirayut Jompong").password("eiei").email("armjirayut@gmail.com").build();

		userRepository.save(user);

		// when - action or the behavior that we are going to test
		userRepository.deleteById(user.getId());
		Optional<User> userOptional = userRepository.findById(user.getId());

		// then - verify the output
		assertThat(userOptional).isEmpty();
	}
}
