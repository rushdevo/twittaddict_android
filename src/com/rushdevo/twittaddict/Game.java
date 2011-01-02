package com.rushdevo.twittaddict;

import java.util.ArrayList;
import java.util.Collections;

import com.rushdevo.twittaddict.constants.Twitter;
import com.rushdevo.twittaddict.twitter.TwitterUser;

public class Game {
	
	private TwitterUser user;
	private Boolean success;
	private ArrayList<String> messages;
	private ArrayList<TwitterUser> friends;
	
	public Game() {
		messages = new ArrayList<String>();
		this.success = true;
		initializeUser();
		initializeFriends();
		return;
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
	
	public ArrayList<TwitterUser> getFriends() {
		return this.friends;
	}
}
