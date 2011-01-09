package com.rushdevo.twittaddict.constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.rushdevo.twittaddict.R;
import com.rushdevo.twittaddict.exceptions.TwitterException;
import com.rushdevo.twittaddict.twitter.TwitterStatus;
import com.rushdevo.twittaddict.twitter.TwitterUser;

public class Twitter {
	public static final CommonsHttpOAuthConsumer CONSUMER = new CommonsHttpOAuthConsumer(
			"cUAeEHVxEhcEMc0h0pHL9g", 
			"JWEPcI88t6Om2XJ25KknVhQFKMsa5j4Pcy9axFN57VI");  
	
	public static final OAuthProvider PROVIDER = new CommonsHttpOAuthProvider(
		    "http://twitter.com/oauth/request_token",
		    "http://twitter.com/oauth/access_token",
		    "http://twitter.com/oauth/authorize");

	public static final HttpClient CLIENT = new DefaultHttpClient();
	
	// URLS
	private static final String BASE_URL = "http://api.twitter.com/1/";
	private static final String FRIEND_URL = BASE_URL + "friends/ids.json";
	private static final String CREDENTIALS_URL = BASE_URL + "account/verify_credentials.json";
	private static final String USER_LOOKUP_URL = BASE_URL + "users/lookup.json";
	private static final String FRIENDS_TIMELINE_URL = BASE_URL + "statuses/friends_timeline.json?count=200";
	
	public static ArrayList<Long> getFriendIds(Context ctx) throws TwitterException {
		try {
			JSONArray array = getJSONArray(FRIEND_URL, "friend list", ctx);
			ArrayList<Long> friendIds = new ArrayList<Long>();
			for (int i=0; i < array.length(); i++) {
				friendIds.add(array.getLong(i));
			}
			return friendIds;
		} catch (JSONException e) {
			debug(e);
			throw new TwitterException(ctx.getString(R.string.bad_friend_response_failure));
		} catch (TwitterException e) {
			debug(e);
			throw new TwitterException(ctx.getString(R.string.bad_friend_response_failure));
		}
	}
	
	public static TwitterUser getUser(Context ctx) throws TwitterException {
		JSONObject hash;
		try {
			hash = getJSONObject(CREDENTIALS_URL, "credentials", ctx);
			return new TwitterUser(hash);
		} catch (TwitterException e) {
			debug(e);
			throw new TwitterException(ctx.getString(R.string.bad_user_response_failure));
		}
	}
	
	public static ArrayList<TwitterUser> getUsers(ArrayList<Long> ids, Context ctx) throws TwitterException {
		if (ids.isEmpty()) return new ArrayList<TwitterUser>();
		Iterator<Long> iter = ids.iterator();
		StringBuffer idStr = new StringBuffer();
		int count = 0;
		while (iter.hasNext() && count < 100) {
			iter.next();
			idStr.append(iter.next());
			count++;
			if (iter.hasNext() && count < 100) idStr.append(",");
		}
		String url = USER_LOOKUP_URL + "?user_id=" + idStr.toString();
		JSONArray userJSON;
		try {
			userJSON = getJSONArray(url, "users", ctx);
			ArrayList<TwitterUser> users = new ArrayList<TwitterUser>();
			for (int i=0; i<userJSON.length(); i++) {
				users.add(new TwitterUser(userJSON.getJSONObject(i)));
			}
			return users;
		} catch (TwitterException e) {
			debug(e);
			throw new TwitterException(ctx.getString(R.string.bad_user_response_failure));
		} catch (JSONException e) {
			debug(e);
			throw new TwitterException(ctx.getString(R.string.bad_user_response_failure));
		}
	}
	
	public static ArrayList<TwitterStatus> getStatuses(Context ctx) throws TwitterException {
		try {
			JSONArray array = getJSONArray(FRIENDS_TIMELINE_URL, "home timeline", ctx);
			ArrayList<TwitterStatus> statuses = new ArrayList<TwitterStatus>();
			for (int i=0; i<array.length(); i++) {
				statuses.add(new TwitterStatus(array.getJSONObject(i)));
			}
			return statuses;
		} catch (JSONException e) {
			debug(e);
			throw new TwitterException(ctx.getString(R.string.bad_status_response_failure));
		} catch (TwitterException e) {
			debug(e);
			throw new TwitterException(ctx.getString(R.string.bad_status_response_failure));
		}
	}
	
	private static JSONArray getJSONArray(String url, String resourceName, Context ctx) throws TwitterException {
		try {
			String json = getJSONString(url, resourceName, ctx);
			return new JSONArray(json);
		} catch (JSONException e) {
			debug(e);
			throw new TwitterException(ctx.getString(R.string.bad_response_failure));
		} catch (TwitterException e) {
			debug(e);
			throw new TwitterException(ctx.getString(R.string.bad_response_failure));
		}
	}
	
	private static JSONObject getJSONObject(String url, String resourceName, Context ctx) throws TwitterException {
		try {
			String json = getJSONString(url, resourceName, ctx);
			return new JSONObject(json);
		} catch (JSONException e) {
			debug(e);
			throw new TwitterException(ctx.getString(R.string.bad_response_failure));
		} catch (TwitterException e) {
			debug(e);
			throw new TwitterException(ctx.getString(R.string.bad_response_failure));
		}
	}
	
	private static String getJSONString(String url, String resourceName, Context ctx) throws TwitterException {
		try {
			HttpGet get = new HttpGet(url);
			CONSUMER.sign(get);
			final HttpResponse response = CLIENT.execute(get);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				debug("Got status " + statusCode + " back from url '" + url + "' for resource '" + resourceName + "'.");
				throw new TwitterException("Failed to get " + resourceName + " from Twitter: " + statusCode);
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer buffer = new StringBuffer();
			String line;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			reader.close();
			return buffer.toString();
		} catch (OAuthMessageSignerException e) {
			debug(e);
			throw new TwitterException(ctx.getString(R.string.oauth_failure));
		} catch (OAuthExpectationFailedException e) {
			debug(e);
			throw new TwitterException(ctx.getString(R.string.oauth_failure));
		} catch (OAuthCommunicationException e) {
			debug(e);
			throw new TwitterException(ctx.getString(R.string.oauth_failure));
		} catch (ClientProtocolException e) {
			debug(e);
			throw new TwitterException(ctx.getString(R.string.communication_failure));
		} catch (IOException e) {
			debug(e);
			throw new TwitterException(ctx.getString(R.string.communication_failure));
		}
	}
	
	private static void debug(String message) {
    	Log.d("Twitter", message);
    }
    
    private static void debug(Exception e) {
    	debug(e.getClass().toString() + " error: " + e.getMessage());
    }
}
