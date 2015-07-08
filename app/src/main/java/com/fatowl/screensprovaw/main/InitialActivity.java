package com.fatowl.screensprovaw.main;

import java.io.IOException;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;

import com.fatowl.screensprovaw.service.ScreensProService;
import com.google.analytics.tracking.android.EasyTracker;

public class InitialActivity extends FragmentActivity {

	boolean fromSettings;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle bundle = getIntent().getExtras();

		if (bundle != null) {
			if (bundle.containsKey("android.service.wallpaper.PREVIEW_MODE")) {
				SharedPreferences settings = getSharedPreferences(
						Constants.SHARED_PREFS_NAME, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean("FROM_SETTINGS", true);
				editor.commit();
				fromSettings=true;
			}else{
				finish();
			}
		}


		// Detect original orientation of device
		Display display = getWindowManager().getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);

		ComponentName component = new ComponentName("com.fatowl.screensprovaw", "com.fatowl.screensprovaw.service.ScreensProService");
		Intent intentz = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
		intentz.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, component);
		stopService(intentz);

		int rotation = display.getRotation();
		int height = metrics.heightPixels;
		int width = metrics.widthPixels;

		switch (rotation) {
		case Surface.ROTATION_0:
		case Surface.ROTATION_180:
			height = metrics.heightPixels;
			width = metrics.widthPixels;
			break;
		case Surface.ROTATION_90:
		case Surface.ROTATION_270:
			width = metrics.heightPixels;
			height = metrics.widthPixels;
			break;
		default:
			break;
		}

		if (!fromSettings){
		if (width > height) {
			Log.i("TAG", "Natural Orientation is landscape");
			Intent intent = new Intent(InitialActivity.this, LandscapeScreensProActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
					| Intent.FLAG_ACTIVITY_NEW_TASK);

			startActivity(intent);

		} else {
			Log.i("TAG", "Natural Orientation is portrait");
			Intent intent = new Intent(InitialActivity.this, PortraitScreensProActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
					| Intent.FLAG_ACTIVITY_NEW_TASK);

//			restart(1000,intent);
			startActivity(intent);
//			finish();
		}
		}else{
			Intent intent = new Intent(InitialActivity.this, VideoPreferences.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
					| Intent.FLAG_ACTIVITY_NEW_TASK);
//			restart(1000,intent);
			startActivity(intent);
//			finish();
		}
		
	}

	public void restart(int delay, Intent intent) {
	    PendingIntent pIntent = PendingIntent.getActivity(this.getBaseContext(), 0, intent, intent.getFlags());
	    AlarmManager manager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
	    manager.set(AlarmManager.RTC, System.currentTimeMillis() + delay, pIntent);
	    finish();
	    System.exit(0);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance().activityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(this);
		
	}
	
}
