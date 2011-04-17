package com.rushdevo.twittaddict.exceptions;

public class TwitterCommunicationException extends Exception {
	private static final long serialVersionUID = 774948243656305587L;
	
	private String message;
	private Boolean deauthorized;
	
	public TwitterCommunicationException(String message) {
		this(message, false);
	}
	
	public TwitterCommunicationException(String message, Boolean deauthorized) {
		this.message = message;
		this.deauthorized = deauthorized;
	}
	
	public String getMessage() {
		return message;
	}
	
	public Boolean wasDeauthorized() {
		return deauthorized;
	}
}
