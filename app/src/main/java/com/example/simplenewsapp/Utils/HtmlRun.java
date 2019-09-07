package com.example.simplenewsapp.Utils;

import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
public class HtmlRun implements Runnable {
    String html;
    String url;

    ArrayList<String> title;
    ArrayList<String> content;
    ArrayList<String> time;
    ArrayList<String> author;
    ArrayList<String> pic_url;

    ArrayList<String> keyword;///////////////////////////////
    ArrayList<String> category;
    ArrayList<String> video_url;
    public HtmlRun(ArrayList<String> title, ArrayList<String> content, ArrayList<String> time, ArrayList<String> author, ArrayList<String> pic_url,
                   ArrayList<String> keyword, ArrayList<String> category, ArrayList<String> video_url, String url) {

        this.url = url;
        this.title = title;
        this.content = content;
        this.time = time;
        this.author = author;
        this.pic_url = pic_url;
        this.keyword = keyword;
        this.category = category;
        this.video_url = video_url;
    }
    public void run() {


        try {
            html = HtmlService.getHtml(url);

        } catch (Exception e) {
            e.printStackTrace();
        }
        com.example.simplenewsapp.Utils.HtmlService.parseDiffJson(html, title, content, time, author, pic_url, keyword, category, video_url);
    }
}
