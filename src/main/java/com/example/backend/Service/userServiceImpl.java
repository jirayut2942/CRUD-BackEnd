package com.example.backend.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.Controller.userNotFoundException;
import com.example.backend.Model.User;
import com.example.backend.Repository.userRepository;

@Service
@CacheConfig(cacheNames = "User")
public class userServiceImpl implements userService {

	private final Logger LOG = LoggerFactory.getLogger(getClass());

	private userRepository userRepo;

	@Autowired
	public userServiceImpl(userRepository theUserRepo) {
		this.userRepo = theUserRepo;
	}

	@Override
	public List<User> findAll() {
		return userRepo.findAll();
	}

	@Override
	@Cacheable(key = "#theId", value = "username")
	public User findById(int theId) {
		Optional<User> result = userRepo.findById(theId);

		User theUser = null;

		if (result.isPresent()) {
			theUser = result.get();
		} else {
			// we didn't find the User
			throw new userNotFoundException("Did not find user id- " + theId);
		}
		LOG.info("Retrieve user with ID {}.", theId);
		return theUser;
	}

	@Override
	@CachePut(key = "#theId", value = "username")
	public User save(User theUser) {
		Optional<User> saveUser = userRepo.findById(theUser.getId());

		return userRepo.save(theUser);

	}

	@Override
	@CacheEvict(value = "username", allEntries = true)
	public void deleteById(int theId) {
		userRepo.deleteById(theId);
	}

}
