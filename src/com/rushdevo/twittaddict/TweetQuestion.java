package com.rushdevo.twittaddict;

import java.util.List;

import com.rushdevo.twittaddict.twitter.TwitterStatus;
import com.rushdevo.twittaddict.twitter.TwitterUser;

public class TweetQuestion implements Question {
	private TwitterStatus status;
	private TwitterUser user1;
	private TwitterUser user2;
	private TwitterUser user3;
	private TwitterUser choice;
	
	public TweetQuestion(TwitterStatus status, List<TwitterUser> users) {
		this.status = status;
		this.user1 = users.get(0);
		this.user2 = users.get(1);
		this.user3 = users.get(2);
	}
	
	public TwitterStatus getStatus() {
		return this.status;
	}
	public TwitterUser getUser1() {
		return this.user1;
	}
	public TwitterUser getUser2() {
		return this.user2;
	}
	public TwitterUser getUser3() {
		return this.user3;
	}
	
	public String getContainerId() {
		return "tweet_question_container";
	}
	
	public void setChoice(int index) {
		switch (index) {
		case 1:
			this.choice = this.user1;
			break;
		case 2:
			this.choice = this.user2;
			break;
		case 3:
			this.choice = this.user3;
			break;
		default:
			this.choice = null;
		}
	}
	
	public boolean isCorrect() {
		return this.choice != null && this.choice == this.status.getUser();
	}
}
