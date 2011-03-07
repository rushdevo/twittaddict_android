package com.rushdevo.twittaddict.data;

import android.provider.BaseColumns;

public interface FriendStats extends BaseColumns {
	public static final String FRIEND_STATS_TABLE_NAME = "friend_stats";
	// Column Names
	public static final String FRIEND_STATS_USER = "user_id";
	public static final String FRIEND_STATS_FRIEND = "friend_id";
	public static final String FRIEND_STATS_CORRECT = "correct";
	public static final String FRIEND_STATS_ATTEMPTS = "attempts";
	public static final String FRIEND_STATS_PERCENT = "percent_correct";
}
