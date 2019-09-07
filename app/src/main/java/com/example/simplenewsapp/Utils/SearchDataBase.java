package com.example.simplenewsapp.Utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class SearchDataBase {
    private Context context;
    private SearchDataBaseHelper helper;
    private SQLiteDatabase db;

    public SearchDataBase(Context context, String user_name) {
        this.context = context;
        init(user_name);
    }
    private void init(String user_name) {
        helper = new SearchDataBaseHelper(context, user_name);
        queryData("");
    }

    public List<String> queryData(String tempName) {
        List<String> data = new ArrayList<>();
        Cursor cursor = helper.getReadableDatabase().rawQuery(
                "select id as _id,name from records where name like '%" + tempName + "%' order by id desc ", null);

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            data.add(name);
        }
        cursor.close();
        return data;
    }

    public boolean hasData(String tempName) {

        Cursor cursor = helper.getReadableDatabase().rawQuery(
                "select id as _id,name from records where name =?", new String[]{tempName});
        return cursor.moveToNext();
    }

    public void insertData(String tempName) {
        db = helper.getWritableDatabase();
        db.execSQL("insert into records(name) values('" + tempName + "')");
        db.close();
    }


    public int delete(String name) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int delete = db.delete("records", " name=?", new String[]{name});
        db.close();
        return delete;
    }

    public void deleteData() {
        db = helper.getWritableDatabase();
        db.execSQL("delete from records");
        db.close();
    }
}
