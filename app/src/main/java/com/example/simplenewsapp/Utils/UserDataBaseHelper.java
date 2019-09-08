package com.example.simplenewsapp.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDataBaseHelper extends SQLiteOpenHelper {
    private Context mContext;
    public static final String CREATE_USER = "create table User (id integer primary key autoincrement, name text, password text, category text)";

    public UserDataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }//
    public void onCreate(SQLiteDatabase db) {
        //String sql = "create table person(id integer primary key autoincrement,name varchar(64),address varchar(64))";
        //db.execSQL(sql); //完成数据库的创建

        db.execSQL(CREATE_USER);

        //db.execSQL(CREATE_BOOK);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 如果存在Book这个表，那么把表删除，一般是根据需要进行操作
        // db.execSQL("drop table if exists Book");
        // 创建新版本的数据库
        //onCreate(db);
    }
}
