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
import com.example.simplenewsapp.Utils.HttpUtils;
import com.example.simplenewsapp.Utils.LoadListView;
import com.example.simplenewsapp.Utils.News;
import com.example.simplenewsapp.R;
import com.example.simplenewsapp.Utils.NewsDataBaseHelper;
import com.example.simplenewsapp.Utils.ShareInfoUtil;
import com.example.simplenewsapp.Utils.ThemeManager;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    private String user_name;


    public ColumnFragment(String type)
    {
        this.type = type;
    }

    public void setNightMode()
    {
        for(News news:newsList)
            news.changestate();
        adapter.notifyDataSetChanged();
        for(News news:newsList)
            news.restorestate();
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
                "news_pic_url, ifread from Collection_News where id = " + id, null);
        cursor.moveToFirst();
        if (!cursor.isNull(cursor.getColumnIndex("id"))) {

            String title_ = cursor.getString(cursor.getColumnIndex("news_title"));
            String date_ = cursor.getString(cursor.getColumnIndex("news_date"));
            String content_ = cursor.getString(cursor.getColumnIndex("news_content"));
            String author_ = cursor.getString(cursor.getColumnIndex("news_author"));
            String url_ = cursor.getString(cursor.getColumnIndex("news_pic_url"));
            int ifread = cursor.getInt(cursor.getColumnIndex("ifread"));


            //BitmapHelper bitmapHelper = new BitmapHelper(this.getContext());

            //Bitmap bitmap = bitmapHelper.getBitmapFromUrl(url_);
            News news = new News(title_, content_, date_, author_, url_);
            if (ifread == 1)
            {
                news.change_clicked();
            }

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
        System.out.println("Create Column fragment VIew ");
        view = inflater.inflate(R.layout.news, container, false);

        user_name = (String) ShareInfoUtil.getParam(getContext(), ShareInfoUtil.LOGIN_DATA, "");//注意一下

        dbHelper = new NewsDataBaseHelper(getContext(), "User_"+user_name+".db", null, 1);

        System.out.println("going to judge network");
        if (HttpUtils.isNetworkConnected(getContext()))
        {
            Toast.makeText(getContext(), "当前网络可用", Toast.LENGTH_SHORT).show();
            setupViews();
            System.out.println("connected!");
            initNews(1);
        }
        else
        {
            setupViews();
            Toast.makeText(getContext(), "当前网络不可用", Toast.LENGTH_SHORT).show();
            initLocalNews();
        }

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
                intent.putExtra("source_activity","mainlist");
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
    boolean judgeShieldFromID(int id, SQLiteDatabase db, String wordShield) {
        Cursor cursor = db.rawQuery("select id, key_words from Collection_News where id = "+id, null);
        cursor.moveToFirst();
        String keyWords = cursor.getString(cursor.getColumnIndex("key_words"));
        ///System.out.println("in columnFragment.judgeShieldFromID "+keyWords);
        String[] shieldWordsList = wordShield.split(" ");
        System.out.println("in columnFragment.judgeShieldFromID "+wordShield);
        System.out.println("in columnFragment.judgeShield "+shieldWordsList.length);
        if (shieldWordsList.length == 1 && shieldWordsList[0].equals(""))
            return false;
        for (int i = 0; i < shieldWordsList.length; i++) {
            //System.out.println("in columnFragment.judgeShieldFromID "+shieldWordsList[i]);
            if (keyWords.contains(shieldWordsList[i]))
                return true;
        }
        return false;
    }
    void setupViews()
    {
        mListView = view.findViewById(R.id.lv_main);

        dbHelper = new NewsDataBaseHelper(getContext(), "User_"+user_name+".db", null, 1);

        mListView.setInterface(this);
        mListView.setReflashInterface(this);

        newsList = new ArrayList<>();
        newsListCache = new ArrayList<>();

        adapter = new NewsAdapter(getContext(), R.layout.news_item, newsList, this);
        mListView.setAdapter(adapter);

        startDate = "2019-09-04";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        endDate = sdf.format(new Date());
    }

    void initLocalNews() {
        System.out.println("Load local !!!!!!!!!!!!!!!!!!!!!!!!!");
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select id, news_title, news_date, news_author, news_pic_url, news_content, "+
                "news_pic_url, news_type from Collection_News", null);
        System.out.println(type);
        while (cursor.moveToNext()) {
            String type_ = cursor.getString(cursor.getColumnIndex("news_type"));
            String title = cursor.getString(cursor.getColumnIndex("news_title"));
            if (type_.equals(type)) {
                System.out.println("type match");
                typeNewsArray[typeNewsTotal++] = getIDFromSQL(title, db);
            }
            System.out.println(type_);
            System.out.println(title);
        }


        //////////////////////////////////以下部分为复制粘贴
        Thread loadthread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (typeNewsTotal - typeNewsWatched >= newsCount) {////////////////////////
                    for (int j = 0; j < newsCount; j++) {
                        //System.out.println("typeNewsWatched is "+typeNewsWatched);
                        //System.out.println("typeNewsTotal is "+typeNewsTotal);
                        System.out.println("in initLocalNews() newsListCache "+newsListCache.size());
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

    void initNews(final int flag)//1则插入 0则不插入cache
    {
        final ArrayList<String> titleList = new ArrayList<>();
        final ArrayList<String> contentList = new ArrayList<>();
        final ArrayList<String> datesList = new ArrayList<>();
        final ArrayList<String> authorList = new ArrayList<>();
        final ArrayList<String> picurlList = new ArrayList<>();
        final ArrayList<String> typeList = new ArrayList<>();
        final ArrayList<String> keywordList = new ArrayList<>();
        final ArrayList<String> videourlList = new ArrayList<>();
        setUrl(2000);/////////?????

        System.out.println("ColumnFragment.initNews "+url);
        HtmlRun test = new HtmlRun(titleList, contentList, datesList, authorList, picurlList, keywordList, typeList, videourlList, url);

        Thread thread = new Thread(test);
        thread.start();

        try
        {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (titleList.size() == 0)
        {
            initLocalNews();
            adapter.setOffline();
            return;
        }
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        for(int i = 0;i < titleList.size();i++)
        {
            String title = titleList.get(i);
            String content = contentList.get(i);
            String date = datesList.get(i);
            String author = authorList.get(i);
            String picurl = picurlList.get(i);
            String keywords = keywordList.get(i);
            String videourl = videourlList.get(i);
            //String type = typeList.get(i);

            if (checkIfNew(title, db)) {
                ContentValues values = new ContentValues();
                values.put("news_title",title);
                values.put("news_date",date);
                values.put("news_content",content);
                values.put("news_author",author);
                values.put("news_pic_url", picurl);
                values.put("key_words", keywords);
                values.put("news_type", type);
                values.put("video_url", videourl);
                values.put("iflike", 0);
                values.put("ifread", 0);


                System.out.println("news_pic_url"+picurl);

                db.insert("Collection_News",null,values);
                /////
                typeNewsArray[typeNewsTotal++] = getIDFromSQL(title, db);
            }
            else {
                //System.out.println("news not new!");
                //判重
                int id = getIDFromSQL(title, db);
                boolean flg = false;
                for (int k = 0; k < typeNewsTotal; k++) {
                    if (typeNewsArray[k] == id)
                        flg = true;
                }
                if (!flg)
                {
                    //System.out.println("title is "+title);
                    /////
                    typeNewsArray[typeNewsTotal++] = id;
                }

            }

            //typeNewsWatched += 5;
            //可以根据typeNewsTotal与typeNewsWatched的数值关系做进一步处理
        }
        System.out.println("before add news " + typeNewsTotal);

        final String mask = (String) ShareInfoUtil.getParam(getContext(), ShareInfoUtil.MASK_WORDS, "");
        Thread loadthread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (typeNewsTotal - typeNewsWatched >= newsCount) {////////////////////////
                    for (int j = 0; j < newsCount; j++) {
                        //System.out.println("typeNewsWatched is "+typeNewsWatched);
                        //System.out.println("typeNewsTotal is "+typeNewsTotal);
                        System.out.println("in initNews newsListCache "+newsListCache.size());
                        if (!judgeShieldFromID(typeNewsArray[typeNewsWatched + j], db, mask) && flag == 1)
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

    void mask()
    {
        String mask = (String) ShareInfoUtil.getParam(getContext(), ShareInfoUtil.MASK_WORDS, "");
    }
    private void LoadNews()
    {
        while (typeNewsTotal - typeNewsWatched <= 2 * newsCount) {
            int startDay = Integer.parseInt(startDate.substring(8, 10));
            int startMon = Integer.parseInt(startDate.substring(5, 7));
            if (startDay-- == 1) {
                startDay = 31;
                startMon -= 1;
            }
            if (startMon == 0)
                startMon = 12;
            //System.out.println(starttime);
            int endDay = Integer.parseInt(endDate.substring(8, 10));
            int endMon = Integer.parseInt(endDate.substring(5, 7));
            if (endDay-- == 1) {
                endDay = 31;
                endMon -= 1;
            }
            if (endMon == 0)
                endMon = 12;
            startDate = startDate.substring(0, 5) + String.format("%0"+2+"d", startMon) + '-' +String.format("%0"+2+"d", startDay);
            endDate = endDate.substring(0, 5) + String.format("%0"+2+"d", endMon) + '-' + String.format("%0"+2+"d", endDay);
            setUrl(2000);
            initNews(0);
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //newsListCache.clear();
       // newsList.clear();
        //for (int i = 0; i < newsCount; i++)
        //    newsListCache
        typeNewsWatched += newsCount;
        System.out.println("columnFragment.loadNews"+typeNewsWatched);
        final String mask = (String) ShareInfoUtil.getParam(getContext(), ShareInfoUtil.MASK_WORDS, "");
        if (typeNewsTotal - typeNewsWatched >= newsCount) {
            for (int j = 0; j < newsCount; j++) {
                System.out.println("in loadnews newsListCache "+newsListCache.size());
                if (!judgeShieldFromID(typeNewsArray[typeNewsWatched + j], db, mask))
                   // newsList.add(getNewsFromSQL(typeNewsArray[typeNewsWatched + j], db));
                    newsListCache.add(getNewsFromSQL(typeNewsArray[typeNewsWatched + j], db));
            }
        }
        newsList.clear();
        newsList.addAll(newsListCache);
        adapter.notifyDataSetChanged();
        System.out.println("??????in loadnews newsListCache "+newsListCache.size());
    }

    private void RefreshNews()
    {

        while (typeNewsTotal - typeNewsWatched <= 2 * newsCount) {
            int startDay = Integer.parseInt(startDate.substring(8, 10));
            int startMon = Integer.parseInt(startDate.substring(5, 7));
            if (startDay-- == 1) {
                startDay = 31;
                startMon -= 1;
            }
            if (startMon == 0)
                startMon = 12;
            //System.out.println(starttime);
            int endDay = Integer.parseInt(endDate.substring(8, 10));
            int endMon = Integer.parseInt(endDate.substring(5, 7));
            if (endDay-- == 1) {
                endDay = 31;
                endMon -= 1;
            }
            if (endMon == 0)
                endMon = 12;
            startDate = startDate.substring(0, 5) + String.format("%0"+2+"d", startMon) + '-' +String.format("%0"+2+"d", startDay);
            endDate = endDate.substring(0, 5) + String.format("%0"+2+"d", endMon) + '-' + String.format("%0"+2+"d", endDay);
            setUrl(2000);
            initNews(0);
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        newsListCache.clear();
        newsList.clear();
        final String mask = (String) ShareInfoUtil.getParam(getContext(), ShareInfoUtil.MASK_WORDS, "");
        if (typeNewsTotal - typeNewsWatched >= newsCount) {
            typeNewsWatched += newsCount;
            int tmp = typeNewsTotal - typeNewsWatched;
            tmp = Math.min(tmp, newsCount);

            for (int j = 0; j < tmp; j++) {
                System.out.println("in refreshnews newsListCache "+newsListCache.size());
                if (!judgeShieldFromID(typeNewsArray[typeNewsWatched + j], db, mask))
                    newsListCache.add(getNewsFromSQL(typeNewsArray[typeNewsWatched + j], db));
            }

        }
        /*typeNewsWatched += newsCount;
        if (typeNewsTotal - typeNewsWatched >= newsCount) {
            for (int j = 0; j < newsCount; j++) {
                newsListCache.add(0, getNewsFromSQL(typeNewsArray[typeNewsWatched + j], db));
            }
        }*/
        newsList.addAll(newsListCache);
        for(News news:newsList)
        {
            System.out.println(news.get_title()+news.get_click());
        }
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
        },1000);

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
        },1000);

    }


}
