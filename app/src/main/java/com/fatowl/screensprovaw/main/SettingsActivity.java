package com.fatowl.screensprovaw.main;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.fatowl.screensprovaw.R;
import com.fatowl.screensprovaw.adapters.SettingsListAdapter;
import com.fatowl.screensprovaw.database.DBHelper;
import com.fatowl.screensprovaw.utils.Utility;

public class SettingsActivity extends Activity {
	SharedPreferences settings;
	SharedPreferences.Editor editor;

	Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_view);

		mContext = this;

		settings = SettingsActivity.this.getSharedPreferences(
				Constants.SHARED_PREFS_NAME, 0);

		ListView wallpaperList = (ListView) findViewById(R.id.settings_view_listView);

		SettingsListAdapter wallpapersettingListAdapter = new SettingsListAdapter(
				this);
		wallpaperList.setAdapter(wallpapersettingListAdapter);

		wallpaperList.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				switch (position) {
				case Constants.WALLPAPER_RESOLUTION_INDEX:
					SettingsActivity.this.openContextMenu(view);
				}
			}

		});

		registerForContextMenu(wallpaperList);

	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
	}

	protected void onCustomContextMenuItemSelected(String[] items, int which) {
		Toast.makeText(this, items[which], Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		super.onCreateContextMenu(menu, v, menuInfo);

		menu.setHeaderTitle("Wallpaper Resolution");
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.wallpaper_resolution_menu, menu);
		switch (settings.getInt(Constants.WALLPAPER_RESOLUTION,
				Constants.WALLPAPER_RESOLUTION_HIGH)) {
		case Constants.WALLPAPER_RESOLUTION_HIGH:
			menu.findItem(R.id.wallpaper_resolution_menu_highest).setChecked(
					true);
			break;

		case Constants.WALLPAPER_RESOLUTION_MID:
			menu.findItem(R.id.wallpaper_resolution_menu_mid).setChecked(true);
			break;

		case Constants.WALLPAPER_RESOLUTION_LOW:
			menu.findItem(R.id.wallpaper_resolution_menu_lowest).setChecked(
					true);
			break;

		}

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		SharedPreferences.Editor editor = settings.edit();

		switch (item.getItemId()) {

		case R.id.wallpaper_resolution_menu_highest:
			if (item.isChecked())
				item.setChecked(false);
			else {
				item.setChecked(true);
				editor.putInt(Constants.WALLPAPER_RESOLUTION,
						Constants.WALLPAPER_RESOLUTION_HIGH);
				editor.commit();
				Utility.initializeConfig(this);
			}
			return false;

		case R.id.wallpaper_resolution_menu_mid:

			if (item.isChecked())
				item.setChecked(false);
			else {
				item.setChecked(true);

				editor.putInt(Constants.WALLPAPER_RESOLUTION,
						Constants.WALLPAPER_RESOLUTION_MID);
				editor.commit();
				Utility.initializeConfig(this);
			}
			return false;
		case R.id.wallpaper_resolution_menu_lowest:
			if (item.isChecked())
				item.setChecked(false);
			else {
				item.setChecked(true);

				editor.putInt(Constants.WALLPAPER_RESOLUTION,
						Constants.WALLPAPER_RESOLUTION_LOW);
				editor.commit();
				Utility.initializeConfig(this);

			}

			return false;

		case R.id.swiping_index_menu_smoother_animation:

			editor.putBoolean(Constants.SWIPING_LAG_INDEX_TEXT, false);
			editor.commit();
			Utility.initializeConfig(this);

			return false;

		case R.id.swiping_index_menu_smoother_video:

			editor.putBoolean(Constants.SWIPING_LAG_INDEX_TEXT, true);
			editor.commit();
			Utility.initializeConfig(this);

			return false;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onStop() {
		super.onStop();

		DBHelper dbHelper = new DBHelper(mContext);
		SharedPreferences settings = mContext.getSharedPreferences(
				Constants.SHARED_PREFS_NAME, 0);

		if (!settings.contains(Constants.STORE_WALLPAPERS_ON_SDCARD)) {
			dbHelper.removeVideoAsStoredSD();

			Intent returnIntent = new Intent();
			returnIntent.putExtra("is_stored", false);
			setResult(RESULT_OK, returnIntent);

			File testVideoDir = new File(
					Environment.getExternalStorageDirectory()
							+ "/ScreensPro3d");
			String[] allFilelist = testVideoDir.list();
			if (allFilelist != null)
				for (int i = 0; i < allFilelist.length; i++) {
					final File currentFile = new File(
							testVideoDir.getAbsolutePath(), allFilelist[i]);
					currentFile.delete();
				}
		}
	}
}
