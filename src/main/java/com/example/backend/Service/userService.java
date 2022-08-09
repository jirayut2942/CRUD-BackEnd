package com.example.backend.Service;

import java.util.List;

import com.example.backend.Model.User;

public interface userService {
	public List<User> findAll();

	public User findById(int theId);

	public User save(User theUser);

	public void deleteById(int theId);

}
