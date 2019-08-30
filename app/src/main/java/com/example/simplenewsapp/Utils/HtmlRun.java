package com.example.simplenewsapp.Utils;

import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import com.example.myapplication.HtmlService;
public class HtmlRun implements Runnable {
    String html;
    String url;

    ArrayList<String> title;
    ArrayList<String> content;
    ArrayList<String> time;
    ArrayList<String> author;
    ArrayList<String> pic_url;
    public HtmlRun(ArrayList<String> title, ArrayList<String> content, ArrayList<String> time, ArrayList<String> author, ArrayList<String> pic_url, String url) {

        this.url = url;
        this.title = title;
        this.content = content;
        this.time = time;
        this.author = author;
        this.pic_url = pic_url;
    }
    public void run() {


        try {
            html = HtmlService.getHtml(url);
            Log.i("jsonData",html);

        } catch (Exception e) {
            e.printStackTrace();
        }
        com.example.myapplication.HtmlService.parseDiffJson(html, title, content, time, author, pic_url);
    }
}
