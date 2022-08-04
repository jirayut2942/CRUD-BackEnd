package com.example.backend.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
	@PostMapping("/users")
	public User addUser(@RequestBody User theUser) {
		theUser.setId(0);

		userService.save(theUser);

		return theUser;
	}

	// find by id
	@GetMapping("/users/{userId}")
	public User findUsersById(@PathVariable int userId) {

		User theUser = userService.findById(userId);

		if (theUser == null) {
			throw new RuntimeException("User id not found - " + userId);
		}

		return theUser;
	}

	// find All
	@GetMapping("/users")
	public List<User> findAll() {
		return userService.findAll();
	}

	// Update by Id
	@PutMapping("/users/{userId}")
	public User updateUserById(@PathVariable("userId") int userId, @Validated @RequestBody User newtheUser) {
		User theUser = userService.findById(userId);
		if (theUser == null) {
			throw new RuntimeException("User id not found - " + userId);
		}

		theUser.setUsername(newtheUser.getUsername());
		theUser.setPassword(newtheUser.getPassword());
		theUser.setEmail(newtheUser.getEmail());
		userService.save(theUser);
		return theUser;
	}

	@PatchMapping("/users/{userId}")
	public User updateUserFilde(@PathVariable("userId") int userId, @Validated @RequestBody User newtheUser) {
		User theUser = userService.findById(userId);
		if (theUser == null) {
			throw new RuntimeException("User id not found - " + userId);
		}

		theUser.setUsername(newtheUser.getUsername());
//		theUser.setEmail(newtheUser.getEmail());
		userService.save(theUser);
		return theUser;
	}

	// delete By Id
	@DeleteMapping("/users/{userId}")
	public String deleteById(@PathVariable("userId") int userId) {
		User theUser = userService.findById(userId);

		// throw exception if null

		if (theUser == null) {
			throw new RuntimeException("User id not found - " + userId);
		}

		userService.deleteById(userId);

		return "Delete By ID: " + userId;
	}
	
	

	@GetMapping("/users/kafka")
	public void sendMessageToKafkaTopic(@RequestParam("message") String message) {
		this.producer.produce(message);
	}
}


