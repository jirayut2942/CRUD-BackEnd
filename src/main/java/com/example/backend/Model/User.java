package com.example.backend.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

//map Java Class => Database Table
@Entity
@Table(name = "userteacj")
public class User {

	// define data model
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "username")
	@NotEmpty(message = "Username must not be empty")
	private String username;

	@Column(name = "password")
	@NotEmpty(message = "Passwoed must not be empty")
	private String password;

	@Column(name = "email")
	@NotEmpty(message = "Email must not be empty")
	private String email;

	// define default contractor
	public User() {
	}

	public User(int id, String username, String password, String email) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.email = email;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "userModel [id=" + id + ", username=" + username + ", password=" + password + ", email=" + email + "]";
	}

}
