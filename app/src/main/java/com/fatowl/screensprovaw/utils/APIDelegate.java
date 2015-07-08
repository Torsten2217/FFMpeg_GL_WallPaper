package com.fatowl.screensprovaw.utils;

import org.json.JSONArray;

public interface APIDelegate {
	public void onSuccessItem(JSONArray items, boolean hasMore);
	public void onError(String message);
}
