package com.example.simplenewsapp.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SearchDataBaseHelper extends SQLiteOpenHelper {
    private static String name = "search_record.db";
    private static Integer version = 1;


    public SearchDataBaseHelper(Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //打开数据库，建立了一个叫records的表，里面只有一列name来存储历史记录：
        db.execSQL("create table records(id integer primary key autoincrement,name varchar(200))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
