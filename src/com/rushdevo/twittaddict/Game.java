package com.rushdevo.twittaddict;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.rushdevo.twittaddict.constants.Twitter;
import com.rushdevo.twittaddict.twitter.TwitterStatus;
import com.rushdevo.twittaddict.twitter.TwitterUser;

public class Game {
	
	private TwitterUser user;
	private Boolean success;
	private List<String> messages;
	private List<TwitterUser> friends;
	private List<TwitterStatus> statuses;
	
	public Game() {
		messages = new ArrayList<String>();
		this.success = true;
		initializeUser();
		initializeFriends();
		initializeStatuses();
	}

	private void initializeUser() {
		this.user = Twitter.getUser();
		if (this.user == null) {
			success = false;
			messages.add("Unable to find screen name for logged in user.");
		}
	}
	
	private void initializeFriends() {
		ArrayList<Long> ids = Twitter.getFriendIds();
		if (ids == null) {
			this.success = false;
			messages.add("Unable to find friends for " + getScreenName() + ".");
		} else {
			Collections.shuffle(ids);
			this.friends = Twitter.getUsers(ids);
			if (this.friends == null || this.friends.isEmpty()) {
				this.success = false;
				messages.add("Unable to retrieve friends for " + getScreenName() + ".");
			}
		}
	}
	
	private void initializeStatuses() {
		this.statuses = Twitter.getStatuses();
		if (this.statuses == null || this.statuses.isEmpty()) {
			this.success = false;
			messages.add("Unable to retrieve statuses for " + getScreenName() + ".");
		}
	}
	
	public TwitterUser getUser() {
		return this.user;
	}
	
	public String getScreenName() {
		if (this.user == null || this.user.getScreenName() == null) {
			return "unknown twitter user";
		} else {
			return this.user.getScreenName();
		}
	}

	public Boolean getSuccess() {
		return success;
	}
	
	public List<TwitterUser> getFriends() {
		return this.friends;
	}
}
