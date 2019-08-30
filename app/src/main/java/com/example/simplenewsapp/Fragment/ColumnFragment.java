package com.example.simplenewsapp.Fragment;

import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.simplenewsapp.Activity.ShowNewsActivity;
import com.example.simplenewsapp.Adapter.NewsAdapter;
import com.example.simplenewsapp.Utils.HtmlRun;
import com.example.simplenewsapp.Utils.LoadListView;
import com.example.simplenewsapp.Utils.News;
import com.example.simplenewsapp.R;

import java.util.ArrayList;
import java.util.List;

public class ColumnFragment extends Fragment implements NewsAdapter.CallBack, LoadListView.ILoadListener,
        LoadListView.RLoadListener
{
    private View view;
    private LoadListView mListView;
    private List<News> newsList;

    final private String basicString = "https://api2.newsminer.net/svc/news/queryNewsList?";
    private String url;
    private NewsAdapter adapter;

    private String type;

    private int newsCount = 8;

    public ColumnFragment(){

    }

    public ColumnFragment(String type)
    {
        this.type = type;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.news, container, false);
        setupViews();
        initNews();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Intent intent = new Intent(getContext(), ShowNewsActivity.class);
                intent.putExtra("title", newsList.get(i - mListView.getHeaderViewsCount()).get_title());
                //intent.putExtra("date", newsList.get(i - mListView.getHeaderViewsCount()).get_date());
                //intent.putExtra("author", newsList.get(i - mListView.getHeaderViewsCount()).get_author());
                intent.putExtra("content", newsList.get(i - mListView.getHeaderViewsCount()).get_content());
                //intent.putExtra("pic_url", newsList.get(i - mListView.getHeaderViewsCount().get_pic()));
                startActivity(intent);
            }
        });
        return view;
    }

    void setupViews()
    {
        mListView = view.findViewById(R.id.lv_main);

        mListView.setInterface(this);
        mListView.setReflashInterface(this);

        newsList = new ArrayList<>();
        adapter = new NewsAdapter(getContext(), R.layout.news_item, newsList, this);
        mListView.setAdapter(adapter);

    }

    void initNews()
    {

        ArrayList<String> titles = new ArrayList<>();
        ArrayList<String> contents = new ArrayList<>();

        setUrl(15);
        System.out.println("urllllllllllllllllll");
        System.out.println(url);
        HtmlRun test = new HtmlRun(titles, contents, url);

        Thread thread = new Thread(test);
        thread.start();

        try
        {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for(int i = 0;i < titles.size();i++)
        {
            String title = titles.get(i);
            String content = contents.get(i);
            String date = "2019.6.30";
            News news = new News(title, content, date);
            newsList.add(news);
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });

    }

    private void setUrl(int size)
    {
        if (type.equals("要闻"))
        {
            url = basicString + "size=" + size  + "&endtime=2019-8-28" + "&words=" + type;
        }
        else
            url = basicString + "size=" + size  + "&endtime=2019-8-28" + "&categories=" + type;
    }

    @Override
    public void click(View view)
    {

    }

    @Override
    public void onLoad()
    {

    }

    @Override
    public void onRefresh()
    {

    }
}
