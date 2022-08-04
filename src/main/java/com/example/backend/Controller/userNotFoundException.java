package com.example.backend.Controller;

public class userNotFoundException extends RuntimeException {

	public userNotFoundException() {
		super();
	}

	public userNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public userNotFoundException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public userNotFoundException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public userNotFoundException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
