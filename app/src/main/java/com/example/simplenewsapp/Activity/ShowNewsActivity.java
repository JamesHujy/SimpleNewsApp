package com.example.simplenewsapp.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ShareCompat;

import com.example.simplenewsapp.R;
import com.example.simplenewsapp.Utils.BitmapHelper;
import com.example.simplenewsapp.Utils.News;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class ShowNewsActivity extends Activity implements View.OnClickListener
{
    private TextView news_title;
    private TextView news_author;
    private TextView news_time;
    private ImageView news_pic;
    private TextView news_body;

    private ImageView collect_news;
    private ImageView transmit_news;
    private ImageView back_to_list;

    private ProgressDialog mDialog;

    private boolean collected = false;

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

        String news_title_str = intent.getStringExtra("title");
        String news_content_str = intent.getStringExtra("content");
        String news_author_str = intent.getStringExtra("author");
        String news_time_str = intent.getStringExtra("date");
        String news_picurl = intent.getStringExtra("pic_url");

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
        back_to_list = findViewById(R.id.back_to_list);

        back_to_list.setOnClickListener(this);
        collect_news.setOnClickListener(this);
    }

    private void addToCollection()
    {

    }

    private void removeFromCollection()
    {

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
}
