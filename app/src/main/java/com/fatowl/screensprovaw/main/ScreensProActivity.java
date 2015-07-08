package com.fatowl.screensprovaw.main;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View.OnClickListener;

import com.fatowl.screensprovaw.beans.VideoBean;
import com.fatowl.screensprovaw.utils.APIDelegate;
import com.google.analytics.tracking.android.EasyTracker;

public abstract class ScreensProActivity extends FragmentActivity implements
		APIDelegate, OnClickListener {

	public boolean isPhone;

	public ArrayList<VideoBean> videos;
	public ArrayList<VideoBean> favoriteVideos;
	public ArrayList<VideoBean> storedVideos;
	
	public int queryNumber;

	public int thumbWidth, thumbHeight, numColumn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 555555){
			Log.v("I am heeeeeeeeeeeeereeeeee","I am heeeeeeeeeere");
		}
	}


	protected boolean checkFavorite(int videoId) {
		boolean isFavorite = false;
		int i;
		for (i = 0; i < favoriteVideos.size(); i++) {
			if (favoriteVideos.get(i).getid() == videoId) {
				isFavorite = true;
				break;
			}
		}
		return isFavorite;
	}
}
