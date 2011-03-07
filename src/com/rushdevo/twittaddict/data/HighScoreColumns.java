package com.rushdevo.twittaddict.data;

import android.provider.BaseColumns;

public interface HighScoreColumns extends BaseColumns {
	public static final String HIGH_SCORES_TABLE_NAME = "high_scores";
	// Column Names
	public static final String HIGH_SCORES_USER = "user_id";
	public static final String HIGH_SCORES_MODE = "mode";
	public static final String HIGH_SCORES_SCORE = "score";
	public static final String HIGH_SCORES_TIMESTAMP = "score_timestamp";
}
