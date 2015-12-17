package com.android.tengfenxiang.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	final private static String DBname = "tengfenxiang";
	final private static int mDbVersion = 1;
	private Context context;

	public DBHelper(Context context, CursorFactory factory) {
		super(context, DBname, factory, mDbVersion);
		this.context = context;
	}

	public DBHelper(Context context) {
		this(context, null);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String creatTaskTable = "create table if not exists task(id integer, title text, content text, price float, thumbnails text, shareUrl text, beginTime text, endTime text, status integer, retweetCount integer, limitRetweetCount integer)";
		String createArticleTable = "create table if not exists article(id integer, title text, content text, thumbnails text, shareUrl text, likeCount integer, viewCount integer, type integer)";
		String createChannelTable = "create table if not exists channel(_id integer primary key autoincrement,  id integer , name text , orderId integer , selected selected)";
		db.execSQL(createArticleTable);
		db.execSQL(creatTaskTable);
		db.execSQL(createChannelTable);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS task");
		db.execSQL("DROP TABLE IF EXISTS article");
		db.execSQL("DROP TABLE IF EXISTS channel");
		onCreate(db);
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
}