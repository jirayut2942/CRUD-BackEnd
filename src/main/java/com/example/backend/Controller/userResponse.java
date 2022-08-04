package com.example.backend.Controller;

import org.springframework.http.HttpStatus;

public class userResponse {
	private HttpStatus status;
	private String message;
	private Object errors;

	public userResponse(HttpStatus status, String message, Object errors) {
		super();
		this.status = status;
		this.message = message;
		this.errors = errors;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getErrors() {
		return errors;
	}

	public void setErrors(Object errors) {
		this.errors = errors;
	}
}
