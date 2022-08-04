package com.example.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.Model.User;

public interface userRepository extends JpaRepository<User, Integer> {

}
