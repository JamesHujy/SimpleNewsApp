package com.example.simplenewsapp.Utils;

import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

public class HtmlRun implements Runnable {
    private String html;
    private String url;

    private ArrayList<String> title;
    private ArrayList<String> content;
    public HtmlRun(ArrayList<String> title, ArrayList<String> content, String url) {
        this.url = url;
        this.title = title;
        this.content = content;
    }
    public void run() {

        //final TextView tmp = (TextView) findViewById(R.id.text123);
        try {
            html = HtmlService.getHtml(url);
            Log.i("jsonData",html);

        } catch (Exception e) {
            e.printStackTrace();
        }
        HtmlService.parseDiffJson(html, title, content);
    }
}
