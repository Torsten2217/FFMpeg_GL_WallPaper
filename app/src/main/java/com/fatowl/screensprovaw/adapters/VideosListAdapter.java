package com.fatowl.screensprovaw.adapters;

import java.io.File;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore.Images.Thumbnails;
import android.provider.MediaStore.Video.Media;
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
import com.fatowl.screensprovaw.service.ScreensProService;
import com.fatowl.screensprovaw.utils.APIWrapper;
import com.fatowl.screensprovaw.utils.ApplicationContext;
import com.fatowl.screensprovaw.utils.DownloadDelegate;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class VideosListAdapter extends BaseAdapter implements
		DownloadDelegate {

	private static String TAG = "SCREENSPRO";
	ScreensProActivity mActivity;
	private Cursor mCursor;
    private ContentResolver cr;
	private boolean doesImageExists = false;
	public ImageLoader imageLoader;
	ArrayList<VideoBean> videos;
	VideoBean videoBean;
    boolean isInterstitialFailedToLoad=false;

	AdRequest adRequest ;
	private InterstitialAd interstitial;

	String globalVideoName;

	int downloadedIndex;

	public VideosListAdapter(ScreensProActivity activity, ArrayList<VideoBean> videos) {
		
		mActivity = activity;
		imageLoader = new ImageLoader(activity);
		
		// Create the interstitial.
	    interstitial = new InterstitialAd(activity);
	    interstitial.setAdUnitId(Constants.INTERSTITIAL_AD_ID);
	 // Create ad request.
	    adRequest = new AdRequest.Builder().build();

	    // Begin loading your interstitial.
	    interstitial.loadAd(adRequest);

		final ProgressDialog progress =  new ProgressDialog(mActivity);
		progress.setMessage("Loading");
		progress.show();

		interstitial.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
				super.onAdLoaded();
				progress.dismiss();
			}

			@Override
			public void onAdFailedToLoad(int errorCode) {
				super.onAdFailedToLoad(errorCode);
				progress.dismiss();
                isInterstitialFailedToLoad=true;
			}
		});

		imageLoader = new ImageLoader(activity);
		this.videos = new ArrayList<VideoBean>();
		cr = mActivity.getContentResolver();
		//Use android.provider.MediaStore.Video.EXTERNAL_CONTENT_URI as
		//per docs MediaStore.Video.Media.html
		//We want DISPLAY_NAME and _ID for now
		//If we ever upgrade we'll probably also want TITLE 
		String[] projection = new String[] {
			Media._ID,
		    Media.DISPLAY_NAME,
		    Media.DATA
		     };
		mCursor = cr.query(Media.EXTERNAL_CONTENT_URI,
				   projection, //with these fields
				   null, //get all videos
				   null, //
				   null
				   );
		int z = 0;
		while (mCursor.moveToNext()){
			//if (z==13){
			if (!mCursor.getString(2).contains("ScreensPro")){
			VideoBean vb = new VideoBean();vb.setvideoabletMid(mCursor.getString(2).replace("/storage/emulated/0", "/sdcard"));
			vb.setname(mCursor.getString(2).replace("/storage/emulated/0", "/sdcard"));
			vb.setthumbMid(mCursor.getString(2).replace("/storage/emulated/0", "/sdcard"));
			//vb.setthum(mCursor.getString(2).replace("/storage/emulated/0", "/sdcard"));
			vb.setvideoTabletHigh(mCursor.getString(2).replace("/storage/emulated/0", "/sdcard"));
			vb.setvideoabletMid(mCursor.getString(2).replace("/storage/emulated/0", "/sdcard"));
			vb.setvideoPhoneHigh(mCursor.getString(2).replace("/storage/emulated/0", "/sdcard"));
			vb.setvideoPhoneMid(mCursor.getString(2).replace("/storage/emulated/0", "/sdcard"));
//			vb.setthumbLow(mCursor.getString(2));
//			vb.setthumbMid(mCursor.getString(2));
//			//vb.setthum(mCursor.getString(2).replace("/storage/emulated/0", "/sdcard"));
//			vb.setvideoTabletHigh(mCursor.getString(2));
//			vb.setvideoabletMid(mCursor.getString(2));
//			vb.setvideoPhoneHigh(mCursor.getString(2));
//			vb.setvideoPhoneMid(mCursor.getString(2));
			
			
			this.videos.add(vb);
			vb = null;
		Log.v("VideosListAdadpter",mCursor.getString(0));
		Log.v("VideosListAdapter",mCursor.getString(1));
		Log.v("VideosListAdapter",mCursor.getString(2));
		}
		z++;
//		 Bitmap bmThumbnail;
//	        bmThumbnail = ThumbnailUtils.createVideoThumbnail(mCursor.getString(2).replace("/emulated/0", "/sdcard"), Thumbnails.MICRO_KIND);
		}
		
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

			if (imageName.length() == 0){
				Log.v("calling Image loader","image name length = 0");
				viewHolder.imageView
						.setImageResource(R.drawable.default_image_background);
			}else {
				Log.v("calling Image loader","With IMage name: "+imageName);
				imageLoader.DisplayImage(imageName, viewHolder.imageView);
				doesImageExists = true;
			}

		} catch (Exception e) {
			Log.v("calling Image loader","Exceptin thrown: "+e.getMessage());
			viewHolder.imageView
					.setImageResource(R.drawable.default_image_background);
		}

		viewHolder.imageView.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (interstitial.isLoaded()) {
				      interstitial.show();
				    }else{
                    loadLiveWallpaper(finalPosition);
                }
				
				interstitial.setAdListener(new AdListener() {
			    	  @Override
			    	  public void onAdClosed() {
			    		  interstitial.loadAd(adRequest);
			    	    // Save app state before going to the ad overlay.
                          loadLiveWallpaper(finalPosition);

			    	  }
						
		    	});
			}
		});

		viewHolder.btnInstall.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
                if (interstitial.isLoaded()) {
                    interstitial.show();
                }else{
                    loadLiveWallpaper(finalPosition);
                }

                interstitial.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        interstitial.loadAd(adRequest);
                        // Save app state before going to the ad overlay.
                        loadLiveWallpaper(finalPosition);

                    }

                });
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

    private void loadLiveWallpaper(int finalPosition){
        try {
            if (doesImageExists) {
                videoBean= videos.get(finalPosition);
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
                            && !videoFile.equals("null")){
                        Log.v("start download video", "downlaod video with file name = " + videoFile);
                        //DownloadVideo(videoBean);
                        new PrepareToShowDownloadFragment().execute();

                    }else{
                        Toast.makeText(mActivity,
                                R.string.video_not_available,
                                Toast.LENGTH_SHORT).show();
                    }
                } else
                    //DownloadVideo(videoBean);
                    new PrepareToShowDownloadFragment().execute();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

	private class ViewHolder {
		ImageView btnInstall;
		ImageView btnFavorite;
		ImageView imageView;
	}

	private boolean isDownloading = false;

	private void DownloadVideo(VideoBean video) {
		try {
			if (isDownloading)
				return;

			SharedPreferences settings = mActivity.getSharedPreferences(
					Constants.SHARED_PREFS_NAME, 0);
			
			//File file = new File(videoName);
			//if (file.exists() && settings.contains(Constants.STORE_WALLPAPERS_ON_SDCARD)) { // if file is downloaded already
			if (true){
				SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean("AlreadyRun", true);
//        				Log.v("setting video name","setting video name with file name = "+video.getname());
				editor.putString(Constants.VIDEO_NAME, videoBean.getname());
//
				boolean fromSettings = settings.getBoolean("FROM_SETTINGS", false);
				if (settings.contains("FROM_SETTINGS"))
					editor.remove("FROM_SETTINGS");

				editor.putBoolean("FULLSCREEN", true);
				editor.commit();
				editor.apply();
				
				
				

					int version = Integer.valueOf(android.os.Build.VERSION.SDK_INT);
					
					if (version >= 16) {
						// try the new Jelly Bean direct android wallpaper chooser first
					    ComponentName component = new ComponentName("com.fatowl.screensprovaw", "com.fatowl.screensprovaw.service.ScreensProService");
					    Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
					    intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, component);

						//mActivity.startActivityForResult(intent, 555555);
					    mActivity.startActivity(intent);
					} 
					else {
					    // try the generic android wallpaper chooser next
						Intent intent = new Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);

						mActivity.startActivity(intent);
						Toast.makeText(mActivity, R.string.user_help_text,
								Toast.LENGTH_LONG).show();
					}

				
//				mActivity.finish();
			} /*else { // if video is not downloaded yet
				globalVideoName = videoName;
				APIWrapper api = new APIWrapper(mActivity,
						VideosListAdapter.this);
				api.downloadVideo(video, videoName);
				isDownloading = true;
			}*/
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
				    Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
				    intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, component);
				    
					mActivity.startActivity(intent);
					component= null;
				} 
				else {
				    // try the generic android wallpaper chooser next
					Intent intent = new Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);

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

	private class PrepareToShowDownloadFragment extends
			AsyncTask<String, Void, String> {

		AlertDialog progress;
		int videoId;

		@Override
		protected void onPreExecute() {
			progress = new AlertDialog.Builder(mActivity).create();
			progress.setTitle("Preparing");
			progress.setMessage("Loading Video...");
			progress.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			//videoId = Integer.parseInt(params[0]);
			synchronized (this) {
				try {
					wait(2 * 1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			//loadSelectedVideo(videoBean);
			DownloadVideo(videoBean);
			progress.dismiss();
			super.onPostExecute(result);
		}

	}


}

