package com.fatowl.screensprovaw.utils;

import android.app.Application;

public class ApplicationContext extends Application {

	int wallpaperResolution;

	public int getWallpaperResolution() {
		return wallpaperResolution;
	}

	public void setWallpaperResolution(int wallpaperResolution) {
		this.wallpaperResolution = wallpaperResolution;
	}

}
