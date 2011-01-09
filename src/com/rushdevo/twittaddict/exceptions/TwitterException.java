package com.rushdevo.twittaddict.exceptions;

public class TwitterException extends Exception {
	private static final long serialVersionUID = -2164789784353590610L;
	
	private String message;
	
	public TwitterException(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
}
