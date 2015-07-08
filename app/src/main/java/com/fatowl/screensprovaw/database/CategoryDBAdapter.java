package com.fatowl.screensprovaw.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.fatowl.screensprovaw.beans.VideoBean;

public class CategoryDBAdapter {
	public static final String KEY_VIDEOID = "videoId";
	public static final String KEY_VIDEONAME = "videoName";
	public static final String KEY_KEYWORDS = "keywords";
	public static final String KEY_DATECREATED = "datecreated";
	public static final String KEY_THUMBS_LOW = "thumbLow";
	public static final String KEY_THUMBS_MID = "thumbMid";
	public static final String KEY_VIDEO_PHONE_LOW = "videoPhoneLow";
	public static final String KEY_VIDEO_PHONE_MID = "videoPhoneMid";
	public static final String KEY_VIDEO_PHONE_HIGH = "videoPhoneHigh";
	public static final String KEY_VIDEO_TABLET_MID = "videoTabletMid";
	public static final String KEY_VIDEO_TABLET_HIGH = "videoTabletHigh";
	public static final String KEY_FAVOURITE_ID = "favId";

	private static final String TAG = "SCREENSPRO";

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	// Store local video
	private static final String VIDEO_LIST_TABLE_CREATE = "create table VideoList (videoId integer primary key,"
			+ "videoName text not null,"
			+ "keywords text ,"
			+ "datecreated text ,"
			+ "thumbLow text not null,"
			+ "thumbMid text not null,"
			+ "videoPhoneLow text,"
			+ "videoPhoneMid text,"
			+ "videoPhoneHigh text,"
			+ "videoTabletMid text," 
			+ "videoTabletHigh text, "
			+ "isSDCard integer not null);";

	// Favorite video
	private static final String FAVOURITE_TABLE_CREATE = "create table Favorites (videoId integer primary key,"
			+ "videoName text not null,"
			+ "keywords text ,"
			+ "datecreated text ,"
			+ "thumbLow text not null,"
			+ "thumbMid text not null,"
			+ "videoPhoneLow text,"
			+ "videoPhoneMid text,"
			+ "videoPhoneHigh text,"
			+ "videoTabletMid text," 
			+ "videoTabletHigh text);";

	private static final String DATABASE_NAME = "ScreensProDB";
	private static final String VIDEO_LIST_TABLE = "VideoList";
	private static final String FAVOURITE_TABLE = "Favorites";
	private static final int DATABASE_VERSION = 2;

	private final Context mCtx;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(VIDEO_LIST_TABLE_CREATE);
			db.execSQL(FAVOURITE_TABLE_CREATE);
			Log.d(TAG, "Table created");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + VIDEO_LIST_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + FAVOURITE_TABLE);
			onCreate(db);
		}
	}

	public CategoryDBAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	public CategoryDBAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDb.close();
		mDbHelper.close();
	}

	public int createVideo(VideoBean videoBean, int isSDCard) { // SDCard: 1
		ContentValues args = new ContentValues();
		args.put(KEY_VIDEOID, videoBean.getid());
		args.put(KEY_VIDEONAME, videoBean.getname());
		args.put(KEY_KEYWORDS, videoBean.getkeywords());
		args.put(KEY_DATECREATED, videoBean.getdateCreated());
		args.put(KEY_THUMBS_LOW, videoBean.getthumbLow());
		args.put(KEY_THUMBS_MID, videoBean.getthumbMid());
		args.put(KEY_VIDEO_PHONE_LOW, videoBean.getvideoPhoneLow());
		args.put(KEY_VIDEO_PHONE_MID, videoBean.getvideoPhoneMid());
		args.put(KEY_VIDEO_PHONE_HIGH, videoBean.getvideoPhoneHigh());
		args.put(KEY_VIDEO_TABLET_MID, videoBean.getvideoTabletMid());
		args.put(KEY_VIDEO_TABLET_HIGH, videoBean.getvideotablethigh());
		args.put("isSDCard", isSDCard);
		return (int) mDb.insert(VIDEO_LIST_TABLE, null, args);
	}

	public Cursor fetchAllVideos() {

		return mDb.query(VIDEO_LIST_TABLE, new String[] { KEY_VIDEOID,
				KEY_VIDEONAME, KEY_KEYWORDS, KEY_DATECREATED, KEY_THUMBS_LOW,
				KEY_THUMBS_MID, KEY_VIDEO_PHONE_LOW, KEY_VIDEO_PHONE_MID,
				KEY_VIDEO_PHONE_HIGH, KEY_VIDEO_TABLET_MID,
				KEY_VIDEO_TABLET_HIGH }, null, null, null, null, null);

	}

	public boolean deleteVideos(int isSDCard) {
		return mDb.delete(VIDEO_LIST_TABLE, "isSDCard =" + isSDCard, null) > 0;
	}

	public Cursor fetchAllFavouriteVideos() {

		return mDb.query(FAVOURITE_TABLE, new String[] { KEY_VIDEOID,
				KEY_VIDEONAME, KEY_KEYWORDS, KEY_DATECREATED, KEY_THUMBS_LOW,
				KEY_THUMBS_MID, KEY_VIDEO_PHONE_LOW, KEY_VIDEO_PHONE_MID,
				KEY_VIDEO_PHONE_HIGH, KEY_VIDEO_TABLET_MID,
				KEY_VIDEO_TABLET_HIGH }, null, null, null, null, null);

	}

	public boolean deleteFavouriteVideo(int videoId) {
		return mDb.delete(FAVOURITE_TABLE, KEY_VIDEOID + " = " + videoId, null) > 0;
	}

	public int createFavourite(VideoBean videoBean) {
		ContentValues args = new ContentValues();
		args.put(KEY_VIDEOID, videoBean.getid());
		args.put(KEY_VIDEONAME, videoBean.getname());
		args.put(KEY_KEYWORDS, videoBean.getkeywords());
		args.put(KEY_DATECREATED, videoBean.getdateCreated());
		args.put(KEY_THUMBS_LOW, videoBean.getthumbLow());
		args.put(KEY_THUMBS_MID, videoBean.getthumbMid());
		args.put(KEY_VIDEO_PHONE_LOW, videoBean.getvideoPhoneLow());
		args.put(KEY_VIDEO_PHONE_MID, videoBean.getvideoPhoneMid());
		args.put(KEY_VIDEO_PHONE_HIGH, videoBean.getvideoPhoneHigh());
		args.put(KEY_VIDEO_TABLET_MID, videoBean.getvideoTabletMid());
		args.put(KEY_VIDEO_TABLET_HIGH, videoBean.getvideotablethigh());
		return (int) mDb.insert(FAVOURITE_TABLE, null, args);
	}
}
