package com.rushdevo.twittaddict.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.provider.BaseColumns._ID; 

import static com.rushdevo.twittaddict.data.UserColumns.USERS_TABLE_NAME;
import static com.rushdevo.twittaddict.data.UserColumns.USERS_TOKEN;
import static com.rushdevo.twittaddict.data.UserColumns.USERS_LOGIN;
import static com.rushdevo.twittaddict.data.UserColumns.USERS_TOKEN_SECRET;


public class TwittaddictData extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "twittaddict.db";
	private static final int DATABASE_VERSION = 1;
	
	private static final String[] USER_COLUMNS = { _ID, USERS_LOGIN, USERS_TOKEN, USERS_TOKEN_SECRET };

	public TwittaddictData(Context ctx) {
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		StringBuilder sql = new StringBuilder();
		sql.append("CREATE TABLE " + USERS_TABLE_NAME + " (");
		sql.append(_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, ");
		sql.append(USERS_TOKEN_SECRET + " TEXT, ");
		sql.append(USERS_TOKEN + " TEXT, ");
		sql.append(USERS_LOGIN + " TEXT");
		sql.append(");");
		db.execSQL(sql.toString());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO: This should be smarter about migrations
		db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE_NAME);
		onCreate(db);
	}
	
	public Cursor getUsers() {
		SQLiteDatabase db = getReadableDatabase();
		return db.query(USERS_TABLE_NAME, USER_COLUMNS, null, null, null, null, null);
	}
	
	public boolean addUser(String login, String token, String tokenSecret) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(USERS_LOGIN, login);
		values.put(USERS_TOKEN, token);
		values.put(USERS_TOKEN_SECRET, tokenSecret);
		try {
			db.insertOrThrow(USERS_TABLE_NAME, null, values);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

}