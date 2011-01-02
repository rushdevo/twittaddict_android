package com.rushdevo.twittaddict.constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

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

import com.rushdevo.twittaddict.exceptions.TwitterConnectionFailedException;

public class Twitter {
	public static final CommonsHttpOAuthConsumer CONSUMER = new CommonsHttpOAuthConsumer(
			"cUAeEHVxEhcEMc0h0pHL9g", 
			"JWEPcI88t6Om2XJ25KknVhQFKMsa5j4Pcy9axFN57VI");  
	
	public static final OAuthProvider PROVIDER = new CommonsHttpOAuthProvider(
		    "http://twitter.com/oauth/request_token",
		    "http://twitter.com/oauth/access_token",
		    "http://twitter.com/oauth/authorize");

	public static final HttpClient CLIENT = new DefaultHttpClient();
	
	public static final String BASE_URL = "http://api.twitter.com/1/";
	public static final String FRIEND_URL = BASE_URL + "friends/ids.json";
	
	public static ArrayList<Long> getFriendIds() {
		try {
			HttpGet get = new HttpGet(FRIEND_URL);
			CONSUMER.sign(get);
			final HttpResponse response = CLIENT.execute(get);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				throw new TwitterConnectionFailedException("Failed to get friends list from Twitter: " + statusCode);
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer buffer = new StringBuffer();
			String line;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			reader.close();
			String json = buffer.toString();
			JSONArray array = new JSONArray(json);
			ArrayList<Long> friendIds = new ArrayList<Long>();
			for (int i=0; i < array.length(); i++) {
				friendIds.add(array.getLong(i));
			}
			return friendIds;
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
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
