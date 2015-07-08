package com.fatowl.screensprovaw.database;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;

import com.fatowl.screensprovaw.beans.VideoBean;
import com.fatowl.screensprovaw.main.Constants;

public class DBHelper {
	private Context mContext;
	private CategoryDBAdapter categoryDBAdapter;

	public DBHelper(Context context) {
		mContext = context;
		categoryDBAdapter = new CategoryDBAdapter(mContext);
	}

	public int setVideoAsStoredSD(VideoBean videoBean) {
		categoryDBAdapter.open();
		int result = categoryDBAdapter.createVideo(videoBean, 1);
		categoryDBAdapter.close();
		return result;
	}
	
	public int setVideoAsStoredLocal(VideoBean videoBean) {
		categoryDBAdapter.open();
		int result = categoryDBAdapter.createVideo(videoBean, 0);
		categoryDBAdapter.close();
		return result;
	}

	public int setVideoAsFavourite(VideoBean videoBean) {
		videoBean.setIsFavourite(Constants.FAVOURITE_TRUE);
		categoryDBAdapter.open();
		int result = categoryDBAdapter.createFavourite(videoBean);
		categoryDBAdapter.close();
		return result;
	}

	public boolean removeVideoAsStoredSD() {
		categoryDBAdapter.open();
		boolean result = categoryDBAdapter.deleteVideos(1);
		categoryDBAdapter.close();
		return result;
	}
	
	public boolean removeVideoAsStoredLocal() {
		categoryDBAdapter.open();
		boolean result = categoryDBAdapter.deleteVideos(0);
		categoryDBAdapter.close();
		return result;
	}
	
	public boolean removeVideoAsFavourite(int videoId) {
		categoryDBAdapter.open();
		boolean result = categoryDBAdapter.deleteFavouriteVideo(videoId);
		categoryDBAdapter.close();
		return result;
	}

	private VideoBean getVideoFromCursor(Cursor cursor) {

		int videoId = cursor.getInt(cursor
				.getColumnIndex(CategoryDBAdapter.KEY_VIDEOID));
		String videoName = cursor.getString(cursor
				.getColumnIndex(CategoryDBAdapter.KEY_VIDEONAME));
		String keywords = cursor.getString(cursor
				.getColumnIndex(CategoryDBAdapter.KEY_KEYWORDS));
		String dateCreated = cursor.getString(cursor
				.getColumnIndex(CategoryDBAdapter.KEY_DATECREATED));
		String thumbLow = cursor.getString(cursor
				.getColumnIndex(CategoryDBAdapter.KEY_THUMBS_LOW));
		String thumbMid = cursor.getString(cursor
				.getColumnIndex(CategoryDBAdapter.KEY_THUMBS_MID));
		String videoPhoneLow = cursor.getString(cursor
				.getColumnIndex(CategoryDBAdapter.KEY_VIDEO_PHONE_LOW));
		String videoPhoneMid = cursor.getString(cursor
				.getColumnIndex(CategoryDBAdapter.KEY_VIDEO_PHONE_MID));
		String videoPhoneHigh = cursor.getString(cursor
				.getColumnIndex(CategoryDBAdapter.KEY_VIDEO_PHONE_HIGH));
		String videoTabletMid = cursor.getString(cursor
				.getColumnIndex(CategoryDBAdapter.KEY_VIDEO_TABLET_MID));
		String videoTabletHigh = cursor.getString(cursor
				.getColumnIndex(CategoryDBAdapter.KEY_VIDEO_TABLET_HIGH));

		VideoBean videoForCategoryBean = new VideoBean(videoId, videoName,
				keywords, dateCreated, thumbLow, thumbMid, videoPhoneLow,
				videoPhoneMid, videoPhoneHigh, videoTabletMid, videoTabletHigh);

		return videoForCategoryBean;
	}

	public ArrayList<VideoBean> getAllStoredVideos() {
		categoryDBAdapter.open();
		Cursor cursor = categoryDBAdapter.fetchAllVideos();

		if (cursor != null) {
			ArrayList<VideoBean> videos = new ArrayList<VideoBean>();
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				videos.add(getVideoFromCursor(cursor));
				cursor.moveToNext();
			}
			cursor.close();
			categoryDBAdapter.close();
			return videos;
		}

		categoryDBAdapter.close();
		return null;
	}

	public ArrayList<VideoBean> getFavouriteVideos() {
		categoryDBAdapter.open();
		Cursor cursor = categoryDBAdapter.fetchAllFavouriteVideos();

		if (cursor != null) {
			ArrayList<VideoBean> favorites = new ArrayList<VideoBean>();
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				favorites.add(getVideoFromCursor(cursor));
				cursor.moveToNext();
			}
			cursor.close();
			categoryDBAdapter.close();
			
			for (int i = 0; i < favorites.size(); i++)
				favorites.get(i).setIsFavourite(1);
			
			return favorites;
		}

		categoryDBAdapter.close();
		return null;
	}

}
