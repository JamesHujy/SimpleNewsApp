package com.example.simplenewsapp.Utils;
import android.graphics.Bitmap;

public class News {

    private String news_img_url;
    private String news_title;

    private String date;
    private String topic;
    private String author;
    private String content;
    private String videourl;
    private Bitmap bitmap;
    private boolean clicked = false;
    private boolean collected = false;
    private String databaseId;

    public News(String news_title,String news_content, String date, String author, String url)
    {
        this.news_title = news_title;
        this.content = news_content;
        this.date = date;
        this.author = author;
        this.news_img_url = url;

    }

    public News(String news_title,String news_content, String date, String author, String url, String videourl)
    {
        this.news_title = news_title;
        this.content = news_content;
        this.date = date;
        this.author = author;
        this.news_img_url = url;
        this.videourl = videourl;

    }
    public News(String news_title,String news_content, String date, String author, String url, Bitmap bitmap)
    {
        this.news_title = news_title;
        this.content = news_content;
        this.date = date;
        this.author = author;
        this.news_img_url = url;
        this.bitmap = bitmap;
    }

    public String get_title()
    {
        return news_title;
    }

    public String get_author()
    {
        return author;
    }

    public String get_content()
    {
        return content;
    }

    public String get_date()
    {
        return date;
    }

    public String get_videourl(){return videourl;}
    public Bitmap get_pic()
    {
        return bitmap;
    }

    public String get_picurl()
    {
        return news_img_url;
    }

    public boolean get_click()
    {
        return clicked;
    }

    public void change_clicked()
    {
        clicked = true;
    }
}
