package com.fatowl.screensprovaw.main;

import com.fatowl.screensprovaw.R;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

public class VideoPreferences extends FragmentActivity {

	SeekBar videoFrameRateSeekBar;
	EditText videoFrameRateEditText;
	CheckBox scaleToFitCheckBox, scaleToFillCheckBox;
	Button applyButton, cancelButton;

	String scaleMode;
	int frameRate;

	SharedPreferences settings;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_preferences);

		settings = getSharedPreferences(
				Constants.SHARED_PREFS_NAME, 0);

		scaleMode = settings.getString("SCALE_TYPE", "SCALE_TO_FILL");
		frameRate = settings.getInt("FRAME_RATE", 33);

		videoFrameRateSeekBar = (SeekBar) findViewById(R.id.videoFrameRateSeekBar);
		videoFrameRateEditText = (EditText) findViewById(R.id.videoFrameRateEditText);
		scaleToFitCheckBox = (CheckBox) findViewById(R.id.scaleToFitCheckBox);
		scaleToFillCheckBox = (CheckBox) findViewById(R.id.scaleToFillCheckBox);


		if(scaleMode.equals("SCALE_TO_FILL")){
			scaleToFillCheckBox.setChecked(true);
		}else if (scaleMode.equals("SCALE_TO_FIT")){
			scaleToFitCheckBox.setChecked(true);
		}



		videoFrameRateSeekBar
				.setOnSeekBarChangeListener(videoFrameRateSeekBarListener);
		videoFrameRateSeekBar.setMax(44);
		videoFrameRateSeekBar.setProgress(frameRate-16);



		scaleToFitCheckBox
				.setOnCheckedChangeListener(scaleToFitCheckedChangeListener);
		scaleToFillCheckBox
				.setOnCheckedChangeListener(scaleToFillCheckedChangeListener);

		applyButton = (Button) findViewById(R.id.applyButton);
		applyButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int frValue = Integer.parseInt(videoFrameRateEditText.getText()
						.toString());
				if (frValue <= 60 && frValue >= 16) {

					SharedPreferences.Editor editor = settings.edit();
					editor.putInt("FRAME_RATE", frValue);
					
					if (scaleToFitCheckBox.isChecked()){
						editor.putString("SCALE_TYPE", "SCALE_TO_FIT");
					}
					
					if (scaleToFillCheckBox.isChecked()){
						editor.putString("SCALE_TYPE", "SCALE_TO_FILL");
					}
					
					editor.commit();
					
					goToWallPaperIntent();
				} else {
					Toast.makeText(getApplicationContext(), "Frame Rate value is not valid. Please specify valid frame rate value.", Toast.LENGTH_LONG).show();
				}
			}
		});

		cancelButton = (Button) findViewById(R.id.cancelButton);
		cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				SharedPreferences.Editor editor = settings.edit();
//				editor.putBoolean("FROM_SETTINGS", true);
//				editor.putBoolean(Constants.WALLPAPAER_CANCELLED, true);
//				editor.commit();
				goToMainView();
//				goToMainView();
			}
		});

	}
	
	private void goToMainView(){
		Intent intent = new Intent(VideoPreferences.this, InitialActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
//		restart(1000, intent);

	}
	
	private void goToWallPaperIntent(){
		int version = Integer.valueOf(android.os.Build.VERSION.SDK_INT);
		if (version >= 16) {
			// try the new Jelly Bean direct android wallpaper chooser first
		    ComponentName component = new ComponentName("com.fatowl.screensprovaw", "com.fatowl.screensprovaw.service.ScreensProService");
		    Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
		    intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, component);

			//mActivity.startActivityForResult(intent, 555555);
		    startActivity(intent);
//		    restart(1000,intent);
		} 
		else {
		    // try the generic android wallpaper chooser next
			Intent intent = new Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);

//			startActivity(intent);
	        restart(1000,intent);
			Toast.makeText(this, R.string.user_help_text,
					Toast.LENGTH_LONG).show();
		}
	}

	OnCheckedChangeListener scaleToFitCheckedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// TODO Auto-generated method stub
			if (isChecked) {
				scaleToFillCheckBox.setChecked(false);
			}
		}
	};

	OnCheckedChangeListener scaleToFillCheckedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// TODO Auto-generated method stub
			if (isChecked) {
				scaleToFitCheckBox.setChecked(false);
			}
		}
	};

	OnSeekBarChangeListener videoFrameRateSeekBarListener = new OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			int frValue = progress + 16;
			videoFrameRateEditText.setText("" + frValue);
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

	};
	
	public void restart(int delay, Intent intent) {
	    PendingIntent pIntent = PendingIntent.getActivity(this.getBaseContext(), 0, intent, intent.getFlags());
	    AlarmManager manager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
	    manager.set(AlarmManager.RTC, System.currentTimeMillis() + delay, pIntent);
	    finish();
	    System.exit(0);
	}

}
