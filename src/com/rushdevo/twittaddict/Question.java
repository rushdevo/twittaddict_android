package com.rushdevo.twittaddict;

public interface Question {
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
