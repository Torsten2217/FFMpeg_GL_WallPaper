package com.fatowl.screensprovaw.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fatowl.screensprovaw.R;
import com.fatowl.screensprovaw.beans.VideoBean;
import com.fatowl.screensprovaw.main.Constants;
import com.fatowl.screensprovaw.main.ScreensProActivity;
import com.google.android.gms.ads.*;

public class APIWrapper  {
	private APIDelegate delegate;
	private DownloadDelegate downloadDelegate;

	public static String REQUEST_URL = "https://soap.screenspro.com/JSON/";

	public static String userId = "android_app_v1";
	public static String apiKey = "c73cbe1e-9470-11e1-baca-0017f201ee40";
	
	private InterstitialAd interstitial;
	
	String sysId;

	int queryType = 0;

	private ScreensProActivity activity;

	private String queryString;

	Dialog dialog;
	boolean isCancel;
	String message;

	private int videoId;
	private VideoBean videoBean;
	private String videoName;
	
	
	
	
	public APIWrapper() {

	}

	public APIWrapper(ScreensProActivity activity,
			final DownloadDelegate downloadDelegate) {
		this.activity = activity;
		this.downloadDelegate = downloadDelegate;
		
		// Create the interstitial.
	    interstitial = new InterstitialAd(activity);
	    interstitial.setAdUnitId(Constants.INTERSTITIAL_AD_ID);
	 // Create ad request.
	    AdRequest adRequest = new AdRequest.Builder().build();

	    // Begin loading your interstitial.
	    interstitial.loadAd(adRequest);
	    
	    interstitial.setAdListener(new AdListener() {
	    	  @Override
	    	  public void onAdClosed() {
	    	    // Save app state before going to the ad overlay.
	    		  //new PrepareToShowDownloadFragment().execute();
	    	  }
	    	});

	}

	private class PrepareToShowDownloadFragment extends
			AsyncTask<String, Void, String> {

		AlertDialog progress;
		int videoId;

		@Override
		protected void onPreExecute() {
			progress = new AlertDialog.Builder(activity).create();
			progress.setTitle("Preparing");
			progress.setMessage("Preparing to Download...");
			progress.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			videoId = Integer.parseInt(params[0]);
			synchronized (this) {
				try {
					wait(5 * 1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			//loadSelectedVideo(videoId);
			downloadDelegate.onSuccess(videoBean);
			progress.dismiss();
			super.onPostExecute(result);
		}

	}

	public APIWrapper(APIDelegate delegate) {
		this.delegate = delegate;
	}

	private String getAuthKey() {

		Random rand = new Random();

		int num1 = rand.nextInt(10000);
		int num2 = rand.nextInt(10000);

		sysId = String.format("%s|%d|%d", userId, num1, num2);

		try {
			String authCode = sha1(userId + sysId + apiKey);
			return authCode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	private String sha1(String s) throws UnsupportedEncodingException,
			NoSuchAlgorithmException, InvalidKeyException {

		MessageDigest md;
		md = MessageDigest.getInstance("SHA-1");
		byte[] sha1hash = new byte[40];
		md.update(s.getBytes("iso-8859-1"), 0, s.length());
		sha1hash = md.digest();
		return convertToHex(sha1hash);

	}

	private static String convertToHex(byte[] data) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9))
					buf.append((char) ('0' + halfbyte));
				else
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}

	/**
	 * @param device
	 *            phone tablet
	 */
	public void countAllVideos(String device) {
		String authKey = getAuthKey();
		String queryString = String
				.format("{\"AuthHeader\":{\"userId\":\"%s\",\"sysId\":\"%s\",\"authKey\":\"%s\"},\"CountAll\":{\"device\":\"%s\"}}",
						userId, sysId, authKey, device);
		invoke(queryString);
		queryType = 0;
	}

	public void getVideos(int index, int numResult, String device) {
		String authKey = getAuthKey();
		String queryString = String
				.format("{\"AuthHeader\":{\"userId\":\"%s\",\"sysId\":\"%s\",\"authKey\":\"%s\"},\"GetAllVideos\":{\"index\":%d,\"num_result\":%d,\"device\":\"%s\"}}",
						userId, sysId, authKey, index, numResult, device);
		invoke(queryString);
		queryType = 1;
	}

	public void getFreeVideos(int index, int numResult, String device) {
		String authKey = getAuthKey();
		String queryString = String
				.format("{\"AuthHeader\":{\"userId\":\"%s\",\"sysId\":\"%s\",\"authKey\":\"%s\"},\"Get3dVideos\":{\"index\":%d,\"num_result\":%d,\"device\":\"%s\"}}",
						userId, sysId, authKey, index, numResult, device);
		invoke(queryString);
		queryType = 1;
	}

	public void getCategories(String device) {
		String authKey = getAuthKey();
		queryString = String
				.format("{\"AuthHeader\":{\"userId\":\"%s\",\"sysId\":\"%s\",\"authKey\":\"%s\"},\"GetCategories\":{\"device\":\"%s\"}}",
						userId, sysId, authKey, device);
		invoke(queryString);
		queryType = 2;
	}

	public void getVideosInCategory(int categoryId, int index, int numResult,
			String device) {
		String authKey = getAuthKey();
		queryString = String
				.format("{\"AuthHeader\":{\"userId\":\"%s\",\"sysId\":\"%s\",\"authKey\":\"%s\"},\"GetVideosForCategoryInRange\":{\"categoryId\":%d,\"index\":%d,\"num_result\":%d,\"device\":\"%s\"}}",
						userId, sysId, authKey, categoryId, index, numResult,
						device);
		invoke(queryString);
		queryType = 1;
	}

	public void getVideosSearch(String term, int index, int numResult,
			String device) {
		String authKey = getAuthKey();
		queryString = String
				.format("{\"AuthHeader\":{\"userId\":\"%s\",\"sysId\":\"%s\",\"authKey\":\"%s\"},\"SearchInRange\":{\"term\":\"%s\",\"index\":%d,\"num_result\":%d,\"device\":\"%s\"}}",
						userId, sysId, authKey, term, index, numResult, device);
		invoke(queryString);
		queryType = 1;
	}
	
	
	
	public void downloadVideo(VideoBean video, String videoName) {
		try {
			dialog = new MyDialog(activity);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.download_dialog);

			LayoutInflater inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View main = inflater.inflate(R.layout.download_dialog, null);
			dialog.setContentView(main);
			
			// Google Ads
			LinearLayout linear = (LinearLayout) main.findViewById(R.id.layoutAds);
			AdView adview = new AdView(activity);
			adview.setAdUnitId(Constants.BANNER_AD_ID);
			adview.setAdSize(AdSize.BANNER);
			AdRequest request = new AdRequest.Builder().build();
			//linear.removeAllViews();
			
			adview.loadAd(request);
			linear.addView(adview);
			
			
		

		    
//			// Amazon Ads
//			AdRegistration.setAppKey(activity.getResources().getString(
//					R.string.amazon_app_key));
//			// Initialize ad views
//			amazonAdView = new AdLayout(activity, com.amazon.device.ads.AdSize.SIZE_320x50);
//
//			// Initialize view container
//			adViewContainer = (ViewGroup) activity.findViewById(R.id.layoutAds);
//			amazonAdEnabled = true;
//			adViewContainer
//			.addView(amazonAdView);
//			String locale = activity.getApplicationContext().getResources().getConfiguration().locale.getCountry();
//			if (locale.equals("US")){
//				adLayout.removeAllViews();
//				adLayout.addView(amazonAdView);
//			amazonAdView.loadAd(new com.amazon.device.ads.AdTargetingOptions());
//			}
//			else{
//				adLayout.removeAllViews();
//				adLayout.addView(adview);
//				adview.loadAd(request);
//			}
			
			this.videoId = video.getid();
			this.videoBean = video;
			this.videoName = videoName;
			
			try {
				String authKey = getAuthKey();
				queryString = String
						.format("{\"AuthHeader\":{\"userId\":\"%s\",\"sysId\":\"%s\",\"authKey\":\"%s\"},\"Download\":{\"videoId\":%d,\"videoFile\":\"%s\"}}",
								userId, sysId, authKey, videoId, videoName);
				
				// new DownloadTask().execute(queryString, videoName);
				threadDownload.start();
				new CountDownTask().execute();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void displayInterstitial() {
	    if (interstitial.isLoaded()) {
	      interstitial.show();
	    }
	  }
	
	private void invoke(String param) {
		new APITask().execute(param);
	}

	public void invokeAgain() {
		invoke(queryString);
	}

	private class MyDialog extends Dialog {

		public MyDialog(Context context) {
			super(context);
		}

		@Override
		public void onBackPressed() {

		}
	}

	private class APITask extends AsyncTask<String, Void, String> {

		JSONArray items = null;
		boolean hasMore;
		
		@Override
		protected String doInBackground(String... params) {
			try {
				queryString = params[0];

				HttpClient client = new DefaultHttpClient();
				HttpPost method = new HttpPost(REQUEST_URL);
				method.setEntity(new StringEntity(queryString, "UTF8"));
				HttpResponse response = client.execute(method);
				String result = EntityUtils.toString(response.getEntity(),
						"UTF-8");
				
				Log.v("JSON Result",result);
				JSONObject resp = new JSONObject(result);
				JSONObject headerObject = resp.getJSONObject("header");
				JSONObject statusObject = headerObject.getJSONObject("status");
				if (statusObject.getString("code").equals("OK")
						&& statusObject.getString("description").equals(
								"Success")) {
					if (headerObject.getInt("totalRecs") > 0) {
						if (queryType == 0) {
							hasMore = headerObject.getBoolean("hasMore");
							int totalRecs = headerObject.getInt("totalRecs");
							String resultString = String.format(
									"[{\"totalRecs\":%d}]", totalRecs);
							items = new JSONArray(resultString);
						} else {
							//hasMore = headerObject.getBoolean("hasMore");
							items = resp.getJSONArray("result");
						}
						return "Success";
					} else
						return "No result found";
				} else {
					return statusObject.getString("description");
				}
			} catch (Exception e) {
				e.printStackTrace();
				return e.getMessage();
			}
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				if (result.equals("Success")) {
					if (queryType == 1)
						delegate.onSuccessItem(items, hasMore);
					else if ((queryType == 2) || (queryType == 0))
						delegate.onError(null);
				} else
					delegate.onError(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	boolean isDownloadFinished, isCountDownFinished, downloadResult;
	
	Thread threadDownload = new Thread()
	{
	    @Override
	    public void run() {
	        try {
	        	isCancel = false;
	        	isDownloadFinished = false;

				HttpClient client = new DefaultHttpClient();
				HttpPost method = new HttpPost(REQUEST_URL);
				method.setEntity(new StringEntity(queryString, "UTF8"));
				HttpResponse response = client.execute(method);
				InputStream is = response.getEntity().getContent();

				File exportDir = new File(
						Environment.getExternalStorageDirectory()
								+ "/ScreensPro3d");

				if (!exportDir.exists()) {
					exportDir.mkdirs();
				}

				FileOutputStream f = new FileOutputStream(new File(exportDir,
						videoName));
				byte[] buffer = new byte[8096];
				int len = 0;
				while ((len = is.read(buffer)) > 0) {
					f.write(buffer, 0, len);
					if (isCancel)
						break;
				}
				f.flush();
				f.close();

				if (isCancel) {
					message = "Downloading is cancelled!";
					// delete created file
					File file = new File(exportDir, videoName);
					if (file.exists())
						file.delete();
					downloadResult = false;
				}
				else {
					downloadResult = true;
					Log.v("TAG", "Finish downloading!");
				}
				
				activity.runOnUiThread(new Runnable() { 
		            public void run() 
		            {
		            	isDownloadFinished = true;
		            	
		            	if (isCountDownFinished) {
							//dialog.dismiss();
							if (downloadResult){
								displayInterstitial();
								//downloadDelegate.onSuccess(videoBean);
							}else{	
								downloadDelegate.onError(message);
							}
						} else {
							downloadResult = true;
						} 
		            } 
		        }); 
				
	        } catch (Exception e) {
	        	message = e.getMessage();
	        	downloadResult = false;
	            e.printStackTrace();
	        }
	    }
	};
	
	private class CountDownTask extends AsyncTask<Void, Integer, Void> {

		ProgressBar progressBar;
		TextView textProgress1, textProgress2;

		public CountDownTask() {
			isCancel = false;
		}

		@Override
		protected void onPreExecute() {
			try {
				isCountDownFinished = false;

				progressBar = (ProgressBar) dialog
						.findViewById(R.id.progressBar);
				textProgress1 = (TextView) dialog
						.findViewById(R.id.textProgress1);
				textProgress2 = (TextView) dialog
						.findViewById(R.id.textProgress2);

				progressBar.setMax(100);
				progressBar.setProgress(0);
				textProgress1.setText("0%");
				textProgress2.setText("0");

				Button cancelButton = (Button) dialog
						.findViewById(R.id.buttonCancel);
				cancelButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
						isCancel = true;
					}
				});

				WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
				Window window = dialog.getWindow();
				lp.copyFrom(window.getAttributes());
				//This makes the dialog take up the full width
				lp.width = WindowManager.LayoutParams.MATCH_PARENT;
				lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
				window.setAttributes(lp);
				
				dialog.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
		  

		@Override
		protected Void doInBackground(Void... params) {
			for (int i = 0; i <= 12; i++) {
				publishProgress(i, 12);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (isCancel) {
					downloadResult = false;
					break;
				}
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			try {
				int downloaded = values[0];
				int total = values[1];
				int progress = downloaded * 100 / total;
				progressBar.setProgress(progress);
				textProgress1.setText(String.valueOf(progress) + "%");
				textProgress2.setText(String.valueOf(12 - downloaded)
						+ " seconds remaining!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void onPostExecute(Void result) {
			try {
				isCountDownFinished = true;
				if (isDownloadFinished) {
					
					dialog.dismiss();
					if (downloadResult){
						//downloadDelegate.onSuccess(videoBean);
						displayInterstitial();
					}else{
						downloadDelegate.onError(message);
					}
				} else {
					textProgress2.setText("Nearly done!");
					//displayInterstitial();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
