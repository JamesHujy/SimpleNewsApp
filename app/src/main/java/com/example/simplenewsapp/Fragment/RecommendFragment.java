package com.example.simplenewsapp.Fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.simplenewsapp.Activity.ShowNewsActivity;
import com.example.simplenewsapp.Adapter.NewsAdapter;
import com.example.simplenewsapp.R;
import com.example.simplenewsapp.Utils.HtmlRun;
import com.example.simplenewsapp.Utils.LoadListView;
import com.example.simplenewsapp.Utils.News;
import com.example.simplenewsapp.Utils.NewsDataBaseHelper;
import com.example.simplenewsapp.Utils.ShareInfoUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class RecommendFragment extends Fragment implements NewsAdapter.CallBack, LoadListView.ILoadListener,
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

    private NewsDataBaseHelper dbHelper;

    private Integer[] typeNewsArray = new Integer[2000];//记录在数据库中哪些id属于这类新闻
    private Integer typeNewsTotal = 0;
    private Integer typeNewsWatched = 0;
    private Integer numPerRefresh = 0;
    private boolean ifEmpty = true;

    private String user_name;

    public RecommendFragment() {}

    void init()
    {
        dbHelper = new NewsDataBaseHelper(getContext(), "User_"+user_name+".db", null, 1);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select id, key_words, ifread from Collection_News where ifread = 1", null);
        Map<String, Integer> keyWordsTimes = new HashMap<>();
        while (cursor.moveToNext()) {
            String keyWords = cursor.getString(cursor.getColumnIndex("key_words"));
            String[] keyWordsList = keyWords.split(" ");
            int len = keyWordsList.length;

            for (int i = 0; i < len; i++) {
                String tmp = keyWordsList[i];
                if (!tmp.equals("")) {
                    if (keyWordsTimes.containsKey(tmp)) {
                        int tmp_ = keyWordsTimes.get(tmp);
                        keyWordsTimes.put(tmp, tmp_ + 1);
                    }
                    else
                        keyWordsTimes.put(tmp, 1);
                }
                //System.out.println(tmp+" ");
            }
        }
        System.out.println(keyWordsTimes.size());

        int maxV = 0;
        String maxK = null;
        String maxK_mayberemove = null;// 中间值，用于保存每次存在的最大值键，但存在下个值比他大，可用他移除掉，替换成最新的值
        Map<String, Integer> map2 = new TreeMap();
        Iterator keys = keyWordsTimes.keySet().iterator();
        while (keys.hasNext()) {
            Object key = keys.next();
            maxK = key.toString();
            int value = keyWordsTimes.get(key);
            if (value > maxV) {
                if (null != maxK_mayberemove) {
                    map2.clear();
                }
                maxV = value;
                map2.put(maxK, maxV);
                maxK_mayberemove = maxK;
            } else if (value == maxV) {
                map2.put(maxK, maxV);
            }
        }
        Iterator keys2 = map2.keySet().iterator();
        if (keys2.hasNext()){
            Object key = keys2.next();
            maxK = key.toString();
            type = maxK;
        }
        else
            type = "体育";

        System.out.println(type);

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
            String date_ = cursor.getString(cursor.getColumnIndex("news_date"));
            String content_ = cursor.getString(cursor.getColumnIndex("news_content"));
            String author_ = cursor.getString(cursor.getColumnIndex("news_author"));
            String url_ = cursor.getString(cursor.getColumnIndex("news_pic_url"));

            News news = new News(title_, content_, date_, author_, url_);
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

        user_name = (String) ShareInfoUtil.getParam(getContext(), ShareInfoUtil.LOGIN_DATA, "");//注意一下
        init();
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
                intent.putExtra("source_activity","recommend");
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

        dbHelper = new NewsDataBaseHelper(getContext(), "User_"+user_name+".db", null, 1);

        mListView.setInterface(this);
        mListView.setReflashInterface(this);

        newsList = new ArrayList<>();
        newsListCache = new ArrayList<>();

        adapter = new NewsAdapter(getContext(), R.layout.news_item, newsList, this);
        mListView.setAdapter(adapter);

        startDate = "2019-09-04";
        endDate = "2019-09-05";
    }

    void initNews()
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

        System.out.println(url);
        HtmlRun test = new HtmlRun(titleList, contentList, datesList, authorList, picurlList, keywordList, typeList, videourlList, url);

        Thread thread = new Thread(test);
        thread.start();

        try
        {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

/////
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        for(int i = 0;i < titleList.size();i++)
        {
            String title = titleList.get(i);
            String content = contentList.get(i);
            String date = datesList.get(i);
            String author = authorList.get(i);
            String picurl = picurlList.get(i);
            String keywords = keywordList.get(i);
            String type = typeList.get(i);
            String videourl = videourlList.get(i);

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
                System.out.println("news not new!");
                if (ifEmpty)
                {
                    System.out.println("title is "+title);
                    /////
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
                if (typeNewsTotal - typeNewsWatched >= newsCount) {////////////////////////
                    for (int j = 0; j < newsCount; j++) {
                        //System.out.println("typeNewsWatched is "+typeNewsWatched);
                        //System.out.println("typeNewsTotal is "+typeNewsTotal);
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

    private void RefreshNews()
    {
        init();

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
            initNews();
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        newsListCache.clear();
        newsList.clear();
        if (typeNewsTotal - typeNewsWatched >= newsCount) {
            typeNewsWatched += newsCount;
            int tmp = typeNewsTotal - typeNewsWatched;
            tmp = Math.min(tmp, newsCount);

            for (int j = 0; j < tmp; j++) {
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
