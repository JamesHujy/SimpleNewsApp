package com.example.simplenewsapp.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.example.simplenewsapp.Adapter.NewsAdapter;
import com.example.simplenewsapp.Utils.News;
import com.example.simplenewsapp.R;

import java.util.ArrayList;
import java.util.List;

public class CollectionActivity extends Activity implements NewsAdapter.CallBack
{
    private List<News> newsList = new ArrayList<>();

    private ListView listView;

    private NewsAdapter newsAdapter;

    private int newsCount = 8;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        initAdapter();
        initNews();
        initView();
        listView.setAdapter(newsAdapter);

    }

    private void initNews()
    {
        for(int i = 0;i < newsCount;i++)
        {
            String title = (i+1)+" 新闻标题 ";
            String content = "顾小姐真好看";
            String date = "2019.6.30";
            String author = "大白";
            News news = new News(title, content, date, author);
            newsList.add(news);
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                newsAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initView()
    {
        listView = findViewById(R.id.listview_collection);
    }

    private void initAdapter()
    {
        newsAdapter = new NewsAdapter(this, R.layout.news_item,newsList,this);
    }

    @Override
    public void click(View view)
    {

    }
}
