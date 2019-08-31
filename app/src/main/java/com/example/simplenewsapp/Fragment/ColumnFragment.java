package com.example.simplenewsapp.Fragment;

import androidx.fragment.app.Fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.simplenewsapp.Activity.ShowNewsActivity;
import com.example.simplenewsapp.Adapter.NewsAdapter;
import com.example.simplenewsapp.Utils.BitmapHelper;
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
    private List<News> newsList, newsListCache;
    private ArrayList<Bitmap> bitmapArrayList;


    final private String basicString = "https://api2.newsminer.net/svc/news/queryNewsList?";
    private String url;
    private NewsAdapter adapter;

    private String type;

    private String startDate, endDate;
    private int newsCount = 8;
    private Bitmap mBitmap;

    NewsDataBaseHelper dbHelper;

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
                intent.putExtra("pic_url", newsList.get(i - mListView.getHeaderViewsCount()).get_picurl());
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
        newsListCache = new ArrayList<>();

        adapter = new NewsAdapter(getContext(), R.layout.news_item, newsList, this);
        mListView.setAdapter(adapter);

        startDate = "2019-08-27";
        endDate = "2019-08-28";
    }

    void initNews()
    {
        final ArrayList<String> titleList = new ArrayList<>();
        final ArrayList<String> contentList = new ArrayList<>();
        final ArrayList<String> datesList = new ArrayList<>();
        final ArrayList<String> authorList = new ArrayList<>();
        final ArrayList<String> picurlList = new ArrayList<>();
        setUrl(15);

        System.out.println(url);
        HtmlRun test = new HtmlRun(titleList, contentList, datesList, authorList, picurlList, url);

        Thread thread = new Thread(test);
        thread.start();

        try
        {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final BitmapHelper bitmapHelper = new BitmapHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0;i < titleList.size();i++)
                {
                    String title = titleList.get(i);
                    String content = contentList.get(i);
                    String date = datesList.get(i);
                    String author = authorList.get(i);
                    String picurl = picurlList.get(i);

                    System.out.println("Before Print bitmap!");

                    Bitmap bitmap = bitmapHelper.getBitmapFromUrl(picurl);

                    System.out.println("Print bitmap!");
                    System.out.println(bitmap);
                    News news = new News(title, content, date, author, picurl, bitmap);
                    newsListCache.add(news);
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        newsList.clear();
                        newsList.addAll(newsListCache);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    private void setUrl(int size) {
        if (type.equals("要闻"))
            url = basicString + "size=" + size + "&startDate="+startDate+"&endDate="+endDate+"&words=" + type;
         else
            url = basicString + "size=" + size + "&startDate="+startDate+"&endDate="+endDate+"&categories=" + type;

    }
    @Override
    public void click(View view)
    {
        Toast.makeText(getContext(), "该新闻已删除！", Toast.LENGTH_SHORT).show();
        newsList.remove(Integer.parseInt(view.getTag().toString()));
        adapter.notifyDataSetChanged();
    }

    private void RefreshNews()
    {

    }

    private void LoadNews()
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
