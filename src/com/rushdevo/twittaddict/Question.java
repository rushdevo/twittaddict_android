package com.rushdevo.twittaddict;

public interface Question {
	/**
	 * 
	 * @return the id of the UI element that contains this type of question
	 */
	public String getContainerId();
	/**
	 * 
	 * Set the user's choice for an answer
	 * @param index
	 */
	public void setChoice(int index);
	/**
	 * 
	 * @return true if the user has set a choice and it matches the correct answer, false otherwise
	 */
	public boolean isCorrect();
}
