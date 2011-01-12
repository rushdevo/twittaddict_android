package com.rushdevo.twittaddict.exceptions;

public class TwitterOAuthException extends Exception {
	private static final long serialVersionUID = -2004709757952610875L;

	private String message;
	
	public TwitterOAuthException(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
}
