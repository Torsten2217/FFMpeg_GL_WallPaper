package com.fatowl.screensprovaw.adapters;

import java.io.File;
import java.util.ArrayList;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.fatowl.screensprovaw.R;
import com.fatowl.screensprovaw.beans.VideoBean;
import com.fatowl.screensprovaw.database.DBHelper;
import com.fatowl.screensprovaw.main.Constants;
import com.fatowl.screensprovaw.main.ImageLoader;
import com.fatowl.screensprovaw.main.ScreensProActivity;
import com.fatowl.screensprovaw.utils.APIWrapper;
import com.fatowl.screensprovaw.utils.ApplicationContext;
import com.fatowl.screensprovaw.utils.DownloadDelegate;

public class WallpaperImageAdapter extends BaseAdapter implements
		DownloadDelegate {

	private static String TAG = "SCREENSPRO";
	ScreensProActivity mActivity;

	private boolean doesImageExists = false;
	public ImageLoader imageLoader;
	ArrayList<VideoBean> videos;

	String globalVideoName;

	int downloadedIndex;

	public WallpaperImageAdapter(ScreensProActivity activity,
			ArrayList<VideoBean> videos) {
		mActivity = activity;
		imageLoader = new ImageLoader(activity);
		this.videos = videos;
	}

	public void setVideoSource(ArrayList<VideoBean> videos) {
		this.videos = videos;
	}

	@Override
	public int getCount() {
		return videos.size();
	}

	@Override
	public Object getItem(int position) {
		return videos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return videos.get(position).getid();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder;
		String imageName = "";
		ApplicationContext applicationContext = (ApplicationContext) mActivity
				.getApplicationContext();
		doesImageExists = false;
		final int finalPosition = position;

		if (convertView == null) {
			LayoutInflater li = (LayoutInflater) mActivity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = li.inflate(R.layout.wallpaper_item, null);

			viewHolder = new ViewHolder();

			viewHolder.btnInstall = (ImageView) convertView
					.findViewById(R.id.imageInstall);
			viewHolder.btnFavorite = (ImageView) convertView
					.findViewById(R.id.imageFavorite);
			viewHolder.imageView = (ImageView) convertView
					.findViewById(R.id.imageView);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.imageView.getLayoutParams().height = mActivity.thumbHeight;
		viewHolder.imageView.getLayoutParams().width = mActivity.thumbWidth;

		int wallpaperResolution = applicationContext.getWallpaperResolution();
		if (wallpaperResolution == Constants.WALLPAPER_RESOLUTION_LOW)
			imageName = videos.get(position).getthumbLow();
		else
			imageName = videos.get(position).getthumbMid();

		try {

			if (imageName.length() == 0)
				viewHolder.imageView
						.setImageResource(R.drawable.default_image_background);
			else {
				imageLoader.DisplayImage(imageName, viewHolder.imageView);
				doesImageExists = true;
			}

		} catch (Exception e) {
			viewHolder.imageView
					.setImageResource(R.drawable.default_image_background);
		}

		viewHolder.imageView.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				try {
					if (doesImageExists) {
						VideoBean video = videos.get(finalPosition);
						downloadedIndex = finalPosition;

						String videoFile = null;

						//TODO: temporary solution of resolution
						if (mActivity.isPhone)
							videoFile = videos.get(finalPosition)
									.getvideoPhoneMid();
						else
							videoFile = videos.get(finalPosition)
									.getvideoTabletMid();
						
//						switch (wallpaperResolution) {
//						case Constants.WALLPAPER_RESOLUTION_LOW:
//							if (mActivity.isPhone)
//								videoFile = videos.get(finalPosition)
//										.getvideoPhoneLow();
//							else
//								videoFile = videos.get(finalPosition)
//										.getvideoTabletMid();
//							break;
//
//						case Constants.WALLPAPER_RESOLUTION_MID:
//							if (mActivity.isPhone)
//								videoFile = videos.get(finalPosition)
//										.getvideoPhoneMid();
//							else
//								videoFile = videos.get(finalPosition)
//										.getvideoTabletMid();
//
//							break;
//						case Constants.WALLPAPER_RESOLUTION_HIGH:
//							if (mActivity.isPhone)
//								videoFile = videos.get(finalPosition)
//										.getvideoPhoneHigh();
//							else
//								videoFile = videos.get(finalPosition)
//										.getvideotablethigh();
//							break;
//						}

						if (videoFile.equals(null) || videoFile.equals("null")) {
							if (mActivity.isPhone) {
								videoFile = videos.get(finalPosition)
										.getvideoPhoneHigh();
								if (videoFile.equals(null)
										|| videoFile.equals("null"))
									videoFile = videos.get(finalPosition)
											.getvideoPhoneMid();
								if (videoFile.equals(null)
										|| videoFile.equals("null"))
									videoFile = videos.get(finalPosition)
											.getvideoPhoneLow();
							} else {
								videoFile = videos.get(finalPosition)
										.getvideotablethigh();
								if (videoFile.equals(null)
										|| videoFile.equals("null"))
									videoFile = videos.get(finalPosition)
											.getvideoTabletMid();
							}

							if (!videoFile.equals(null)
									&& !videoFile.equals("null"))
								DownloadVideo(video, videoFile);
							else
								Toast.makeText(mActivity,
										R.string.video_not_available,
										Toast.LENGTH_SHORT).show();
						} else
							DownloadVideo(video, videoFile);
					}

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

		viewHolder.btnInstall.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				try {
					if (doesImageExists) {
						VideoBean video = videos.get(finalPosition);

						ApplicationContext applicationContext = (ApplicationContext) mActivity
								.getApplicationContext();

						int wallpaperResolution = applicationContext
								.getWallpaperResolution();
						String videoFile = videos.get(finalPosition)
								.getvideoTabletMid();

						if (mActivity.isPhone)
							videoFile = videos.get(finalPosition)
									.getvideoPhoneMid();
						else
							videoFile = videos.get(finalPosition)
									.getvideoTabletMid();

						if (videoFile.equals(null) || videoFile.equals("null")) {

							if (mActivity.isPhone) {
								videoFile = videos.get(finalPosition)
										.getvideoPhoneHigh();
								if (videoFile.equals(null)
										|| videoFile.equals("null"))
									videoFile = videos.get(finalPosition)
											.getvideoPhoneMid();
								if (videoFile.equals(null)
										|| videoFile.equals("null"))
									videoFile = videos.get(finalPosition)
											.getvideoPhoneLow();
							} else {
								videoFile = videos.get(finalPosition)
										.getvideotablethigh();
								if (videoFile.equals(null)
										|| videoFile.equals("null"))
									videoFile = videos.get(finalPosition)
											.getvideoTabletMid();
							}

							if (!videoFile.equals(null)
									&& !videoFile.equals("null"))
								DownloadVideo(video, videoFile);
							else
								Toast.makeText(mActivity,
										R.string.video_not_available,
										Toast.LENGTH_SHORT).show();
						} else
							DownloadVideo(video, videoFile);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		// Check favorite or not
		final ViewHolder finalViewHolder = viewHolder;
		if (videos.get(finalPosition).getIsFavourite() == 1) // is favorite
			viewHolder.btnFavorite
					.setImageResource(R.drawable.favourite_button_selected);
		else
			// is not favorite
			viewHolder.btnFavorite
					.setImageResource(R.drawable.favourite_button);

		viewHolder.btnFavorite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					int videoID = videos.get(finalPosition).getid();
					DBHelper videoHelper = new DBHelper(mActivity);

					if (videos.get(finalPosition).getIsFavourite() == 1) {
						videos.get(finalPosition).setIsFavourite(0);
						videoHelper.removeVideoAsFavourite(videoID);
						finalViewHolder.btnFavorite
								.setImageResource(R.drawable.favourite_button);
						int i = 0;

						for (i = 0; i < mActivity.videos.size(); i++)
							if (videoID == mActivity.videos.get(i).getid())
								break;
						if (i < mActivity.videos.size())
							mActivity.videos.get(i).setIsFavourite(0);

						for (i = 0; i < mActivity.storedVideos.size(); i++)
							if (videoID == mActivity.storedVideos.get(i)
									.getid())
								break;
						if (i < mActivity.storedVideos.size())
							mActivity.storedVideos.get(i).setIsFavourite(0);

						for (i = 0; i < mActivity.favoriteVideos.size(); i++)
							if (videoID == mActivity.favoriteVideos.get(i)
									.getid())
								break;
						if (i < mActivity.favoriteVideos.size())
							mActivity.favoriteVideos.remove(i);

						notifyDataSetChanged();
					} else {
						videos.get(finalPosition).setIsFavourite(1);
						videoHelper.setVideoAsFavourite(videos
								.get(finalPosition));
						finalViewHolder.btnFavorite
								.setImageResource(R.drawable.favourite_button_selected);

						mActivity.favoriteVideos.add(videos.get(finalPosition));

						int i = 0;

						for (i = 0; i < mActivity.videos.size(); i++)
							if (videoID == mActivity.videos.get(i).getid())
								break;
						if (i < mActivity.videos.size())
							mActivity.videos.get(i).setIsFavourite(1);

						for (i = 0; i < mActivity.storedVideos.size(); i++)
							if (videoID == mActivity.storedVideos.get(i)
									.getid())
								break;
						if (i < mActivity.storedVideos.size())
							mActivity.storedVideos.get(i).setIsFavourite(1);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

		});

		return convertView;
	}

	private class ViewHolder {
		ImageView btnInstall;
		ImageView btnFavorite;
		ImageView imageView;
	}

	private boolean isDownloading = false;

	private void DownloadVideo(VideoBean video, String videoName) {
		try {
			if (isDownloading)
				return;

			SharedPreferences settings = mActivity.getSharedPreferences(
					Constants.SHARED_PREFS_NAME, 0);
			
			File file = new File(Environment.getExternalStorageDirectory()
					+ "/ScreensPro3d/" + videoName);
			if (file.exists() && settings.contains(Constants.STORE_WALLPAPERS_ON_SDCARD)) { // if file is downloaded already
				SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean("AlreadyRun", true);
				editor.putString(Constants.VIDEO_NAME, globalVideoName);
				
				boolean fromSettings = settings.getBoolean("FROM_SETTINGS", false);
				if (settings.contains("FROM_SETTINGS"))
					editor.remove("FROM_SETTINGS");
				
				editor.putBoolean("FULLSCREEN", true);
				editor.commit();
				
				if (!fromSettings) {
					int version = Integer.valueOf(android.os.Build.VERSION.SDK_INT);
					
					if (version >= 16) {
						// try the new Jelly Bean direct android wallpaper chooser first
					    ComponentName component = new ComponentName("com.fatowl.screensprovaw", "com.fatowl.screensprovaw.service.ScreensProService");
						Intent intent = new Intent(Intent.ACTION_SET_WALLPAPER);
					    intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, component);
						intent.setFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME
								| Intent.FLAG_ACTIVITY_NEW_TASK);
						mActivity.startActivity(intent);
					} 
					else {
					    // try the generic android wallpaper chooser next
						Intent intent = new Intent(Intent.ACTION_SET_WALLPAPER);
						intent.setFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME
								| Intent.FLAG_ACTIVITY_NEW_TASK);
						mActivity.startActivity(intent);
						Toast.makeText(mActivity, R.string.user_help_text,
								Toast.LENGTH_LONG).show();
					}
				}
				
				mActivity.finish();
			} else { // if video is not downloaded yet
				globalVideoName = videoName;
				APIWrapper api = new APIWrapper(mActivity,
						WallpaperImageAdapter.this);
				api.downloadVideo(video, videoName);
				isDownloading = true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void onSuccess(VideoBean video) {
		try {
			isDownloading = false;
			SharedPreferences settings = mActivity.getSharedPreferences(
					Constants.SHARED_PREFS_NAME, 0);

			if (settings.contains(Constants.STORE_WALLPAPERS_ON_SDCARD)) {
				DBHelper videoHelper = new DBHelper(mActivity);
				videoHelper.setVideoAsStoredSD(video);
				mActivity.storedVideos.add(video);
			}

			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("AlreadyRun", true);
			editor.putString(Constants.VIDEO_NAME, globalVideoName);
			
			boolean fromSettings = settings.getBoolean("FROM_SETTINGS", false);
			if (settings.contains("FROM_SETTINGS"))
				editor.remove("FROM_SETTINGS");
			
			editor.putBoolean("FULLSCREEN", true);
			editor.commit();
			
			if (!fromSettings) {
				int version = Integer.valueOf(android.os.Build.VERSION.SDK_INT);
				
				if (version >= 16) {
					// try the new Jelly Bean direct android wallpaper chooser first
				    ComponentName component = new ComponentName("com.fatowl.screensprovaw", "com.fatowl.screensprovaw.service.ScreensProService");
				    Intent intent = new Intent(Intent.ACTION_SET_WALLPAPER);
				    intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, component);
					intent.setFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME
							| Intent.FLAG_ACTIVITY_NEW_TASK);
					mActivity.startActivity(intent);
				} 
				else {
				    // try the generic android wallpaper chooser next
					Intent intent = new Intent(Intent.ACTION_SET_WALLPAPER);
			        intent.setFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME
							| Intent.FLAG_ACTIVITY_NEW_TASK);
					mActivity.startActivity(intent);
					Toast.makeText(mActivity, R.string.user_help_text,
							Toast.LENGTH_LONG).show();
				}
			}
			
			mActivity.finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onError(String message) {
		isDownloading = false;
		if (mActivity != null)
			Toast.makeText(mActivity, message, Toast.LENGTH_LONG).show();
	}
}

