package com.example.simplenewsapp.Fragment;

import androidx.fragment.app.Fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
import com.example.simplenewsapp.Utils.NewsDataBaseHelper;

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

    NewsDataBaseHelper dbHelper;

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
                intent.putExtra("date", newsList.get(i - mListView.getHeaderViewsCount()).get_date());
                intent.putExtra("author", newsList.get(i - mListView.getHeaderViewsCount()).get_author());
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

        dbHelper = new NewsDataBaseHelper(this.getContext(), "UserDB.db", null, 1);

        mListView.setInterface(this);
        mListView.setReflashInterface(this);

        newsList = new ArrayList<>();


        adapter = new NewsAdapter(getContext(), R.layout.news_item, newsList, this);
        mListView.setAdapter(adapter);

    }

    void initNews()
    {

        ArrayList<String> titleList = new ArrayList<>();
        ArrayList<String> contentList = new ArrayList<>();
        ArrayList<String> datesList = new ArrayList<>();
        ArrayList<String> authorList = new ArrayList<>();
        ArrayList<String> pic_url = new ArrayList<>();
        setUrl(15);

        System.out.println(url);
        HtmlRun test = new HtmlRun(titleList, contentList, datesList, authorList, pic_url, url);

        Thread thread = new Thread(test);
        thread.start();

        try
        {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for(int i = 0;i < titleList.size();i++)
        {
            String title = titleList.get(i);
            String content = contentList.get(i);
            String date = datesList.get(i);
            String author = authorList.get(i);

            News news = new News(title, content, date, author);
            newsList.add(news);

            ContentValues values = new ContentValues();
            values.put("news_title",title);
            values.put("news_date",date);
            values.put("news_content",content);
            values.put("news_author",author);
            db.insert("Collection_News",null,values);
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });

    }

    private void setUrl(int size) {
        if (type.equals("要闻")) {
            url = basicString + "size=" + size + "&startDate=2019-08-20&endDate=2019-08-29&words=" + type;
        } else
            url = basicString + "size=" + size + "&startDate=2019-08-20&endDate=2019-08-29&categories=" + type;

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
