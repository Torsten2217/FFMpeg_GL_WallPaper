package com.fatowl.screensprovaw.main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Display;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fatowl.screensprovaw.R;
import com.fatowl.screensprovaw.beans.VideoBean;
import com.fatowl.screensprovaw.database.DBHelper;
import com.fatowl.screensprovaw.utils.APIDelegate;
import com.fatowl.screensprovaw.utils.APIWrapper;
import com.fatowl.screensprovaw.utils.Utility;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class PortraitScreensProActivity extends ScreensProActivity implements
		APIDelegate, OnClickListener {

	private static final String TAG = "SCREENSPRO";
	
	private ViewGroup adViewContainer;

	//private InterstitialAd interstitial;
	
	int menuItem;

	LinearLayout layoutAllCategory;
	RelativeLayout layoutSort;
	TextView textTotal;
	TextView textSort;

	DBHelper dbHelper;

	WallpaperFragment wallpaperFragment;

	APIWrapper api;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Detect original orientation of device
		Display display = getWindowManager().getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);

		int rotation = display.getRotation();
		int height = metrics.heightPixels;
		int width = metrics.widthPixels;

		switch (rotation) {
		case Surface.ROTATION_0:
		case Surface.ROTATION_180:
			Log.i(TAG, "Rotation is: 0 or 180");
			height = metrics.heightPixels;
			width = metrics.widthPixels;
			break;
		case Surface.ROTATION_90:
		case Surface.ROTATION_270:
			Log.i(TAG, "Rotation is: 90 or 270");
			width = metrics.heightPixels;
			height = metrics.widthPixels;
			break;
		default:
			break;
		}

		int mNaturalOrientation;

		if (width > height) {
			Log.i(TAG, "Natural Orientation is landscape");
			mNaturalOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
					| ActivityInfo.SCREEN_ORIENTATION_NOSENSOR;
			isPhone = false;
		} else {
			Log.i(TAG, "Natural Orientation is portrait");
			mNaturalOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
					| ActivityInfo.SCREEN_ORIENTATION_NOSENSOR;
			isPhone = true;
		}

		setRequestedOrientation(mNaturalOrientation);
		if (isPhone)
			setContentView(R.layout.main_layout);
		else
			setContentView(R.layout.main_layout_land);

		// Google Analytic
		EasyTracker.getInstance().setContext(this);

		SharedPreferences settings = getSharedPreferences(
				Constants.SHARED_PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();

		
		AdView adview = new AdView(this);
		adview.setAdUnitId(Constants.BANNER_AD_ID);
		adview.setAdSize(AdSize.BANNER);
//		
//
//		// Initialize view container
		adViewContainer = (ViewGroup) findViewById(R.id.layoutAds);
			adViewContainer.addView(adview);
			
			adview.loadAd(new AdRequest.Builder().build());
			
		// Google Ads
		// LinearLayout adLayout = (LinearLayout) findViewById(R.id.layoutAds);
		// AdView adview = new AdView(this, AdSize.BANNER,
		// Constants.BANNER_AD_ID);
		// AdRequest request = new AdRequest();
		// Set<String> devices = new HashSet<String>();
		// devices.add(Constants.DEVICE_TEST_AD_ID);
		// request.setTestDevices(devices);
		// adLayout.addView(adview);
		// adview.loadAd(request);

		// TODO: temporary solution of resolution issue
		editor.putInt(Constants.WALLPAPER_RESOLUTION,
				Constants.WALLPAPER_RESOLUTION_MID);
		editor.commit();

		// Detect how many time application is run in order to show the rating
		// dialog
		int numRun = settings.getInt("NumRun", 0);

		if (numRun == 3) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					PortraitScreensProActivity.this);
			alertDialogBuilder.setTitle("ScreensPro");
			alertDialogBuilder
					.setMessage("Please help us by ratings/reviewing our app")
					.setCancelable(false)
					.setPositiveButton("Rate now",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									Intent ratingIntent = new Intent(
											Intent.ACTION_VIEW).setData(Uri
													.parse("market://details?id=com.fatowl.screensprovaw")); //For Google Play
													//.parse("http://www.amazon.com/gp/mas/dl/android?p=com.fatowl.screensprovaw"));  //For Amazon Store
													
									startActivity(ratingIntent);
								}
							})
					.setNegativeButton("No Thanks",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();

			numRun++;
			editor.putInt("NumRun", numRun);
			editor.commit();
		} else {
			numRun++;
			editor.putInt("NumRun", numRun);
			editor.commit();
		}

		// Initialize the device resolution settings for the first time
		boolean initWallpaperResolution = settings.getBoolean(
				Constants.INIT_WALLPAPER_RESOLUTION, false);

		if (!initWallpaperResolution) { // first time
			// Delete all old files
			File testVideoDir = new File(
					Environment.getExternalStorageDirectory()
							+ "/ScreensProVAW");
			String[] allFilelist = testVideoDir.list();
			if (allFilelist != null)
				for (int i = 0; i < allFilelist.length; i++) {
					final File currentFile = new File(
							testVideoDir.getAbsolutePath(), allFilelist[i]);
					currentFile.delete();
				}

			if (isPhone) { // for Phone
				if (height >= 1280 && width >= 720)
					editor.putInt(Constants.WALLPAPER_RESOLUTION,
							Constants.WALLPAPER_RESOLUTION_HIGH);
				else if (height >= 800 && width >= 480)
					editor.putInt(Constants.WALLPAPER_RESOLUTION,
							Constants.WALLPAPER_RESOLUTION_MID);
				else
					editor.putInt(Constants.WALLPAPER_RESOLUTION,
							Constants.WALLPAPER_RESOLUTION_LOW);
			} else { // for Tablet
				if (height >= 1200 && width >= 1920)
					editor.putInt(Constants.WALLPAPER_RESOLUTION,
							Constants.WALLPAPER_RESOLUTION_HIGH);
				else
					editor.putInt(Constants.WALLPAPER_RESOLUTION,
							Constants.WALLPAPER_RESOLUTION_MID);
			}
			editor.putBoolean(Constants.INIT_WALLPAPER_RESOLUTION, true);

			// Default settings
			editor.commit();
		}

		// Copy preferences to context variable
		Utility.initializeConfig(this);

		// Set number of query item
		if (isPhone) { // for Phone
			if (height >= 1280 && width >= 720) {
				numColumn = 3;
				queryNumber = 24;
			} else if (height >= 800 && width >= 480) {
				numColumn = 2;
				queryNumber = 8;
			} else {
				numColumn = 2;
				queryNumber = 6;
			}
		} else { // for Tablet
			if (height >= 1200 && width >= 1920) {
				numColumn = 4;
				queryNumber = 42;
			} else {
				numColumn = 4;
				queryNumber = 20;
			}
		}

		// Detect thumb width and height
		Resources r = getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4,
				r.getDisplayMetrics());
		int itemWidth = (int) ((width - (numColumn + 1) * px) / numColumn);
		thumbWidth = (int) (itemWidth - 2 * px);
		thumbHeight = thumbWidth * 84 / 150;

		videos = new ArrayList<VideoBean>();

		// Create folder sdcard/screenspro/testvideo
		createFolder();

		wallpaperFragment = new WallpaperFragment(this);

		// Get favorites from DB
		dbHelper = new DBHelper(this);
		favoriteVideos = dbHelper.getFavouriteVideos();
		storedVideos = dbHelper.getAllStoredVideos();
		for (int i = 0; i < storedVideos.size(); i++)
			if (checkFavorite(storedVideos.get(i).getid()))
				storedVideos.get(i).setIsFavourite(1);
			else
				storedVideos.get(i).setIsFavourite(0);

//		// Get categories
//		api = new APIWrapper(this);
//
//		if (isPhone)
//			api.getVideosSearch("3d", 0, queryNumber, "phone");
//		else
//			api.getVideosSearch("3d", 0, queryNumber, "tablet");
//
//		wallpaperFragment.isLoading = true;

		layoutSort = (RelativeLayout) findViewById(R.id.layoutSort);
		layoutSort.setOnClickListener(this);
		registerForContextMenu(layoutSort);
		layoutAllCategory = (LinearLayout) findViewById(R.id.layoutAllCategory);
		layoutAllCategory.setOnClickListener(this);
		textTotal = (TextView) findViewById(R.id.textTotal);
		textSort = (TextView) findViewById(R.id.textSort);

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.add(R.id.layoutWallpaperFragment, wallpaperFragment);
		ft.commit();

		ImageView menuIcon = (ImageView) findViewById(R.id.topbar_menu);
		menuIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				menuItem = Constants.SETTINGS_MENU;
				PortraitScreensProActivity.this.openContextMenu(v);
			}
		});

		registerForContextMenu(menuIcon);
	}

	@Override
	public void onStart() {
		super.onStart(); // TODO: Exception
		EasyTracker.getInstance().activityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(this);
	}

	private void createFolder() {
		File folder = new File(Environment.getExternalStorageDirectory()
				+ "/ScreensPro3d");
		if (!folder.exists()) {
			folder.mkdir();
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		MenuInflater inflater = getMenuInflater();
		if (menuItem == Constants.SETTINGS_MENU)
			inflater.inflate(R.menu.settings_menu, menu);
		else if (menuItem == Constants.LATEST_MENU)
			inflater.inflate(R.menu.latest_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.settings_menu_settings:
			Intent settingsIntent = new Intent(this, SettingsActivity.class);
			startActivityForResult(settingsIntent, 1234);
			return true;
		case R.id.settings_menu_rate_review:
			final Intent reviewIntent = new Intent(Intent.ACTION_VIEW)
					.setData(Uri
							.parse("market://details?id=com.fatowl.screensprovaw")); //For Google Play
							//.parse("http://www.amazon.com/gp/mas/dl/android?p=com.fatowl.screensprovaw"));  //For Amazon Store
			startActivity(reviewIntent);
			return true;
		case R.id.settings_menu_facebookpage:
			final Intent facebook_intent = new Intent(Intent.ACTION_VIEW)
					.setData(Uri.parse("http://www.facebook.com/screenspro"));
			startActivity(facebook_intent);
			return true;

		case R.id.settings_menu_twitterpage:
			final Intent twitter_intent = new Intent(Intent.ACTION_VIEW)
					.setData(Uri.parse("https://twitter.com/screenspro"));
			startActivity(twitter_intent);
			return true;

		case R.id.settings_menu_googleplusplage:
			final Intent googleplus_intent = new Intent(Intent.ACTION_VIEW)
					.setData(Uri
							.parse("https://plus.google.com/u/0/b/104454458531772431659/104454458531772431659/posts"));
			startActivity(googleplus_intent);
			return true;

		case R.id.settings_menu_aboutscreenspro:
			aboutDialog();
			return true;

		case R.id.latest_menu_your_favourites:
			textSort.setText("Your Favourites");

			wallpaperFragment.wallpaperAdapter.setVideoSource(favoriteVideos);
			wallpaperFragment.wallpaperAdapter.notifyDataSetChanged();

			return true;

//		case R.id.latest_menu_stored:
//			textSort.setText("Stored");
//
//			wallpaperFragment.wallpaperAdapter.setVideoSource(storedVideos);
//			wallpaperFragment.wallpaperAdapter.notifyDataSetChanged();
//
//			return true;

		case R.id.latest_menu_latest:
			textSort.setText("Latest");

			wallpaperFragment.wallpaperAdapter.setVideoSource(videos);
			wallpaperFragment.wallpaperAdapter.notifyDataSetChanged();

			return true;

		default:
			return super.onContextItemSelected(item);
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 1234) {

			if (resultCode == RESULT_OK) {
				boolean result = data.getBooleanExtra("is_stored", true);
				if (!result) {
					storedVideos.clear();
					wallpaperFragment.wallpaperAdapter.notifyDataSetChanged();
				}
			}
		}else if (requestCode == 555555){
			Log.v("I am heee","I am heeeeeeeeeere");
		}
	}

	void aboutDialog() {
		final Dialog aboutDialog = new Dialog(this);
		aboutDialog.getWindow();
		aboutDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		aboutDialog.setContentView(R.layout.about);
		WebView webView = (WebView) aboutDialog.findViewById(R.id.webView);
		webView.loadUrl("file:///android_asset/sp.html");
		webView.setBackgroundColor(0xffffffff);
		Button closeButton = (Button) aboutDialog
				.findViewById(R.id.about_close);
		closeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				aboutDialog.dismiss();
			}
		});
		aboutDialog.show();
	}

	@Override
	public void onSuccessItem(JSONArray items, boolean hasMore) {
		try {
			wallpaperFragment.isLoading = false;
			videos.clear();

			wallpaperFragment.hasMore = hasMore;

			for (int i = 0; i < items.length(); i++) {
				JSONObject itemObject = items.getJSONObject(i);
				int id = itemObject.getInt("id");
				String name = itemObject.getString("name");
				String keywords = itemObject.getString("keywords");
				String dateCreated = itemObject.getString("dateCreated");
				String thumbLow = itemObject.getString("thumbLow");
				String thumbMid = itemObject.getString("thumbLow");
				String videoPhoneLow = itemObject.getString("videoPhoneLow");
				String videoPhoneMid = itemObject.getString("videoPhoneMid");
				String videoPhoneHigh = itemObject.getString("videoPhoneHigh");
				String videoTabletMid = itemObject.getString("videoTabletMid");
				String videoTabletHigh = itemObject
						.getString("videoTabletHigh");

				VideoBean item = new VideoBean(id, name, keywords, dateCreated,
						thumbLow, thumbMid, videoPhoneLow, videoPhoneMid,
						videoPhoneHigh, videoTabletMid, videoTabletHigh);
				if (checkFavorite(id))
					item.setIsFavourite(1);
				else
					item.setIsFavourite(0);
				videos.add(item);
			}

			wallpaperFragment.layoutProgress.setVisibility(LinearLayout.GONE);
			wallpaperFragment.wallpaperAdapter.notifyDataSetChanged();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onError(String message) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				PortraitScreensProActivity.this);

		alertDialogBuilder.setTitle("ScreensPro");

		// set dialog message
		alertDialogBuilder
				.setMessage("You appear to be disconnected. Tap Yes to retry")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								api.invokeAgain();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						wallpaperFragment.layoutProgress
								.setVisibility(ProgressBar.GONE);
					}
				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layoutSort:
			menuItem = Constants.LATEST_MENU;
			PortraitScreensProActivity.this.openContextMenu(v);

			break;

		default:
			break;
		}

	}
}
