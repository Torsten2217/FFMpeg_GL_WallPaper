package com.fatowl.screensprovaw.main.video;

//Copyright 2011 Uriel Avalos and Frank and Robot Productions

//This software uses libraries from FFmpeg licensed under the LGLv2.1.

//This software uses GLWallpaperService licensed under the Apache v2.

//This file is part of FFvideo Live Wallpaper.

//FFvideo Live Wallpaper is free software: you can redistribute it
//and/or modify it under the terms of the GNU General Public License as
//published by the Free Software Foundation, either version 3 of the
//License, or (at your option) any later version.

//FFvideo Live Wallpaper is distributed in the hope that it will be
//useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
//General Public License for more details.

//You should have received a copy of the GNU General Public License
//along with FFvideo Live Wallpaper.  If not, see <http://www.gnu.org/licenses/>.

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import net.rbgrn.android.glwallpaperservice.*;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Log;

import com.fatowl.screensprovaw.main.Constants;
import com.fatowl.screensprovaw.service.*;

//Original code provided by Robert Green
//http://www.rbgrn.net/content/354-glsurfaceview-adapted-3d-live-wallpapers
public class VideoRenderer implements GLWallpaperService.Renderer {

	ScreensProService.CubeEngine _engine;

	public VideoRenderer(ScreensProService.CubeEngine e) {
		_engine = e;
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		Log.v("VideoRenderer", "onSurfaceCreated");
		_engine.InitializeVideo();
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		Log.v("VideoRenderer", "onSurfaceChanged");
		_engine.UpdateDisplaying(width, height);


	}


	public void onDrawFrame(GL10 gl) {
		Log.v("VideoRenderer", "onDrawFrame");
		NativeCalls.getFrame();
		NativeCalls.drawFrame();
	}

	public void release() {

	}

}