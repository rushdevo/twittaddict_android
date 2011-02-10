package com.rushdevo.twittaddict;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import android.content.Context;
import android.util.Log;

import com.rushdevo.twittaddict.constants.Twitter;
import com.rushdevo.twittaddict.exceptions.TwitterCommunicationException;
import com.rushdevo.twittaddict.exceptions.TwitterException;
import com.rushdevo.twittaddict.exceptions.TwitterOAuthException;
import com.rushdevo.twittaddict.twitter.TwitterStatus;
import com.rushdevo.twittaddict.twitter.TwitterUser;

public class Game {
	
	private TwitterUser user;
	private Boolean success;
	private Set<String> messages;
	private List<TwitterUser> friends;
	private List<TwitterStatus> statuses;
	private List<TwitterStatus> backupStatuses;
	public static final int PENDING = 0;
	public static final int IN_PLAY = 1;
	public static final int COMPLETE = 2;
	private int state;
	
	public Game(Context ctx) {
		messages = new HashSet<String>();
		this.success = true;
		initializeUser(ctx);
		initializeFriends(ctx);
		initializeStatuses(ctx);
		this.state = PENDING;
	}
	
	public void start() {
		this.state = IN_PLAY;
	}
	
	public void finish() {
		this.state = COMPLETE;
	}
	
	public int getState() {
		return this.state;
	}
	
	public Question getNextQuestion() {
		Question question;
		// Generate question of random type (tweet or user)
		if (new Random().nextBoolean()) {
			question = generateTweetQuestion();
		} else {
			question = generateUserQuestion();
		}
		return question;
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
	
	public Set<String> getMessages() {
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
	
	private void initializeUser(Context ctx) {
		try {
			this.user = Twitter.getUser(ctx);
		} catch (TwitterException e) {
			debug(e);
			success = false;
			messages.add(ctx.getString(R.string.screen_name_failure));
		} catch (TwitterOAuthException e) {
			debug(e);
			success = false;
			messages.add(e.getMessage());
		} catch (TwitterCommunicationException e) {
			debug(e);
			success = false;
			messages.add(e.getMessage());
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
		} catch (TwitterOAuthException e) {
			debug(e);
			success = false;
			messages.add(e.getMessage());
		} catch (TwitterCommunicationException e) {
			debug(e);
			success = false;
			messages.add(e.getMessage());
		}
	}
	
	private void initializeStatuses(Context ctx) {
		try {
			this.statuses = Twitter.getStatuses(ctx);
			// Make a backup list because we change statuses as we go
			this.backupStatuses = new ArrayList<TwitterStatus>();
			this.backupStatuses.addAll(this.statuses);
			if (this.statuses.isEmpty()) {
				debug("No status for " + getScreenName(ctx));
				this.success = false;
				messages.add(ctx.getString(R.string.no_statuses_failure) + " " + getScreenName(ctx) + ".");
			}
		} catch (TwitterException e) {
			debug(e);
			this.success = false;
			messages.add(ctx.getString(R.string.no_statuses_failure) + " " + getScreenName(ctx) + ".");
		} catch (TwitterOAuthException e) {
			debug(e);
			success = false;
			messages.add(e.getMessage());
		} catch (TwitterCommunicationException e) {
			debug(e);
			success = false;
			messages.add(e.getMessage());
		}
	}
	
	/**
	 * Generate a TweetQuestion - a question where you are given a tweet
	 * and you pick which user tweeted it (from three possible users)
	 * @return TweetQuestion
	 */
	private TweetQuestion generateTweetQuestion() {
		TwitterStatus status = getRandomStatus();
		List<TwitterUser> users = getThreeRandomUsers();
		return new TweetQuestion(status, users);
	}
	/**
	 * Generate a UserQuestion - a question where you are given a user
	 * and you pick which tweet belongs to them (from three tweets)
	 * @return UserQuestion
	 */
	private UserQuestion generateUserQuestion() {
		TwitterUser user = getRandomUser();
		List<TwitterStatus> statuses = getThreeRandomStatuses();
		return new UserQuestion(user, statuses);
	}
	private TwitterStatus getRandomStatus() {
		// If we've run through all the statuses, reload them from backup and start over
		if (this.statuses.isEmpty()) this.statuses.addAll(this.backupStatuses);
		// Return a random status from the list
		int index = new Random().nextInt() % this.statuses.size();
		return this.statuses.remove(index);
	}
	private TwitterUser getRandomUser() {
		int index = new Random().nextInt() % this.friends.size();
		return this.friends.get(index);
	}
	private List<TwitterStatus> getThreeRandomStatuses() {
		List<TwitterStatus> statuses = new ArrayList<TwitterStatus>();
		statuses.add(getRandomStatus());
		statuses.add(getRandomStatus());
		statuses.add(getRandomStatus());
		return statuses;
	}
	private List<TwitterUser> getThreeRandomUsers() {
		List<TwitterUser> users = new ArrayList<TwitterUser>();
		int size = this.friends.size();
		if (size <= 3) {
			// Just shuffle and return the whole thing
			users.addAll(this.friends);
			Collections.shuffle(users);
		} else {
			// Find three random users
			TwitterUser user;
			while (users.size() < 3) {
				user = getRandomUser();
				if (!users.contains(user)) {
					users.add(user);
				}
			}
		}
		return users;
	}
	
	private void debug(String message) {
    	Log.d("Twitter", message);
    }
    
    private void debug(Exception e) {
    	debug(e.getClass().toString() + " error: " + e.getMessage());
    }
}
