package com.rushdevo.twittaddict.exceptions;

public class TwitterConnectionFailedException extends Exception {
	
	private String message;
	
	public TwitterConnectionFailedException(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
}
