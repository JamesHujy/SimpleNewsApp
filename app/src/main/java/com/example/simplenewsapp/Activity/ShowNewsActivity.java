package com.example.simplenewsapp.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.simplenewsapp.R;
import com.example.simplenewsapp.Utils.BitmapHelper;
import com.example.simplenewsapp.Utils.News;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ShowNewsActivity extends Activity
{
    private TextView news_title;
    private TextView news_author;
    private TextView news_time;
    private ImageView news_pic;
    private TextView news_body;

    private ImageView collect_news;
    private ImageView transmit_news;
    private ProgressDialog dialog;

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

        final String news_title_str = intent.getStringExtra("title");
        final String news_content_str = intent.getStringExtra("content");
        final String news_author_str = intent.getStringExtra("author");
        final String news_time_str = intent.getStringExtra("date");
        final String news_picurl = intent.getStringExtra("pic_url");

        news_title.setText(news_title_str);
        news_body.setText(news_content_str);
        news_author.setText(news_author_str);
        news_time.setText(news_time_str);

        BitmapHelper bitmapHelper= new BitmapHelper(this);
        news_pic.setImageBitmap(bitmapHelper.getBitmapFromUrl(news_picurl));
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
    }
}
