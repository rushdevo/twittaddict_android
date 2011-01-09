package com.rushdevo.twittaddict.data;

import static android.provider.BaseColumns._ID;
import static com.rushdevo.twittaddict.data.UserColumns.USERS_TABLE_NAME;
import static com.rushdevo.twittaddict.data.UserColumns.USERS_TOKEN;
import static com.rushdevo.twittaddict.data.UserColumns.USERS_TOKEN_SECRET;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class TwittaddictData extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "twittaddict.db";
	private static final int DATABASE_VERSION = 1;
	
	private static final String[] USER_COLUMNS = { _ID, USERS_TOKEN, USERS_TOKEN_SECRET };
	
	private Activity ctx;

	public TwittaddictData(Context ctx) {
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
		this.ctx = (Activity)ctx;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		StringBuilder sql = new StringBuilder();
		sql.append("CREATE TABLE " + USERS_TABLE_NAME + " (");
		sql.append(_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, ");
		sql.append(USERS_TOKEN_SECRET + " TEXT, ");
		sql.append(USERS_TOKEN + " TEXT");
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
		Cursor cursor = db.query(USERS_TABLE_NAME, USER_COLUMNS, null, null, null, null, null);
		ctx.startManagingCursor(cursor);
		return cursor;
	}
	
	public boolean addUser(String token, String tokenSecret) {
		SQLiteDatabase db = getWritableDatabase();
		// Get rid of any previously existing user records - there can only be one
		truncateUsersTable(db);
		// Then add the new one
		ContentValues values = new ContentValues();
		values.put(USERS_TOKEN, token);
		values.put(USERS_TOKEN_SECRET, tokenSecret);
		try {
			db.insertOrThrow(USERS_TABLE_NAME, null, values);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
	
	public void truncateUsersTable(SQLiteDatabase db) {
		db.execSQL("DELETE FROM " + USERS_TABLE_NAME);
	}

}
