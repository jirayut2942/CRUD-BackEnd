package com.example.backend.Controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.backend.Model.User;
import com.example.backend.Service.userService;
import com.example.backend.Service.Producer;

@RestController
@RequestMapping("/api/v1")
public class userController {

	private userService userService;

	@Autowired
	private Producer producer;

	@Autowired
	public userController(userService theUserService) {
		this.userService = theUserService;
	}

	// Hello Team Page : 1.2
	@GetMapping("/hello")
	public String Hello() {
		return "Hello backend team";
	}

	// create User POST : 1.7
//	@PostMapping("/users")
//	public User addUser(@RequestBody User theUser) {
//		theUser.setId(0);
//
//		userService.save(theUser);
//
//		return theUser;
//	}

	@PostMapping("/users")
	public ResponseEntity<User> addEmployee(@Valid @RequestBody User theUser) {
		theUser.setId(0);
		//
		userService.save(theUser);
		//
		return new ResponseEntity<User>(theUser, HttpStatus.OK);
	}

	// find by id
	@GetMapping("/users/{userId}")
	public User findUsersById(@PathVariable int userId) {
		try {
			User theUser = userService.findById(userId);

			return theUser;
		} catch (Exception e) {
//			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found By ID: " + userId, e);
			throw new userNotFoundException("User id not found - " + userId);
		}

	}

	// find All
	@GetMapping("/users")
	public List<User> findAll() {
		return userService.findAll();
	}

	// Update by Id
	@PutMapping("/users/{userId}")
	public User updateUserById(@PathVariable("userId") int userId, @Validated @RequestBody User newtheUser) {
		try {
			User theUser = userService.findById(userId);

			theUser.setUsername(newtheUser.getUsername());
			theUser.setPassword(newtheUser.getPassword());
			theUser.setEmail(newtheUser.getEmail());
			userService.save(theUser);

			return theUser;
		} catch (Exception e) {
			throw new userNotFoundException("User id not found - " + userId);
		}

	}

	@PatchMapping("/users/{userId}")
	public User updateUserFilde(@PathVariable("userId") int userId, @Validated @RequestBody User newtheUser) {
		try {
			User theUser = userService.findById(userId);

			theUser.setUsername(newtheUser.getUsername());
//			theUser.setEmail(newtheUser.getEmail());
			userService.save(theUser);

			return theUser;

		} catch (Exception e) {
			throw new userNotFoundException("User id not found - " + userId);
		}

	}

	// delete By Id
	@DeleteMapping("/users/{userId}")
	public String deleteById(@PathVariable("userId") int userId) {
		try {
			User theUser = userService.findById(userId);

			userService.deleteById(userId);

			return "Delete By ID: " + userId;

		} catch (Exception e) {
			throw new userNotFoundException("User id not found - " + userId);
		}

	}

	// kafka message
	@GetMapping("/users/kafka")
	public void sendMessageToKafkaTopic(@RequestParam("message") String message) {
		this.producer.produce(message);
	}
}
