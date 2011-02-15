package com.rushdevo.twittaddict;

import java.util.List;

import com.rushdevo.twittaddict.twitter.TwitterStatus;
import com.rushdevo.twittaddict.twitter.TwitterUser;

public class UserQuestion implements Question {
	private TwitterStatus status1;
	private TwitterStatus status2;
	private TwitterStatus status3;
	private TwitterUser user;
	private TwitterStatus choice;
	
	public UserQuestion(TwitterUser user, List<TwitterStatus> statuses) {
		this.user = user;
		this.status1 = statuses.get(0);
		this.status2 = statuses.get(1);
		this.status3 = statuses.get(2);
		this.choice = null;
	}
	
	public TwitterStatus getStatus1() {
		return this.status1;
	}
	public TwitterStatus getStatus2() {
		return this.status2;
	}
	public TwitterStatus getStatus3() {
		return this.status3;
	}
	public TwitterUser getUser() {
		return this.user;
	}
	
	public void setChoice(int index) {
		switch (index) {
		case 1:
			this.choice = this.status1;
			break;
		case 2:
			this.choice = this.status2;
			break;
		case 3:
			this.choice = this.status3;
			break;
		default:
			this.choice = null;
		}
	}
	
	public boolean isAnswered() {
		return this.choice != null;
	}
	
	public boolean isCorrect() {
		return isAnswered() && this.choice.getUser() == this.user;
	}
}
