package com.rushdevo.twittaddict;

import static com.rushdevo.twittaddict.constants.Twitter.CONSUMER;
import static com.rushdevo.twittaddict.constants.Twitter.PROVIDER;
import oauth.signpost.AbstractOAuthConsumer;
import oauth.signpost.OAuth;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.rushdevo.twittaddict.data.TwittaddictData;

public class Twittaddict extends Activity implements Runnable {
	
	private static String CALLBACK_URL = "twittaddict://twitterauth";
	private TwittaddictData db;
	private Game game;
	private boolean initializing = false;
	private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        db = new TwittaddictData(this);
        if (authorized(CONSUMER)) {
        	startGame();
        } else {
        	// Get authorization
        	String authUrl;
			try {
				authUrl = PROVIDER.retrieveRequestToken(CONSUMER, CALLBACK_URL);
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl)));
			} catch (OAuthMessageSignerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OAuthNotAuthorizedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OAuthExpectationFailedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OAuthCommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
        }
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	if (progressDialog != null) progressDialog.dismiss();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	Uri uri = this.getIntent().getData();  
    	if (uri != null && uri.toString().startsWith(CALLBACK_URL)) {  
    	    String verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);  
    	    // this will populate token and token_secret in consumer
    	    try {
				PROVIDER.retrieveAccessToken(CONSUMER, verifier);
			} catch (OAuthMessageSignerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OAuthNotAuthorizedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OAuthExpectationFailedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OAuthCommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			saveTokenAndSecret(CONSUMER);
			startGame();
    	}
    }
    
    @Override
	public void run() {
		// Start the game
    	game = new Game();
    	handler.sendEmptyMessage(0);
	}
    
    public void startGame() {
    	if (!initializing) {
	    	initializing = true;
	    	String title = getResources().getString(R.string.loading_title);
	    	String message = getResources().getString(R.string.loading_message);
	    	progressDialog = ProgressDialog.show(this, title, message, true);
	    	Thread thread = new Thread(this);
	    	thread.start();
    	}
    }
    
    private boolean authorized(AbstractOAuthConsumer consumer) {
    	if (consumer.getToken() != null && consumer.getTokenSecret() != null) {
    		return true;
    	} else {
    		return setupTokenAndSecretFromSavedValues(consumer);
    	}
    }
    
    private boolean setupTokenAndSecretFromSavedValues(AbstractOAuthConsumer consumer) {
    	Cursor cursor = db.getUsers();
    	if (cursor.moveToNext()) {
    		// We have the user saved
    		String token = cursor.getString(1);
    		String tokenSecret = cursor.getString(2);
    		if (token == null || tokenSecret == null) {
    			// We have crappy data somehow
    			return false;
    		} else {
    			// We have the token and secret set aside, use it
    			consumer.setTokenWithSecret(token, tokenSecret);
    			return true;
    		}
    	} else {
    		// No user records
    		return false;
    	}
    }
    
    private void saveTokenAndSecret(AbstractOAuthConsumer consumer) {
    	db.addUser(consumer.getToken(), consumer.getTokenSecret());
    }
    
    private Handler handler = new Handler() {
    	@Override
    	public void handleMessage(Message message) {
    		progressDialog.dismiss();
    		initializing = false;
    	}
    };
}