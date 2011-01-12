package com.rushdevo.twittaddict.exceptions;

public class TwitterCommunicationException extends Exception {
	private static final long serialVersionUID = 774948243656305587L;
	
	private String message;
	
	public TwitterCommunicationException(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
}
