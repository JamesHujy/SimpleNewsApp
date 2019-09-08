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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.core.app.ShareCompat;

import com.example.simplenewsapp.Adapter.NewsAdapter;
import com.example.simplenewsapp.R;
import com.example.simplenewsapp.Utils.BitmapHelper;
import com.example.simplenewsapp.Utils.HtmlRun;
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
    private VideoView videoView;
    private String videourl;

    private ImageView collect_news;
    private ImageView transmit_news;
    private ImageView back_to_list;
    private ImageView wechat_share_word;
    private ImageView wechat_share_pic;
    private String source_activity;
    private Bitmap bitmap;

    private LinearLayout linearLayout;

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
        loadVideo();
    }

    private void getInfo()
    {
        Intent intent = getIntent();

        news_title_str = intent.getStringExtra("title");
        news_body_str = intent.getStringExtra("content");
        news_author_str = intent.getStringExtra("author");
        news_time_str = intent.getStringExtra("date");
        news_picurl = intent.getStringExtra("pic_url");
        source_activity = intent.getStringExtra("source_activity");
        videourl = intent.getStringExtra("videourl");


        System.out.println(news_title_str);
        System.out.println(videourl);

        String user_name = (String) ShareInfoUtil.getParam(this, ShareInfoUtil.LOGIN_DATA, "");//注意一下

        //dbHelper = new NewsDataBaseHelper(this, "UserDB.db", null, 1);
        dbHelper = new NewsDataBaseHelper(this, "User_"+user_name+".db", null, 1);

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("select news_title, key_words, iflike from Collection_News where news_title=?", new String[]{news_title_str});
        System.out.println(cursor.getCount());
        //System.out.println(news);
        cursor.moveToFirst();
        int like = cursor.getInt(cursor.getColumnIndex("iflike"));
        if (like == 1)
        {
            collected = true;
            collect_news.setImageResource(R.drawable.collected);
        }


        //dbHelper = new NewsDataBaseHelper(this, "User_"+user_name+".db", null, 1);

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

        String keyWords = cursor.getString(cursor.getColumnIndex("key_words"));
        String[] keyWordList = keyWords.split(" ");

        recommendNews(keyWordList[0], db, newsList);

        System.out.println("in showNewsActivity.recommendNews "+newsList.size());
        newsAdapter.notifyDataSetChanged();
        db.close();
        //newsList.add()
    }///

    private void recommendNews(String type, SQLiteDatabase db, List<News> newsList) {
        System.out.println("in showNewsActivity.recommendNews "+type);
        newsList.clear();
        String url = "https://api2.newsminer.net/svc/news/queryNewsList?size=3&words=" + type;
        final ArrayList<String> titleList = new ArrayList<>();
        final ArrayList<String> contentList = new ArrayList<>();
        final ArrayList<String> datesList = new ArrayList<>();
        final ArrayList<String> authorList = new ArrayList<>();
        final ArrayList<String> picurlList = new ArrayList<>();
        final ArrayList<String> typeList = new ArrayList<>();
        final ArrayList<String> keywordList = new ArrayList<>();
        final ArrayList<String> videourlList = new ArrayList<>();
        //setUrl(2000);/////////?????

        System.out.println("ColumnFragment.initNews "+url);
        HtmlRun test = new HtmlRun(titleList, contentList, datesList, authorList, picurlList, keywordList, typeList, videourlList, url);

        Thread thread = new Thread(test);
        thread.start();

        try
        {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //final SQLiteDatabase db = dbHelper.getWritableDatabase();

        for(int i = 0;i < titleList.size();i++)
        {
            String title = titleList.get(i);
            String content = contentList.get(i);
            String date = datesList.get(i);
            String author = authorList.get(i);
            String picurl = picurlList.get(i);
            String keywords = keywordList.get(i);
            String videourl = videourlList.get(i);
            //String type = typeList.get(i);

            if (checkIfNew(title, db)) {
                ContentValues values = new ContentValues();
                values.put("news_title",title);
                values.put("news_date",date);
                values.put("news_content",content);
                values.put("news_author",author);
                values.put("news_pic_url", picurl);
                values.put("key_words", keywords);
                values.put("news_type", type);
                values.put("video_url", videourl);
                values.put("iflike", 0);
                values.put("ifread", 0);


                System.out.println("news_pic_url"+picurl);

                db.insert("Collection_News",null,values);
                /////

            }
            News news = new News(title, content, date, author, url);
            newsList.add(news);
        }

    }
    boolean checkIfNew(String title, SQLiteDatabase db) {
        Cursor cursor = db.query("Collection_News", new String[]{"news_title"}, "news_title = ?", new String[]{title},
                null, null, null);
        if (cursor.getCount()==0)
            return true;
        return false;
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
                linearLayout.setVisibility(View.VISIBLE);
            }
            else {
                System.out.println("NewsImgGone");
                imageView.setVisibility(View.GONE);
                linearLayout.setVisibility(View.GONE);
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
        wechat_share_pic = findViewById(R.id.share_wechat_pic);
        wechat_share_word = findViewById(R.id.share_wechat_word);
        linearLayout = findViewById(R.id.share_pic_layout);

        videoView = findViewById(R.id.content_video);

        listView = findViewById(R.id.listview_recommend);
        newsAdapter = new NewsAdapter(this, R.layout.news_item, newsList, this);

        back_to_list.setOnClickListener(this);
        collect_news.setOnClickListener(this);
        wechat_share_word.setOnClickListener(this);
        wechat_share_pic.setOnClickListener(this);
        transmit_news.setOnClickListener(this);
    }

    private void loadVideo()
    {
        if (videourl != null) {
            if (!videourl.isEmpty()) {
                Uri uri = Uri.parse(videourl);
                System.out.println("Loading video..."+videourl);
                videoView.setMediaController(new MediaController(this));
                videoView.setVideoURI(uri);
                int widthvideo = getWindowManager().getDefaultDisplay().getWidth();
                videoView.setLayoutParams(new LinearLayout.LayoutParams(widthvideo,widthvideo*9/16));
                videoView.start();
            } else {

                videoView.setVideoURI(null);
                videoView.setVisibility(View.GONE);
            }
        }


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
        db.close();
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
        db.delete("News_Like", "news_title = ? ", new String[]{news_title_str});
        //db.insert("News_Like", null, values_);
        db.close();
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.back_to_list:
            {
                finish();
                if (source_activity.equals("collection"))
                {
                    startActivity(new Intent(this, CollectionActivity.class));
                }
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
            case R.id.share_wechat_pic:
            {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.putExtra(Intent.EXTRA_STREAM, Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null)));
                i.setType("image/*");
                startActivity(Intent.createChooser(i, "lnntql!"));
            }
            break;
            case R.id.share_wechat_word:
            case R.id.transmit_news:
                String mimeType="text/plain";
                ShareCompat.IntentBuilder
                        .from(this)
                        .setType(mimeType)
                        .setChooserTitle("choose app!")
                        .setText("标题:"+news_title+"\n"+news_body_str)
                        .startChooser();
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
