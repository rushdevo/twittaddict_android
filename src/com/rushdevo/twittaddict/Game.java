package com.rushdevo.twittaddict;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.rushdevo.twittaddict.constants.Twitter;
import com.rushdevo.twittaddict.exceptions.TwitterException;
import com.rushdevo.twittaddict.twitter.TwitterStatus;
import com.rushdevo.twittaddict.twitter.TwitterUser;

public class Game {
	
	private TwitterUser user;
	private Boolean success;
	private List<String> messages;
	private List<TwitterUser> friends;
	private List<TwitterStatus> statuses;
	
	public Game(Context ctx) {
		messages = new ArrayList<String>();
		this.success = true;
		initializeUser(ctx);
		initializeFriends(ctx);
		initializeStatuses(ctx);
	}

	private void initializeUser(Context ctx) {
		try {
			this.user = Twitter.getUser(ctx);
		} catch (TwitterException e) {
			debug(e);
			success = false;
			messages.add(ctx.getString(R.string.screen_name_failure));
		}
	}
	
	private void initializeFriends(Context ctx) {
		try {
			ArrayList<Long> ids = Twitter.getFriendIds(ctx);
			Collections.shuffle(ids);
			this.friends = Twitter.getUsers(ids, ctx);
			if (this.friends.isEmpty()) {
				debug("No friends for " + getScreenName(ctx));
				this.success = false;
				messages.add(ctx.getString(R.string.no_friends_failure) + " " + getScreenName(ctx) + ".");
			}
		} catch (TwitterException e) {
			debug(e);
			this.success = false;
			messages.add(ctx.getString(R.string.no_friends_failure) + " " + getScreenName(ctx) + ".");
		}
	}
	
	private void initializeStatuses(Context ctx) {
		try {
			this.statuses = Twitter.getStatuses(ctx);
			if (this.statuses.isEmpty()) {
				debug("No status for " + getScreenName(ctx));
				this.success = false;
				messages.add(ctx.getString(R.string.no_statuses_failure) + " " + getScreenName(ctx) + ".");
			}
		} catch (TwitterException e) {
			debug(e);
			this.success = false;
			messages.add(ctx.getString(R.string.no_statuses_failure) + " " + getScreenName(ctx) + ".");
		}
	}
	
	public TwitterUser getUser() {
		return this.user;
	}
	
	public String getScreenName(Context ctx) {
		if (this.user == null || this.user.getScreenName() == null) {
			return ctx.getString(R.string.unknown_user);
		} else {
			return this.user.getScreenName();
		}
	}

	public Boolean getSuccess() {
		return success;
	}
	
	public List<String> getMessages() {
		return this.messages;
	}
	
	public String getFormattedMessage() {
		if (this.messages.isEmpty()) return null;
		String message = "";
		Iterator<String> iter = this.messages.iterator();
		while (iter.hasNext()) {
			message += iter.next();
			if (iter.hasNext()) message += "\n\n";
		}
		return message;
	}
	
	public List<TwitterUser> getFriends() {
		return this.friends;
	}
	
	private void debug(String message) {
    	Log.d("Twitter", message);
    }
    
    private void debug(Exception e) {
    	debug(e.getClass().toString() + " error: " + e.getMessage());
    }
}
