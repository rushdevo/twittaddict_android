package com.rushdevo.twittaddict.data;

import android.provider.BaseColumns;

public interface UserColumns extends BaseColumns{
	public static final String USERS_TABLE_NAME = "users";
	// Column Names
	public static final String USERS_LOGIN = "login";
	public static final String USERS_TOKEN = "token";
	public static final String USERS_TOKEN_SECRET = "token_secret";
}
