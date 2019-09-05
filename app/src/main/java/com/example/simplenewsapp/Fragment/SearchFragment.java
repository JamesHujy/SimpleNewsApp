package com.example.simplenewsapp.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplenewsapp.R;
import com.example.simplenewsapp.Utils.BaseSearchAdapter;
import com.example.simplenewsapp.Utils.SearchAdapter;
import com.example.simplenewsapp.Utils.SearchDataBase;

public class SearchFragment extends Fragment
{
    private Button mbtn_serarch;
    private EditText met_search;
    private RecyclerView mRecyclerView;
    private TextView mtv_deleteAll;
    private View view;
    private SearchAdapter mAdapter;
    private SearchDataBase mDatebase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){

        view = inflater.inflate(R.layout.fragment_search, container, false);
        initViews();
        return view;
    }

    private void initViews() {
        mDatebase =new SearchDataBase(this.getContext());
        mbtn_serarch = view.findViewById(R.id.btn_serarch);
        met_search = view.findViewById(R.id.et_search);

        mtv_deleteAll = view.findViewById(R.id.tv_deleteAll);
        mtv_deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatebase.deleteData();
                mAdapter.updata(mDatebase.queryData(""));
            }
        });
        mRecyclerView = (RecyclerView) view.findViewById(R.id.mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter =new SearchAdapter(mDatebase.queryData(""),this.getContext());
        mAdapter.setRvItemOnclickListener(new BaseSearchAdapter.RvItemOnclickListener(){
            @Override
            public void RvItemOnclick(int position) {
                mDatebase.delete(mDatebase.queryData("").get(position));

                mAdapter.updata(mDatebase.queryData(""));
            }
        });
        mRecyclerView.setAdapter(mAdapter);
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
                        getNews(searchContent);
                    }
                    //
                    mAdapter.updata(mDatebase.queryData(""));
                }else {
                    Toast.makeText(getContext(), "请输入内容", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void getNews(String content)
    {

    }

    private void showNews(String content)
    {

    }

}
