package com.example.backend.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.Model.User;
import com.example.backend.Repository.userRepository;

@Service
public class userServiceImpl implements userService {

	userRepository userRepo;

	@Autowired
	public userServiceImpl(userRepository theUserRepo) {
		this.userRepo = theUserRepo;
	}

	@Override
	public List<User> findAll() {
		return userRepo.findAll();
	}

	@Override
	public User findById(int theId) {
		Optional<User> result = userRepo.findById(theId);

		User theUser = null;

		if (result.isPresent()) {
			theUser = result.get();
		} else {
			// we didn't find the employee
			throw new RuntimeException("Did not find user id- " + theId);
		}

		return theUser;
	}

	@Override
	public void save(User theUser) {
		userRepo.save(theUser);

	}

	@Override
	public void deleteById(int theId) {
		userRepo.deleteById(theId);
	}

}
