package com.example.simplenewsapp.Utils;
import android.graphics.Bitmap;

public class News {
    private String news_img_url;
    private String news_title;
    private String news_url;
    private String news_picurl;

    private String date;
    private String topic;
    private String author;
    private String content;

    public News(String news_img, String news_title, String news_url, String topic, String date)
    {
        this.news_img_url = news_img;
        this.news_title = news_title;
        this.news_url = news_url;
        this.topic = topic;
        this.date = date;
    }

    public News(String news_title,String news_content, String date, String author)
    {
        this.news_title = news_title;
        this.content = news_content;
        this.date = date;
        this.author = author;
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

    public String get_pic()
    {
        return news_img_url;
    }
}
