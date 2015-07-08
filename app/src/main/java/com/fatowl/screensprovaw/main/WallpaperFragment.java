package com.fatowl.screensprovaw.main;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.fatowl.screensprovaw.R;
import com.fatowl.screensprovaw.adapters.VideosListAdapter;
import com.fatowl.screensprovaw.adapters.WallpaperImageAdapter;
import com.fatowl.screensprovaw.beans.VideoBean;
import com.fatowl.screensprovaw.utils.APIDelegate;
import com.fatowl.screensprovaw.utils.APIWrapper;

public class WallpaperFragment extends Fragment implements APIDelegate {

	private static final String TAG = "SCREENSPRO";

	public GridView wallpaperGrid;
	LinearLayout layoutProgress;

	private ScreensProActivity mActivity;

	VideosListAdapter wallpaperAdapter;

	boolean isLoading = true;

	APIWrapper api;

	boolean hasMore = true;

	public WallpaperFragment() {

	}

	public WallpaperFragment(ScreensProActivity activity) {
		mActivity = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		api = new APIWrapper(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.wallpaper_fragment, container,
				false);
		wallpaperGrid = (GridView) view.findViewById(R.id.gridView);
		//wallpaperGrid.setOnScrollListener(this);
		wallpaperGrid.setNumColumns(mActivity.numColumn); // TODO: Exception

		layoutProgress = (LinearLayout) view.findViewById(R.id.layoutProgress);

		wallpaperAdapter = new VideosListAdapter(mActivity, mActivity.videos);
		wallpaperGrid.setAdapter(wallpaperAdapter);

//		//if (isLoading)
//			layoutProgress.setVisibility(LinearLayout.VISIBLE);
//		else
			layoutProgress.setVisibility(LinearLayout.GONE);

		return view;
	}

	@Override
	public void onSuccessItem(JSONArray items, boolean hasMore) {
		try {
			layoutProgress.setVisibility(LinearLayout.GONE);

			this.hasMore = hasMore;

			isLoading = false;
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

				mActivity.videos.add(item);
			}
			wallpaperAdapter.notifyDataSetChanged();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean checkFavorite(int videoId) {
		try {
			boolean isFavorite = false;
			int i;
			for (i = 0; i < mActivity.favoriteVideos.size(); i++) {
				if (mActivity.favoriteVideos.get(i).getid() == videoId) {
					isFavorite = true;
					break;
				}
			}
			return isFavorite;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	@Override
	public void onError(String message) {
		try {
			if (message.equals("No result found")) {
				layoutProgress.setVisibility(ProgressBar.GONE);
				return;
			}

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					WallpaperFragment.this.getActivity());

			alertDialogBuilder.setTitle("ScreensPro");

			// set dialog message
			alertDialogBuilder
					.setMessage(
							"You appear to be disconnected. Tap Yes to retry")
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									api.invokeAgain();
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
									layoutProgress
											.setVisibility(ProgressBar.GONE);
								}
							});

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d(TAG, "WALLPAPER: onAttach");
	}

	// @Override
	// public void onScroll(AbsListView view, int firstVisibleItem,
	// int visibleItemCount, int totalItemCount) {
	// hasMore=true;
	// try {
	// int lastInScreen = firstVisibleItem + visibleItemCount;
	// if ((lastInScreen == totalItemCount) && !isLoading) {
	// if (hasMore) {
	// if (mActivity.videos.size() % mActivity.queryNumber == 0) {
	// if (mActivity.isPhone)
	// api.getVideosSearch("3D",
	// mActivity.videos.size(),
	// mActivity.queryNumber, "phone");
	// else
	// api.getVideosSearch("3D",
	// mActivity.videos.size(),
	// mActivity.queryNumber, "tablet");
	// } else {
	// isLoading = true;
	// return;
	// }
	//
	// layoutProgress.setVisibility(LinearLayout.VISIBLE);
	// }
	// isLoading = true;
	// }
	// } catch (Exception ex) {
	// ex.printStackTrace();
	// }
	// }
	//
	// @Override
	// public void onScrollStateChanged(AbsListView view, int scrollState) {
	//
	// }
}
