package com.rushdevo.twittaddict;

import java.io.InputStream;
import java.net.URL;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.rushdevo.twittaddict.data.TwittaddictData;

public class GameOverScreen extends Activity implements OnClickListener {
	
	LinearLayout highScoreContainer;
	Button playAgainButton;
	TwittaddictData db;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_over);
		Bundle bundle = getIntent().getExtras();
		// Show current score
		int score = bundle.getInt("score");
		String user = bundle.getString("user");
		String bff = bundle.getString("bff");
		String bffAvatarUrl = bundle.getString("bffAvatar");
		Drawable bffAvatar = null;
		try {
    		URL url = new URL(bffAvatarUrl);
    		InputStream is = (InputStream)url.getContent();
    		bffAvatar = Drawable.createFromStream(is, bff);
    	} catch (Exception e) {
    		// NOOP
    	}
    	
		TextView scoreView = (TextView)findViewById(R.id.score);
		scoreView.setText(Integer.toString(score));
		// Add the high scores list
		highScoreContainer = (LinearLayout)findViewById(R.id.high_score_container);
		db = new TwittaddictData(this);
		Cursor highScores = db.getHighScores();
		int count = 0;
		boolean matched = false;
		TextView nextScoreContainer;
		while (highScores.moveToNext()) {
			count++;
			int nextScore = highScores.getInt(3);
			nextScoreContainer = new TextView(this);
			nextScoreContainer.setTextSize(TypedValue.COMPLEX_UNIT_PX, 15);
			if (score == nextScore && !matched) {
				// Color the first instance of a matched score in blue
				matched = true;
				nextScoreContainer.setTextColor(getResources().getColor(R.color.medium_blue));
			} else {
				nextScoreContainer.setTextColor(getResources().getColor(R.color.grey));
			}
			nextScoreContainer.setText(count+". "+nextScore);
			highScoreContainer.addView(nextScoreContainer);
		}
		highScores.close();
		// Setup the BFF tab
		TextView bffLabelView = (TextView)findViewById(R.id.bff_label);
		ImageView bffAvatarView = (ImageView)findViewById(R.id.bff_avatar);
		TextView bffValueView = (TextView)findViewById(R.id.bff_value);
		// TODO: Hit Twitter and get the most recent info for this user to display "diminish7's BFF is..." and "shanfu!"
		bffAvatarView.setImageDrawable(bffAvatar);
		bffLabelView.setText(user + getString(R.string.bff_label));
		bffValueView.setText(bff + "!");
		// Setup play-again button
		playAgainButton = (Button)findViewById(R.id.play_again_button);
		playAgainButton.setOnClickListener(this);
		// Setup high-score and bff tabs
		TabHost tabHost=(TabHost)findViewById(R.id.tab_host);
        tabHost.setup();

        String highScoreLabel = getString(R.string.high_score_tab);
        TabSpec highScoreSpec = tabHost.newTabSpec(highScoreLabel);
        highScoreSpec.setContent(R.id.high_score_tab);
        // TODO: Style the tabs better
        // How to style the disabled tab?
        // Also move this to a method rather than repeating it for both tabs...
        TextView tabView = new TextView(this);
        tabView.setHeight(20);
        tabView.setBackgroundColor(R.color.white);
        tabView.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM);
        tabView.setText(highScoreLabel);
        highScoreSpec.setIndicator(tabView);
        
        String bffLabel= getString(R.string.bff_tab);
        TabSpec bffSpec = tabHost.newTabSpec(bffLabel);
        bffSpec.setContent(R.id.bff_tab);
        tabView = new TextView(this);
        tabView.setHeight(20);
        tabView.setBackgroundColor(R.color.white);
        tabView.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM);
        tabView.setText(bffLabel);
        bffSpec.setIndicator(tabView);
        
        tabHost.addTab(highScoreSpec);
        tabHost.addTab(bffSpec);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.play_again_button:
			finish();
			break;
		}
	}
}
