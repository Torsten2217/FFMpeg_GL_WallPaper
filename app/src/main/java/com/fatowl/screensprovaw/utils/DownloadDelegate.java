package com.fatowl.screensprovaw.utils;

import com.fatowl.screensprovaw.beans.VideoBean;

public interface DownloadDelegate {
	public void onSuccess(VideoBean videoBean);
	public void onError(String message);
}
