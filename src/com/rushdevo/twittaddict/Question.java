package com.rushdevo.twittaddict;

import com.rushdevo.twittaddict.twitter.TwitterUser;

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
	/**
	 * 
	 * @return true if the question has already been answered
	 */
	public boolean isAnswered();
	/**
	 * 
	 * @return TwitterUser: the user associated with the correct answer to the question
	 */
	public TwitterUser getCorrectUser();
}
