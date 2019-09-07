package com.example.simplenewsapp.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ShareCompat;

import com.example.simplenewsapp.Adapter.NewsAdapter;
import com.example.simplenewsapp.R;
import com.example.simplenewsapp.Utils.BitmapHelper;
import com.example.simplenewsapp.Utils.News;
import com.example.simplenewsapp.Utils.NewsDataBaseHelper;
import com.example.simplenewsapp.Utils.ShareAnyWhere;
import com.example.simplenewsapp.Utils.ShareInfoUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class ShowNewsActivity extends Activity implements View.OnClickListener, NewsAdapter.CallBack
{
    private TextView news_title;
    private String news_title_str;
    private String news_author_str;
    private TextView news_author;
    private TextView news_time;
    private String news_time_str;
    private ImageView news_pic;
    private String news_picurl;
    private TextView news_body;
    private String news_body_str;

    private ImageView collect_news;
    private ImageView transmit_news;
    private ImageView back_to_list;
    private ImageView wechat_share;

    private ProgressDialog mDialog;

    private boolean collected = false;

    private NewsDataBaseHelper dbHelper;

    private ListView listView;
    private List<News> newsList = new ArrayList<>();
    private NewsAdapter newsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_news);
        initView();
        getInfo();
    }

    private void getInfo()
    {
        Intent intent = getIntent();

        news_title_str = intent.getStringExtra("title");
        //news_title_str = intent.getStringExtra("title");
        news_body_str = intent.getStringExtra("content");
        news_author_str = intent.getStringExtra("author");
        news_time_str = intent.getStringExtra("date");
        news_picurl = intent.getStringExtra("pic_url");

        String user_name = (String) ShareInfoUtil.getParam(ShowNewsActivity.this, ShareInfoUtil.LOGIN_DATA, "");//注意一下

        //dbHelper = new NewsDataBaseHelper(this, "UserDB.db", null, 1);
        dbHelper = new NewsDataBaseHelper(this, "User_"+user_name+".db", null, 1);

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("select news_title from Collection_News", null);
        //cursor = db.rawQuery("select news_title, iflike from Collection_News where news_title="+news_title_str, null);
        //cursor = db.rawQuery("select news_title, iflike from Collection_News where id="+5, null);
        cursor = db.rawQuery("select news_title, iflike from Collection_News where news_title=?", new String[]{news_title_str});
        cursor.moveToFirst();
        int like = cursor.getInt(cursor.getColumnIndex("iflike"));
        if (like == 1)
        {
            collected = true;
            collect_news.setImageResource(R.drawable.collected);
        }


        dbHelper = new NewsDataBaseHelper(this, "User_"+user_name+".db", null, 1);

        news_title.setText(news_title_str);
        news_body.setText(news_body_str);
        news_author.setText(news_author_str);
        news_time.setText(news_time_str);

        LoadPic loadPic = new LoadPic(news_picurl, news_pic);
        loadPic.execute(news_picurl);

        Integer id = getIDFromSQL(news_title_str, db);
        ContentValues values = new ContentValues();
        values.put("ifread", 1);
        db.update("Collection_News", values, "id=? ", new String[] {id.toString()});
        ContentValues values_ = new ContentValues();
        //values_.put("news_id", id);
        values_.put("news_title", news_title_str);
        db.insert("News_History", null, values_);
    }

    private class LoadPic extends AsyncTask<Object, Void, Bitmap>
    {
        String url;
        ImageView imageView;

        private String refineString(String pic_url)
        {
            if (pic_url.equals("[]"))
                return "None";
            int urllength = pic_url.length();
            pic_url = pic_url.substring(1,urllength-1);
            pic_url = pic_url.split(",")[0];
            return pic_url;
        }

        private Bitmap getBitmap(String url)
        {
            url = refineString(url);
            Bitmap bm = null;
            try {
                URL iconUrl = new URL(url);
                URLConnection conn = iconUrl.openConnection();
                HttpURLConnection http = (HttpURLConnection) conn;

                int length = http.getContentLength();

                conn.connect();

                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is, length);
                bm = BitmapFactory.decodeStream(bis);
                System.out.println(bm);
                is.close();
            }
            catch (Exception e) {
                System.out.println("Load picture excepition !!!!!!!!!!!!!!!!");
            }
            return bm;
        }

        LoadPic(String url, ImageView imageView)
        {
            this.url = url;
            this.imageView = imageView;
        }
        @Override
        public Bitmap doInBackground(Object[] params)
        {
            url = (String)params[0];
            Bitmap bitmap = getBitmap(url);
            return bitmap;
        }

        @Override
        public void onProgressUpdate(Void[] values)
        {
            super.onProgressUpdate(values);
        }

        @Override
        public void onPostExecute(Bitmap result)
        {

            if(result != null)
            {
                System.out.println("NewsImgShow");
                imageView.setImageBitmap(result);
            }
            else{
                System.out.println("NewsImgGone");
                imageView.setVisibility(View.GONE);
            }
        }
    }

    private void initView()
    {
        news_title = findViewById(R.id.content_title);
        news_author = findViewById(R.id.content_author);
        news_time = findViewById(R.id.content_time);
        news_body = findViewById(R.id.content_body);
        news_pic = findViewById(R.id.content_picture);
        collect_news = findViewById(R.id.collect_news);
        transmit_news = findViewById(R.id.transmit_news);
        back_to_list = findViewById(R.id.back_to_list);
        wechat_share = findViewById(R.id.share_wechat);

        listView = findViewById(R.id.listview_recommend);
        newsAdapter = new NewsAdapter(this, R.layout.news_item, newsList, this);


        back_to_list.setOnClickListener(this);
        collect_news.setOnClickListener(this);
        wechat_share.setOnClickListener(this);

    }

    private int getIDFromSQL(String title, SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("select id, news_title from Collection_News", null);
        while (cursor.moveToNext()) {
            String title_ = cursor.getString(cursor.getColumnIndex("news_title"));
            if (title.equals(title_)) {
                return cursor.getInt(cursor.getColumnIndex("id"));
            }
        }
        return -1;
    }

    private void addToCollection()
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Integer id = getIDFromSQL(news_title_str, db);

        ContentValues values = new ContentValues();
        values.put("iflike", 1);
        db.update("Collection_News", values, "id=? ", new String[] {id.toString()});

        ContentValues values_ = new ContentValues();
        //values_.put("news_id", id);
        values_.put("news_title", news_title_str);
        db.insert("News_Like", null, values_);
        //db.update("news_like", values_, "id=? ", new String[] {})
    }

    private void removeFromCollection()
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Integer id = getIDFromSQL(news_title_str, db);

        ContentValues values = new ContentValues();
        values.put("iflike", 0);
        db.update("Collection_News", values, "id=? ", new String[] {id.toString()});

        ContentValues values_ = new ContentValues();
        //values_.put("news_id", id);
        values_.put("news_title", news_title_str);
        db.delete("News_Like", "news_title_str = ? ", new String[]{news_title_str});
        //db.insert("News_Like", null, values_);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.back_to_list:
            {
                finish();
            }
            break;
            case R.id.collect_news:
            {
                if (collected)
                {
                    collect_news.setImageResource(R.drawable.collect);
                    collected = false;
                    removeFromCollection();
                    Toast toast = Toast.makeText(this, "取消收藏", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else
                {
                    collected = true;
                    collect_news.setImageResource(R.drawable.collected);
                    addToCollection();
                    Toast toast = Toast.makeText(this, "收藏成功", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            break;
            case R.id.share_wechat:
            {
                /*Uri uri_title = ShareAnyWhere.viewToUri(this,news_title);
                Uri uri_body = ShareAnyWhere.viewToUri(this,news_body);
                ArrayList<Uri> uriArrayList = new ArrayList<>();
                uriArrayList.add(uri_title);
                uriArrayList.add(uri_body);
                System.out.println("Try sharing");
                ShareAnyWhere.shareWeichat(this,uriArrayList,"A news");*/
                String mimeType="text/plain";
                ShareCompat.IntentBuilder
                        .from(this)
                        .setType(mimeType)
                        .setChooserTitle("choose app!")
                        .setText(news_body_str)
                        .startChooser();
            }
            break;
            default:
                break;
        }
    }

    public void shareMsg() {
        String mimeType="text/plain";
        ShareCompat.IntentBuilder
                .from(ShowNewsActivity.this)
                .setType(mimeType)
                .setChooserTitle("choose app!")
                .setText("hello world!")
                .startChooser();
    }
    @Override
    public void click(View view)
    {

    }
}
