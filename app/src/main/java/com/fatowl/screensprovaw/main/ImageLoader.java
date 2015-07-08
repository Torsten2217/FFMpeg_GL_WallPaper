package com.fatowl.screensprovaw.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore.Images.Thumbnails;
import android.util.Log;
import android.widget.ImageView;

import com.fatowl.screensprovaw.R;

public class ImageLoader {

	MemoryCache memoryCache = new MemoryCache();
	FileCache fileCache;
	private Map<ImageView, String> imageViews = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());
	ExecutorService executorService;
	Context mContext;
	final int stub_id;

	public ImageLoader(Context context) {
		fileCache = new FileCache(context);
		mContext = context;
		executorService = Executors.newFixedThreadPool(5);

		stub_id = R.drawable.default_image_background;
	}

	public void DisplayImage(String url, ImageView imageView) {
		imageViews.put(imageView, url);
		Bitmap bitmap = memoryCache.get(url);
		if (bitmap != null)
			imageView.setImageBitmap(bitmap);
		else {
			Log.v("Quering phoeo","With URL name: "+url);
			queuePhoto(url, imageView);
			imageView.setImageResource(stub_id);
		}
	}

	private void queuePhoto(String url, ImageView imageView) {
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		executorService.submit(new PhotosLoader(p));
	}

	private Bitmap getBitmap(String url) {
		//File f = fileCache.getFile(url);
		File f = new File(url);
		Log.v("getting bitmap","With file path: "+f.getPath());

		// from SD cache
		//Bitmap b = decodeFile(f);
		
		
        Bitmap b =  ThumbnailUtils.createVideoThumbnail(url, Thumbnails.MICRO_KIND);
		
	//	if (b != null)
			return b;

//		// from web
//		try {
//			Bitmap bitmap = null;
//			URL imageUrl = new URL(url);
//			HttpURLConnection conn = (HttpURLConnection) imageUrl
//					.openConnection();
//			conn.setConnectTimeout(30000);
//			conn.setReadTimeout(30000);
//			conn.setInstanceFollowRedirects(true);
//			InputStream is = conn.getInputStream();
//			OutputStream os = new FileOutputStream(f);
//			Utils.CopyStream(is, os);
//			os.close();
//			bitmap = decodeFile(f);
//			return bitmap;
//		} catch (Throwable ex) {
//			ex.printStackTrace();
//			if (ex instanceof OutOfMemoryError)
//				memoryCache.clear();
//			return null;
//		}
	}

	// decodes image and scales it to reduce memory consumption
	private Bitmap decodeFile(File f) {
		Log.v("decoding file","decoding file: "+f.getPath());
		Bitmap bit = null;
		Bitmap bit2 = null;
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			bit = BitmapFactory.decodeStream(new FileInputStream(f), null, null);
			if (bit ==null){
				Log.v("bit is null","bit is null");
			}
			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = 70;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			Log.v("file loaded properly","with name: "+f.getPath());
			 bit2 = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
			Log.v("file not found","this file not found: "+f.getPath());
		}
		if (bit2 == null){
			Log.v("bit2 is null","bit2 is null");
			return bit;
		}
		return bit2;
	}

	// Task for the queue
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;

		public PhotoToLoad(String u, ImageView i) {
			url = u;
			imageView = i;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			Log.v("preparing bitmap","With URL name: "+photoToLoad.url);
			Bitmap bmp = getBitmap(photoToLoad.url);
			memoryCache.put(photoToLoad.url, bmp);
			if (imageViewReused(photoToLoad))
				return;
			BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
			Activity a = (Activity) photoToLoad.imageView.getContext();
			a.runOnUiThread(bd);
		}
	}

	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.url))
			return true;
		return false;
	}

	// Used to display bitmap in the UI thread
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			if (bitmap != null){
				Log.v("bitmap is not null, set the image view","bitmap is not null, set image");
				photoToLoad.imageView.setImageBitmap(bitmap);
			}else{
				Log.v("bitmap is null","bitmap is null, set image");
				photoToLoad.imageView.setImageResource(stub_id);
			}
		}
	}

	public void clearCache() {
		memoryCache.clear();
		fileCache.clear();
	}

}
