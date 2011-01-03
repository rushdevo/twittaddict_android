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

import com.rushdevo.twittaddict.exceptions.TwitterConnectionFailedException;
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
	
	private static final String BASE_URL = "http://api.twitter.com/1/";
	private static final String FRIEND_URL = BASE_URL + "friends/ids.json";
	private static final String CREDENTIALS_URL = BASE_URL + "account/verify_credentials.json";
	private static final String USER_LOOKUP_URL = BASE_URL + "users/lookup.json";
	private static final String FRIENDS_TIMELINE_URL = BASE_URL + "statuses/friends_timeline.json?count=200";
	
	public static ArrayList<Long> getFriendIds() {
		try {
			JSONArray array = getJSONArray(FRIEND_URL, "friend list");
			if (array != null) {
				ArrayList<Long> friendIds = new ArrayList<Long>();
				for (int i=0; i < array.length(); i++) {
					friendIds.add(array.getLong(i));
				}
				return friendIds;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static TwitterUser getUser() {
		JSONObject hash = getJSONObject(CREDENTIALS_URL, "credentials");
		if (hash != null) return new TwitterUser(hash);
		else return null;
	}
	
	public static ArrayList<TwitterUser> getUsers(ArrayList<Long> ids) {
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
		JSONArray userJSON = getJSONArray(url, "users");
		ArrayList<TwitterUser> users = new ArrayList<TwitterUser>();
		if (userJSON != null) {
			try {
				for (int i=0; i<userJSON.length(); i++) {
					users.add(new TwitterUser(userJSON.getJSONObject(i)));
				}
				return users;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return users;
	}
	
	public static ArrayList<TwitterStatus> getStatuses() {
		try {
			JSONArray array = getJSONArray(FRIENDS_TIMELINE_URL, "home timeline");
			if (array != null) {
				ArrayList<TwitterStatus> statuses = new ArrayList<TwitterStatus>();
				for (int i=0; i<array.length(); i++) {
					statuses.add(new TwitterStatus(array.getJSONObject(i)));
				}
				return statuses;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private static JSONArray getJSONArray(String url, String resourceName) {
		String json = getJSONString(url, resourceName);
		if (json == null) {
			return null;
		} else {
			try {
				return new JSONArray(json);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}
	
	private static JSONObject getJSONObject(String url, String resourceName) {
		String json = getJSONString(url, resourceName);
		if (json == null) {
			return null;
		} else {
			try {
				return new JSONObject(json);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}
	
	private static String getJSONString(String url, String resourceName) {
		try {
			HttpGet get = new HttpGet(url);
			CONSUMER.sign(get);
			final HttpResponse response = CLIENT.execute(get);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				throw new TwitterConnectionFailedException("Failed to get " + resourceName + " from Twitter: " + statusCode);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TwitterConnectionFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
