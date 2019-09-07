package com.example.simplenewsapp.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NewsDataBaseHelper extends SQLiteOpenHelper
{
    private Context mContext;
    // public static final String CREATE_USER = "create table User (id integer primary key autoincrement, name text, password text)";

    ///////////////
    public static final String CREATE_COLLECTION_NEWS = "create table Collection_News ("
            + "id integer primary key autoincrement, news_title text, news_date text, "
            + "news_author text, news_pic_url text, news_url text, news_content text, key_words text, news_type text, iflike integer, ifread integer, UNIQUE (news_title))";
    public static final String CREATE_NEWS_HISTORY = "create table News_History (id integer primary key autoincrement, news_id integer, news_title text)";
    //name 数据库名字 factory 查询时传入的cursor version版本 用于升级
    public static final String CREATE_NEWS_LIKE = "create table News_Like (id integer primary key autoincrement, news_id integer, news_title text)";
    public NewsDataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //String sql = "create table person(id integer primary key autoincrement,name varchar(64),address varchar(64))";
        //db.execSQL(sql); //完成数据库的创建

        //db.execSQL(CREATE_USER);

        db.execSQL(CREATE_COLLECTION_NEWS);
        db.execSQL(CREATE_NEWS_HISTORY);
        db.execSQL(CREATE_NEWS_LIKE);
        //db.execSQL(CREATE_BOOK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 如果存在Book这个表，那么把表删除，一般是根据需要进行操作
        // db.execSQL("drop table if exists Book");
        // 创建新版本的数据库
        //onCreate(db);
    }
}
