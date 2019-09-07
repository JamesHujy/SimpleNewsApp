package com.example.simplenewsapp.Fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplenewsapp.Activity.ChannelChooseActivity;
import com.example.simplenewsapp.Activity.ShowNewsActivity;
import com.example.simplenewsapp.Adapter.NewsAdapter;
import com.example.simplenewsapp.R;
import com.example.simplenewsapp.Utils.BaseSearchAdapter;
import com.example.simplenewsapp.Utils.HtmlRun;
import com.example.simplenewsapp.Utils.LoadListView;
import com.example.simplenewsapp.Utils.News;
import com.example.simplenewsapp.Utils.NewsDataBaseHelper;
import com.example.simplenewsapp.Utils.SearchAdapter;
import com.example.simplenewsapp.Utils.SearchDataBase;
import com.example.simplenewsapp.Utils.ShareInfoUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SearchFragment extends Fragment implements NewsAdapter.CallBack
{
    private Button mbtn_serarch;
    private EditText met_search;
    private RecyclerView mRecyclerView;
    private TextView mtv_deleteAll;
    private List<News> newsArrayList;
    private View view;
    private SearchAdapter mSearchAdapter;
    private SearchDataBase mDatebase;
    private ListView listView;
    private RelativeLayout linearLayout;
    private String startDate;
    private String endDate;
    final private String basicString = "https://api2.newsminer.net/svc/news/queryNewsList?";
    private String url;

    private NewsAdapter mNewsAdapter;

    private NewsDataBaseHelper dbHelper;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){

        view = inflater.inflate(R.layout.fragment_search, container, false);
        initSearch();
        initShow();

        return view;
    }

    private void initSearch() {
        String user_name = (String) ShareInfoUtil.getParam(getContext(), ShareInfoUtil.LOGIN_DATA, "");
        dbHelper = new NewsDataBaseHelper(getContext(), "User_"+user_name+".db", null, 1);
        mDatebase =new SearchDataBase(this.getContext(), user_name+"_search_record.db");
        mbtn_serarch = view.findViewById(R.id.btn_serarch);
        met_search = view.findViewById(R.id.et_search);

        mtv_deleteAll = view.findViewById(R.id.tv_deleteAll);
        mtv_deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatebase.deleteData();
                mSearchAdapter.updata(mDatebase.queryData(""));
            }
        });
        mRecyclerView = (RecyclerView) view.findViewById(R.id.mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mSearchAdapter =new SearchAdapter(mDatebase.queryData(""),this.getContext());
        mSearchAdapter.setRvItemOnclickListener(new BaseSearchAdapter.RvItemOnclickListener(){
            @Override
            public void RvItemOnclick(int position) {
                mDatebase.delete(mDatebase.queryData("").get(position));

                mSearchAdapter.updata(mDatebase.queryData(""));
            }
        });
        mRecyclerView.setAdapter(mSearchAdapter);
        //事件监听
        mbtn_serarch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchContent = met_search.getText().toString().trim();
                System.out.println(searchContent);
                if (searchContent.length() != 0){
                    boolean hasData = mDatebase.hasData(searchContent);
                    if (!hasData)
                    {
                        mDatebase.insertData(searchContent);
                        System.out.println("in search"+searchContent);

                    }
                    getNews(searchContent);
                    listView.setVisibility(View.VISIBLE);
                    linearLayout.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.GONE);

                    //
                    mSearchAdapter.updata(mDatebase.queryData(""));
                }else {
                    Toast.makeText(getContext(), "请输入内容", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void initShow()
    {
        //mLoadListView = view.findViewById(R.id.search_result);


        listView = view.findViewById(R.id.listview_search);
        newsArrayList = new ArrayList<>();
        linearLayout = view.findViewById(R.id.history_info);

        mNewsAdapter = new NewsAdapter(getContext(), R.layout.news_item, newsArrayList,this);
        listView.setAdapter(mNewsAdapter);
        listView.setVisibility(View.GONE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Intent intent = new Intent(getContext(), ShowNewsActivity.class);
                intent.putExtra("title", newsArrayList.get(i - listView.getHeaderViewsCount()).get_title());
                intent.putExtra("date", newsArrayList.get(i - listView.getHeaderViewsCount()).get_date());
                intent.putExtra("author", newsArrayList.get(i - listView.getHeaderViewsCount()).get_author());
                intent.putExtra("content", newsArrayList.get(i - listView.getHeaderViewsCount()).get_content());
                intent.putExtra("pic_url", newsArrayList.get(i - listView.getHeaderViewsCount()).get_picurl());
                //newsList.get(i-listView.getHeaderViewsCount()).change_clicked();
                startActivity(intent);
            }
        });
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
            //System.out.println("in get News from SQL. title is "+title_);
            String date_ = cursor.getString(cursor.getColumnIndex("news_date"));
            String content_ = cursor.getString(cursor.getColumnIndex("news_content"));
            String author_ = cursor.getString(cursor.getColumnIndex("news_author"));
            String url_ = cursor.getString(cursor.getColumnIndex("news_pic_url"));
            //System.out.println("test cursor author is " + author_);
            //System.out.println("test cursor url is " + url_);

            //BitmapHelper bitmapHelper = new BitmapHelper(this.getContext());

            //Bitmap bitmap = bitmapHelper.getBitmapFromUrl(url_);
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

    private void setUrl(String type, int size) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        endDate = sdf.format(new Date());
        System.out.println("Date:"+endDate);

        if (type.equals("科技") || type.equals("体育") || type.equals("教育") ||
                type.equals("文化") || type.equals("社会") || type.equals("军事") ||
                type.equals("财经") || type.equals("汽车") || type.equals("娱乐") )
            url = basicString + "size=" + size +"&endDate="+endDate+"&categories=" + type;
        else
            url = basicString + "size=" + size +"&endDate="+endDate+"&words=" + type;

    }

    private void getNews(String content)
    {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final ArrayList<String> titleList = new ArrayList<>();
        final ArrayList<String> contentList = new ArrayList<>();
        final ArrayList<String> datesList = new ArrayList<>();
        final ArrayList<String> authorList = new ArrayList<>();
        final ArrayList<String> picurlList = new ArrayList<>();
        final ArrayList<String> typeList = new ArrayList<>();
        final ArrayList<String> keywordList = new ArrayList<>();
        final ArrayList<String> videourlList = new ArrayList<>();
        setUrl(content,2000);

        HtmlRun test = new HtmlRun(titleList, contentList, datesList, authorList, picurlList, keywordList, typeList, videourlList, url);
        Thread thread = new Thread(test);
        thread.start();

        try
        {
            thread.join();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        System.out.println(titleList.size());
        for(int i = 0; i < titleList.size();i++)
        {
            /*String title = titleList.get(i);
            String body = contentList.get(i);
            String date = datesList.get(i);
            String author = authorList.get(i);
            String picurl = picurlList.get(i);
            String videourl = videourlList.get(i);*/
            String title = titleList.get(i);
            String content_ = contentList.get(i);
            String date = datesList.get(i);
            String author = authorList.get(i);
            String picurl = picurlList.get(i);
            String keywords = keywordList.get(i);
            String videourl = videourlList.get(i);
            if (checkIfNew(title, db)) {
                ContentValues values = new ContentValues();
                values.put("news_title",title);
                values.put("news_date",date);
                values.put("news_content",content_);
                values.put("news_author",author);
                values.put("news_pic_url", picurl);
                values.put("key_words", keywords);
                values.put("news_type", content);
                values.put("video_url", videourl);
                values.put("iflike", 0);
                values.put("ifread", 0);


                //System.out.println("news_pic_url"+picurl);

                db.insert("Collection_News",null,values);
                /////
                //typeNewsArray[typeNewsTotal++] = getIDFromSQL(title, db);
            }

            News news = new News(title, content_, date, author, picurl);
            newsArrayList.add(news);
        }

        mNewsAdapter.notifyDataSetChanged();
    }

    @Override
    public void click(View view)
    {
        Toast.makeText(this.getContext(), "该新闻已删除！", Toast.LENGTH_SHORT).show();
        newsArrayList.remove(Integer.parseInt(view.getTag().toString()));

        mNewsAdapter.notifyDataSetChanged();
    }

}
