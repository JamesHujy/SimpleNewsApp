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
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.example.simplenewsapp.Utils.ThemeManager;

import java.util.ArrayList;
import java.util.List;

public class MaskActivity extends Activity {

    private Button mbtn_serarch;
    private EditText met_search;
    private RecyclerView mRecyclerView;
    private TextView mtv_deleteAll, masked_word;
    private ImageView exitMask;


    private SearchAdapter mSearchAdapter;
    private SearchDataBase mDatebase;

    private RelativeLayout relativeLayout;

    private LinearLayout mask_layout;
    private LinearLayout mask_head;

    //private NewsDataBaseHelper dbHelper;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mask);
        initSearch();
    }

    private void initSearch()
    {
        mask_layout = findViewById(R.id.mask_layout);
        mask_layout.setBackgroundColor(getResources().getColor(ThemeManager.getCurrentThemeRes(this, R.color.backgroundColor)));

        mask_head = findViewById(R.id.mask_head);
        mask_head.setBackgroundColor(getResources().getColor(ThemeManager.getCurrentThemeRes(this, R.color.backgroundColor)));

        relativeLayout = findViewById(R.id.mask_relative);
        relativeLayout.setBackgroundColor(getResources().getColor(ThemeManager.getCurrentThemeRes(this, R.color.backgroundColor)));

        masked_word = findViewById(R.id.masked_word);
        masked_word.setTextColor(getResources().getColor(ThemeManager.getCurrentThemeRes(this, R.color.colorGrey)));


        String user_name = (String) ShareInfoUtil.getParam(this, ShareInfoUtil.LOGIN_DATA, "");

        mDatebase =new SearchDataBase(this, user_name+"_mask_record.db");
        mbtn_serarch = findViewById(R.id.btn_serarch);

        met_search = findViewById(R.id.et_search);
        met_search.setHintTextColor(getResources().getColor(ThemeManager.getCurrentThemeRes(this, R.color.colorGrey)));
        met_search.setTextColor(getResources().getColor(ThemeManager.getCurrentThemeRes(this, R.color.textColor)));

        exitMask = findViewById(R.id.exit_mask);
        exitMask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), MainActivity.class));
                finish();
            }
        });

        mtv_deleteAll = findViewById(R.id.tv_deleteAll);
        mtv_deleteAll.setTextColor(getResources().getColor(ThemeManager.getCurrentThemeRes(this, R.color.textColor)));
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
                if (searchContent.length() != 0){
                    boolean hasData = mDatebase.hasData(searchContent);
                    if (!hasData)
                    {
                        mDatebase.insertData(searchContent);

                    }
                    mSearchAdapter.updata(mDatebase.queryData(""));
                    sendData(mDatebase.queryData(""));
                }

            }
        });
    }

    void sendData(List<String> data)
    {
        String sending = "";
        for(String str:data)
            sending = sending + str + " ";
        ShareInfoUtil.setParam(this,ShareInfoUtil.MASK_WORDS,sending);
    }
}
