package com.example.simplenewsapp.Fragment;

import androidx.fragment.app.Fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
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

    final private String basicString = "https://api2.newsminer.net/svc/news/queryNewsList?";
    private String url;
    private NewsAdapter adapter;

    private String type;

    private String startDate, endDate;
    final private int newsCount = 8;

    NewsDataBaseHelper dbHelper;

    private Integer[] typeNewsArray = new Integer[2000];//记录在数据库中哪些id属于这类新闻
    private Integer typeNewsTotal = 0;
    private Integer typeNewsWatched = 0;
    private Integer numPerRefresh = 0;
    private boolean ifEmpty = true;


    public ColumnFragment(String type)
    {
        this.type = type;
    }

    int getIDFromSQL(String title, SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("select id, news_title from Collection_News", null);
        while (cursor.moveToNext()) {
            String title_ = cursor.getString(cursor.getColumnIndex("news_title"));
            if (title.equals(title_)) {
                return cursor.getInt(cursor.getColumnIndex("id"));
            }
        }
        return -1;
    }

    News getNewsFromSQL(int id, SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("select id, news_title, news_date, news_author, news_pic_url, news_content, " +
                "news_pic_url from Collection_News where id = " + id, null);
        cursor.moveToFirst();
        if (!cursor.isNull(cursor.getColumnIndex("id"))) {

            String title_ = cursor.getString(cursor.getColumnIndex("news_title"));
            System.out.println("in get News from SQL. title is "+title_);
            String date_ = cursor.getString(cursor.getColumnIndex("news_date"));
            String content_ = cursor.getString(cursor.getColumnIndex("news_content"));
            String author_ = cursor.getString(cursor.getColumnIndex("news_author"));
            String url_ = cursor.getString(cursor.getColumnIndex("news_pic_url"));
            System.out.println("test cursor author is " + author_);
            System.out.println("test cursor url is " + url_);

            BitmapHelper bitmapHelper = new BitmapHelper(this.getContext());

            Bitmap bitmap = bitmapHelper.getBitmapFromUrl(url_);
            News news = new News(title_, content_, date_, author_, url_, bitmap);
            return news;
        }
        return new News("", "", "", "", "");
        //return -1;
    }
    boolean checkIfNew(String title, SQLiteDatabase db) {
        Cursor cursor = db.query("Collection_News", new String[]{"news_title"}, "news_title = ?", new String[]{title},
                null, null, null);
        if (cursor.getCount()==0)
            return true;
        return false;
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
                newsList.get(i-mListView.getHeaderViewsCount()).change_clicked();
                startActivity(intent);
                for(int k = 0;k < 100000000;k++)
                {
                    int u = 0;
                }
                adapter.notifyDataSetChanged();
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
        setUrl(2000);/////////?????

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


        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        for(int i = 0;i < titleList.size();i++)
        {
            String title = titleList.get(i);
            String content = contentList.get(i);
            String date = datesList.get(i);
            String author = authorList.get(i);
            String picurl = picurlList.get(i);

            if (checkIfNew(title, db)) {
                ContentValues values = new ContentValues();
                values.put("news_title",title);
                values.put("news_date",date);
                values.put("news_content",content);
                values.put("news_author",author);
                values.put("news_pic_url", picurl);

                System.out.println("news_pic_url"+picurl);

                db.insert("Collection_News",null,values);
                typeNewsArray[typeNewsTotal++] = getIDFromSQL(title, db);
            }
            else {
                System.out.println("news not new!");
                if (ifEmpty)
                {
                    System.out.println("title is "+title);
                    typeNewsArray[typeNewsTotal++] = getIDFromSQL(title, db);
                }

            }

            //typeNewsWatched += 5;
            //可以根据typeNewsTotal与typeNewsWatched的数值关系做进一步处理
        }
        System.out.println("before add news " + typeNewsTotal);

        Thread loadthread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (typeNewsTotal >= newsCount) {
                    for (int j = 0; j < newsCount; j++) {
                        newsListCache.add(getNewsFromSQL(typeNewsArray[typeNewsWatched + j], db));
                    }
                }

                ifEmpty = false;
                /////
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        newsList.clear();
                        newsList.addAll(newsListCache);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });

        loadthread.start();

        try{
            loadthread.join();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        db.close();

    }

    private void setUrl(int size) {
        if (type.equals("科技") || type.equals("体育") || type.equals("教育") ||
                type.equals("文化") || type.equals("社会") || type.equals("军事") ||
                type.equals("财经") || type.equals("汽车") || type.equals("娱乐") )
            url = basicString + "size=" + size +"&endDate="+endDate+"&categories=" + type;
         else
            url = basicString + "size=" + size +"&endDate="+endDate+"&words=" + type;

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
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        newsListCache.clear();
        newsList.clear();
        typeNewsWatched += newsCount;
        if (typeNewsTotal - typeNewsWatched >= newsCount) {
            for (int j = 0; j < newsCount; j++) {
                newsListCache.add(0, getNewsFromSQL(typeNewsArray[typeNewsWatched + j], db));
            }
        }
        newsList.addAll(newsListCache);

        adapter.notifyDataSetChanged();
    }

    private void LoadNews()
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //newsListCache.clear();
        newsList.clear();
        typeNewsWatched += newsCount;
        if (typeNewsTotal - typeNewsWatched >= newsCount) {
            for (int j = 0; j < newsCount; j++) {
                newsListCache.add(getNewsFromSQL(typeNewsArray[typeNewsWatched + j], db));
            }
        }
        newsList.addAll(newsListCache);

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoad()
    {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                LoadNews();
                mListView.loadCompleted();
            }
        },2000);
    }

    @Override
    public void onRefresh()
    {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                RefreshNews();
                mListView.reflashComplete();
            }
        },2000);
    }
}
