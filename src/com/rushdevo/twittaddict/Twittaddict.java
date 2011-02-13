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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rushdevo.twittaddict.data.TwittaddictData;

public class Twittaddict extends Activity implements Runnable {
	
	private static String CALLBACK_URL = "twittaddict://twitterauth";
	private static final int INIT_MESSAGE = 0;
	private static final int TIMER_MESSAGE = 1;
	private static final int GAME_LENGTH = 60;
	private TwittaddictData db;
	private Game game;
	private boolean initializing = false;
	private ProgressDialog progressDialog;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        db = new TwittaddictData(this);
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
				saveTokenAndSecret(CONSUMER);
				startGame();
			} catch (OAuthMessageSignerException e) {
				errorAlert(getString(R.string.oauth_failure));
				debug(e);
			} catch (OAuthNotAuthorizedException e) {
				errorAlert(getString(R.string.oauth_failure));
				debug(e);
			} catch (OAuthExpectationFailedException e) {
				errorAlert(getString(R.string.oauth_failure));
				debug(e);
			} catch (OAuthCommunicationException e) {
				errorAlert(getString(R.string.oauth_failure));
				debug(e);
			}
    	} else {
    		authorizeOrStartGame();
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.exit:
    		finish();
    		return true;
    	}
    	return false;
    }
    
    @Override
	public void run() {
		// Start the game
    	game = new Game(this);
    	game.start();
    	handler.sendEmptyMessage(INIT_MESSAGE);
		// Deal with the timer
		long timeStamp = System.currentTimeMillis() / 1000;
		int seconds = GAME_LENGTH;
		while (seconds > 0) {
			int diff = GAME_LENGTH - (int)((System.currentTimeMillis() / 1000) - timeStamp);
			if (diff < 0) diff = 0;
			if (diff != seconds) {
				seconds = diff;
				Message msg = new Message();
        		msg.what = TIMER_MESSAGE;
        		msg.arg1 = seconds;
        		handler.sendMessage(msg);
			}
		}
		game.finish();
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
    
    public void showNextQuestion() {
    	if (game.isInPlay()) {
    		Question question = game.getNextQuestion();
    		if (question instanceof TweetQuestion) {
    			displayTweetQuestion((TweetQuestion)question);
    		} else if (question instanceof UserQuestion) {
    			displayUserQuestion((UserQuestion)question);
    		} // Else - TODO: what do we do...
    	} // Else - TODO: what do we do...
    }
    
    private void displayTweetQuestion(TweetQuestion question) {
    	// Set the tweet text
    	TextView tweetView = (TextView)findViewById(R.id.tweet_container);
    	tweetView.setText(question.getStatus().getText());
    	// TODO: Display Possible User Avatars
    	
    	// Display the container
    	LinearLayout tweetLayout = (LinearLayout)findViewById(R.id.tweet_question_container);
		LinearLayout userLayout = (LinearLayout)findViewById(R.id.user_question_container);
    	tweetLayout.setVisibility(LinearLayout.VISIBLE);
		userLayout.setVisibility(LinearLayout.GONE);
    }
    
    private void displayUserQuestion(UserQuestion question) {
    	// Set the three tweet texts
    	TextView tweetView = (TextView)findViewById(R.id.tweet1_container);
    	tweetView.setText(question.getStatus1().getText());
    	tweetView = (TextView)findViewById(R.id.tweet2_container);
    	tweetView.setText(question.getStatus2().getText());
    	tweetView = (TextView)findViewById(R.id.tweet3_container);
    	tweetView.setText(question.getStatus3().getText());
    	// TODO: Display User Avatar
    	
    	// Display the container
    	LinearLayout tweetLayout = (LinearLayout)findViewById(R.id.tweet_question_container);
		LinearLayout userLayout = (LinearLayout)findViewById(R.id.user_question_container);
		userLayout.setVisibility(LinearLayout.VISIBLE);
		tweetLayout.setVisibility(LinearLayout.GONE);
    }
    private void authorizeOrStartGame() {
    	if (authorized(CONSUMER)) {
        	if (game == null || !game.getSuccess()) startGame();
        } else {
        	// Get authorization
        	String authUrl;
			try {
				authUrl = PROVIDER.retrieveRequestToken(CONSUMER, CALLBACK_URL);
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl)));
			} catch (OAuthMessageSignerException e) {
				errorAlert(getString(R.string.oauth_failure));
				debug(e);
			} catch (OAuthNotAuthorizedException e) {
				errorAlert(getString(R.string.oauth_failure));
				debug(e);
			} catch (OAuthExpectationFailedException e) {
				errorAlert(getString(R.string.oauth_failure));
				debug(e);
			} catch (OAuthCommunicationException e) {
				errorAlert(getString(R.string.oauth_failure));
				debug(e);
			}  
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
    		switch(message.what) {
    		case INIT_MESSAGE:
    			progressDialog.dismiss();
        		initializing = false;
        		if (!game.getSuccess()) {
        			// Something went wrong during game initialization
        			errorAlert(game.getFormattedMessage());
        		} else {
        			showNextQuestion();
        		}
        		break;
    		case TIMER_MESSAGE:
    			TextView timerLabel = (TextView)findViewById(R.id.timer);
    			timerLabel.setText(Integer.toString(message.arg1));
    			break;
    		}
    	}
    };
    
    private void errorAlert(String message) {
    	AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    	alertDialog.setTitle(getString(R.string.error_alert_title));
    	alertDialog.setMessage(message);
    	alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
    	   public void onClick(DialogInterface dialog, int which) { }
    	});
    	alertDialog.show();
    }
    
    private void debug(String message) {
    	Log.d("Twittaddict", message);
    }
    
    private void debug(Exception e) {
    	debug(e.getClass().toString() + " error: " + e.getMessage());
    }
}