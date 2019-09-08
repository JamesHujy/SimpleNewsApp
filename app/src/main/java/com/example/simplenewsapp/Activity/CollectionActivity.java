package com.example.simplenewsapp.Activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.simplenewsapp.Adapter.NewsAdapter;
import com.example.simplenewsapp.Utils.News;
import com.example.simplenewsapp.R;

import com.example.simplenewsapp.Utils.NewsDataBaseHelper;
import com.example.simplenewsapp.Utils.ShareInfoUtil;
import com.example.simplenewsapp.Utils.ThemeManager;

import java.util.ArrayList;
import java.util.List;

public class CollectionActivity extends Activity implements NewsAdapter.CallBack
{
    private List<News> newsList = new ArrayList<>();

    private ListView listView;
    private String type;

    private NewsAdapter newsAdapter;

    private ImageView exitButton;
    private TextView textView;

    private int newsCount = 8;

    private LinearLayout collection_layout;
    private LinearLayout headline;


    NewsDataBaseHelper dbHelper;
    String user_name;

    private void initView()
    {
        listView = findViewById(R.id.listview_collection);
        user_name = (String) ShareInfoUtil.getParam(this, ShareInfoUtil.LOGIN_DATA, "");
        dbHelper = new NewsDataBaseHelper(this, "User_"+user_name+".db", null, 1);

        collection_layout = findViewById(R.id.collection_layout);
        headline = findViewById(R.id.headline);

        collection_layout.setBackgroundColor(getResources().getColor(ThemeManager.getCurrentThemeRes(this, R.color.backgroundColor)));
        headline.setBackgroundColor(getResources().getColor(ThemeManager.getCurrentThemeRes(this, R.color.backgroundColor)));
        textView = findViewById(R.id.head_title);
        textView.setTextColor(getResources().getColor(ThemeManager.getCurrentThemeRes(this, R.color.textColor)));
        exitButton = findViewById(R.id.exit_collection);

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        initView();
        initAdapter();
        if (type.equals("collect"))
        {
            initCollectNews();
            textView.setText("我的收藏");
        }
        else
        {
            initHistoryNews();
            textView.setText("历史记录");

        }

        listView.setAdapter(newsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Intent intent = new Intent(getBaseContext(), ShowNewsActivity.class);
                intent.putExtra("title", newsList.get(i - listView.getHeaderViewsCount()).get_title());
                intent.putExtra("date", newsList.get(i - listView.getHeaderViewsCount()).get_date());
                intent.putExtra("author", newsList.get(i - listView.getHeaderViewsCount()).get_author());
                intent.putExtra("content", newsList.get(i - listView.getHeaderViewsCount()).get_content());
                intent.putExtra("pic_url", newsList.get(i - listView.getHeaderViewsCount()).get_picurl());
                if (type.equals("collect"))
                {
                    intent.putExtra("source_activity","collect");
                }
                else
                {
                    intent.putExtra("source_activity","history");
                }

                startActivity(intent);
                finish();
            }
        });


    }
    News getNewsFromCollectSQL(int id, SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("select id, news_title, news_date, news_author, news_pic_url, news_content, " +
                "news_pic_url from Collection_News where id = " + id, null);
        cursor.moveToFirst();
        if (!cursor.isNull(cursor.getColumnIndex("id"))) {

            String title_ = cursor.getString(cursor.getColumnIndex("news_title"));
            System.out.println("in get News from SQL. title is "+title_);
            String date_ = cursor.getString(cursor.getColumnIndex("news_date"));
            String content_ = cursor.getString(cursor.getColumnIndex("news_content"));
            String author_ = cursor.getString(cursor.getColumnIndex("news_author"));
            String url_ = cursor.getString(cursor.getColumnIndex("news_pic_url"));
            System.out.println("test cursor author is " + author_);
            System.out.println("test cursor url is " + url_);

            //BitmapHelper bitmapHelper = new BitmapHelper(this.getContext());

            //Bitmap bitmap = bitmapHelper.getBitmapFromUrl(url_);
            News news = new News(title_, content_, date_, author_, url_);
            return news;
        }
        return new News("", "", "", "", "");
        //return -1;
    }

    News getNewsFromHistorySQL(int id, SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("select id, news_title, news_date, news_author, news_pic_url, news_content, " +
                "news_pic_url from Collection_News where id = " + id, null);
        cursor.moveToFirst();
        if (!cursor.isNull(cursor.getColumnIndex("id"))) {

            String title_ = cursor.getString(cursor.getColumnIndex("news_title"));
            //System.out.println("in get News from SQL. title is "+title_);
            String date_ = cursor.getString(cursor.getColumnIndex("news_date"));
            String content_ = cursor.getString(cursor.getColumnIndex("news_content"));
            String author_ = cursor.getString(cursor.getColumnIndex("news_author"));
            String url_ = cursor.getString(cursor.getColumnIndex("news_pic_url"));
            //System.out.println("test cursor author is " + author_);
            //System.out.println("test cursor url is " + url_);

            //BitmapHelper bitmapHelper = new BitmapHelper(this.getContext());

            //Bitmap bitmap = bitmapHelper.getBitmapFromUrl(url_);
            News news = new News(title_, content_, date_, author_, url_);
            return news;
        }
        return new News("", "", "", "", "");
        //return -1;
    }

    private void initCollectNews()
    {
        newsList.clear();


        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select id, news_title from News_Like", null);
        while (cursor.moveToNext()) {
            if (!cursor.isNull(cursor.getColumnIndex("id"))) {
                String news_title = cursor.getString(cursor.getColumnIndex("news_title"));
                int news_id = getIDFromSQL(news_title, db);
                News news = getNewsFromCollectSQL(news_id, db);

                newsList.add(news);
                //return news;
            }
        }
        cursor.close();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                newsAdapter.notifyDataSetChanged();
            }
        });
        db.close();
    }

    private void initHistoryNews()
    {
        newsList.clear();


        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select id, news_title from News_History", null);
        while (cursor.moveToNext()) {
            if (!cursor.isNull(cursor.getColumnIndex("id"))) {
                String news_title = cursor.getString(cursor.getColumnIndex("news_title"));
                int news_id = getIDFromSQL(news_title, db);
                News news = getNewsFromHistorySQL(news_id, db);

                newsList.add(news);
                //return news;
            }
        }
        cursor.close();
        db.close();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                newsAdapter.notifyDataSetChanged();
            }
        });
    }



    private void initAdapter()
    {
        newsAdapter = new NewsAdapter(this, R.layout.news_item,newsList,this);
    }
    int getIDFromSQL(String title, SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("select id, news_title from Collection_News", null);
        while (cursor.moveToNext()) {
            String title_ = cursor.getString(cursor.getColumnIndex("news_title"));
            if (title.equals(title_)) {
                return cursor.getInt(cursor.getColumnIndex("id"));
            }
        }
        return -1;
    }
    @Override
    public void click(View view)
    {

        int index = Integer.parseInt(view.getTag().toString());
        String news_title = newsList.get(index).get_title();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Integer news_id = getIDFromSQL(news_title, db);
        db.delete("News_Like", "news_title = ?", new String[]{news_title});

        ContentValues values = new ContentValues();
        values.put("iflike", 0);
        db.update("Collection_News", values, "id=? ", new String[] {news_id.toString()});


        Toast.makeText(this, "该新闻已删除！", Toast.LENGTH_SHORT).show();
        newsList.remove(Integer.parseInt(view.getTag().toString()));
        newsAdapter.notifyDataSetChanged();
        db.close();
    }
}
