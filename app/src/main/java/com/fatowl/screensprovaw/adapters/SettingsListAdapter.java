package com.fatowl.screensprovaw.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.fatowl.screensprovaw.R;
import com.fatowl.screensprovaw.main.Constants;
import com.fatowl.screensprovaw.utils.Utility;

public class SettingsListAdapter extends BaseAdapter {

	Context mContext;
	String[] titleArray;
	String[] subScriptArray;
	SharedPreferences mPrefs;

	public SettingsListAdapter(Context context) {
		mContext = context;
		titleArray = mContext.getResources().getStringArray(
				R.array.settings_title_array);
		subScriptArray = mContext.getResources().getStringArray(
				R.array.settings_subscript_array);
		mPrefs = mContext.getSharedPreferences(Constants.SHARED_PREFS_NAME, 0);
	}

	public int getCount() {
		return titleArray.length;
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	@Override
	public int getItemViewType(int position) {
		if ((position == 0) || (position == 2)) {
			return 0;
		}
		// else if (position == 3) {
		// return 1;
		// }
		else {
			return 2;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 3;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		final ViewHolder viewHolder;
		final int finalposition = position;

		if (convertView == null) {
			LayoutInflater li = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mContext.getAssets();
			mContext.getResources();

			viewHolder = new ViewHolder();

			if ((position == 0) || (position == 2)) {
				convertView = li.inflate(R.layout.settings_text_item_header,
						parent, false);
				viewHolder.titleTextView = (TextView) convertView
						.findViewById(R.id.settings_text_item_textView);

			}
			// else if (position == 3) {
			// convertView = li.inflate(R.layout.setting_list_item, parent,
			// false);
			// viewHolder.titleTextView = (TextView) convertView
			// .findViewById(R.id.setting_list_item_titleText);
			// viewHolder.subscriptTextView = (TextView) convertView
			// .findViewById(R.id.setting_list_item_subscripttextView);
			// }
			else {
				convertView = li.inflate(R.layout.setting_list_item_checked,
						parent, false);
				viewHolder.titleTextView = (TextView) convertView
						.findViewById(R.id.setting_list_item_checked_titleText);
				viewHolder.subscriptTextView = (TextView) convertView
						.findViewById(R.id.setting_list_item_checked_subscripttextView);
				viewHolder.checkBox = (CheckBox) convertView
						.findViewById(R.id.setting_list_item_checked_checkBox);
			}

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if ((position == 0) || (position == 2)) {
			viewHolder.titleTextView.setText(titleArray[position]);

		}
		// else if (position == 3) {
		// viewHolder.titleTextView.setText(titleArray[position]);
		// viewHolder.subscriptTextView.setText(subScriptArray[position]);
		// }
		else {
			viewHolder.titleTextView.setText(titleArray[position]);
			viewHolder.subscriptTextView.setText(subScriptArray[position]);

			SharedPreferences settings = mContext.getSharedPreferences(
					Constants.SHARED_PREFS_NAME, 0);

			if (position == 1) {
				if (settings.contains(Constants.SKIP_FRAMES)) {
					viewHolder.checkBox
							.setButtonDrawable(R.drawable.checkboxiconchecked);
					viewHolder.checked = true;
				} else {
					viewHolder.checkBox
							.setButtonDrawable(R.drawable.checkboxicon);
					viewHolder.checked = false;
				}
			}

			if (position == 3) // / looking at sdcard here
			{
				if (settings.contains(Constants.STORE_WALLPAPERS_ON_SDCARD)) {
					viewHolder.checkBox
							.setButtonDrawable(R.drawable.checkboxiconchecked);
					viewHolder.checked = true;
				} else {
					viewHolder.checkBox
							.setButtonDrawable(R.drawable.checkboxicon);
					viewHolder.checked = false;
				}
			}

			convertView.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					SharedPreferences settings = mContext.getSharedPreferences(
							Constants.SHARED_PREFS_NAME, 0);
					SharedPreferences.Editor editor = settings.edit();

					switch (finalposition) {
					case 1:
						boolean skipFrames = settings.getBoolean(
								Constants.SKIP_FRAMES, false);
						editor.putBoolean(Constants.SKIP_FRAMES, !skipFrames);
						editor.commit();
						break;

					case 3:
						if (settings
								.contains(Constants.STORE_WALLPAPERS_ON_SDCARD))
							editor.remove(Constants.STORE_WALLPAPERS_ON_SDCARD);
						else
							editor.putBoolean(
									Constants.STORE_WALLPAPERS_ON_SDCARD, true);
						editor.commit();
						break;
					}

					// Change checkbox ticked/unticked
					if (viewHolder.checked == false) {
						viewHolder.checkBox
								.setButtonDrawable(R.drawable.checkboxiconchecked);
						viewHolder.checked = true;
					} else {
						viewHolder.checkBox
								.setButtonDrawable(R.drawable.checkboxicon);
						viewHolder.checked = false;
					}

					Utility.initializeConfig(mContext);
				}

			});

			viewHolder.checkBox.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					SharedPreferences settings = mContext.getSharedPreferences(
							Constants.SHARED_PREFS_NAME, 0);
					SharedPreferences.Editor editor = settings.edit();

					switch (finalposition) {
					case 1:
						boolean skipFrames = settings.getBoolean(
								Constants.SKIP_FRAMES, false);
						editor.putBoolean(Constants.SKIP_FRAMES, !skipFrames);
						editor.commit();
						break;

					case 3:
						if (settings
								.contains(Constants.STORE_WALLPAPERS_ON_SDCARD))
							editor.remove(Constants.STORE_WALLPAPERS_ON_SDCARD);
						else
							editor.putBoolean(
									Constants.STORE_WALLPAPERS_ON_SDCARD, true);
						editor.commit();
						break;

					}

					// Change checkbox ticked/unticked
					if (viewHolder.checked == false) {
						viewHolder.checkBox
								.setButtonDrawable(R.drawable.checkboxiconchecked);
						viewHolder.checked = true;
					} else {
						viewHolder.checkBox
								.setButtonDrawable(R.drawable.checkboxicon);
						viewHolder.checked = false;
					}

					Utility.initializeConfig(mContext);
				}
			});

		}

		return convertView;
	}

	private class ViewHolder {
		boolean checked;
		TextView titleTextView;
		TextView subscriptTextView;
		CheckBox checkBox;
	}

}
