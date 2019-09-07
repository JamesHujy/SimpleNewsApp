package com.example.simplenewsapp.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplenewsapp.Adapter.NewsAdapter;
import com.example.simplenewsapp.R;
import com.example.simplenewsapp.Utils.BaseSearchAdapter;
import com.example.simplenewsapp.Utils.News;
import com.example.simplenewsapp.Utils.NewsDataBaseHelper;
import com.example.simplenewsapp.Utils.SearchAdapter;
import com.example.simplenewsapp.Utils.SearchDataBase;
import com.example.simplenewsapp.Utils.ShareInfoUtil;

import java.util.ArrayList;
import java.util.List;

public class MaskActivity extends Activity {

    private Button mbtn_serarch;
    private EditText met_search;
    private RecyclerView mRecyclerView;
    private TextView mtv_deleteAll;


    private SearchAdapter mSearchAdapter;
    private SearchDataBase mDatebase;

    private RelativeLayout linearLayout;

    private NewsDataBaseHelper dbHelper;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mask);
        initSearch();
    }

    private void initSearch()
    {
        String user_name = (String) ShareInfoUtil.getParam(this, ShareInfoUtil.LOGIN_DATA, "");
        dbHelper = new NewsDataBaseHelper(this, "User_"+user_name+".db", null, 1);
        mDatebase =new SearchDataBase(this, user_name+"_mask_record.db");
        mbtn_serarch = findViewById(R.id.btn_serarch);
        met_search = findViewById(R.id.et_search);

        mtv_deleteAll = findViewById(R.id.tv_deleteAll);
        mtv_deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatebase.deleteData();
                mSearchAdapter.updata(mDatebase.queryData(""));
            }
        });
        mRecyclerView = findViewById(R.id.mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSearchAdapter =new SearchAdapter(mDatebase.queryData(""),this);
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
                    mSearchAdapter.updata(mDatebase.queryData(""));
                }

            }
        });
    }
}
