package com.rushdevo.twittaddict.constants;

import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

public interface Twitter {
	
	public static final CommonsHttpOAuthConsumer CONSUMER = new CommonsHttpOAuthConsumer(
			"cUAeEHVxEhcEMc0h0pHL9g", 
			"JWEPcI88t6Om2XJ25KknVhQFKMsa5j4Pcy9axFN57VI");  
	
	public static final OAuthProvider PROVIDER = new CommonsHttpOAuthProvider(
		    "http://twitter.com/oauth/request_token",
		    "http://twitter.com/oauth/access_token",
		    "http://twitter.com/oauth/authorize");
	
	public static final HttpClient CLIENT = new DefaultHttpClient();
}
