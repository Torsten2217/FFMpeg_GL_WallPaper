package com.fatowl.screensprovaw.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.fatowl.screensprovaw.main.Constants;

public class Utility {

	public static void initializeConfig(Context mContext) {

		SharedPreferences settings = mContext.getSharedPreferences(
				Constants.SHARED_PREFS_NAME, 0);

		int wallpaperResolution = settings.getInt(
				Constants.WALLPAPER_RESOLUTION,
				Constants.WALLPAPER_RESOLUTION_CANCELED);
		
		((ApplicationContext) mContext.getApplicationContext())
				.setWallpaperResolution(wallpaperResolution);
	}
}
