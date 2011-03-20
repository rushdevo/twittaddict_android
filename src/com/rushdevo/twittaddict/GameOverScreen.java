package com.rushdevo.twittaddict;

import com.rushdevo.twittaddict.data.TwittaddictData;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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
			nextScoreContainer.setTextSize(TypedValue.COMPLEX_UNIT_PX, 16);
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
		playAgainButton = (Button)findViewById(R.id.play_again_button);
		playAgainButton.setOnClickListener(this);
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
