package com.example.simplenewsapp.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.simplenewsapp.Adapter.FragmentAdapter;
import com.example.simplenewsapp.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment
{
    private View view;

    private TabLayout tabLayout;
    private HorizontalScrollView scrollView;
    private ViewPager viewPager;

    private List<String> titleList;
    private List<Fragment> fragmentList;

    private FragmentAdapter fragmentAdapter;

    private ColumnFragment edu_fragment;
    private ColumnFragment tech_fragment;
    private ColumnFragment health_fragment;
    private ColumnFragment ente_fragment;
    private ColumnFragment sport_fragment;
    private ColumnFragment cul_fragment;
    private ColumnFragment fin_fragment;
    private ColumnFragment sco_fragment;
    private ColumnFragment car_fragment;
    private ColumnFragment mil_fragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_main,container,false);
        initView();
        setDefaultTypeList();
        initFragment();

        return view;
    }

    private void setDefaultTypeList()
    {
        titleList.add("要闻");
        titleList.add("社会");
        titleList.add("财经");
        titleList.add("科技");
        titleList.add("文化");
        titleList.add("健康");
    }
    private void initView()
    {
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);

        titleList = new ArrayList<>();
        fragmentList = new ArrayList<>();
    }

    private void initFragment()
    {
        System.out.println("Init begin!!!!!!!!!!!!!!!!!!!!");

        for(String str:titleList)
        {
            System.out.println(str);
            initOneFragement(str);
        }


        System.out.println("Init done!!!!!!!!!!!!!!!!!!!!");
        fragmentAdapter = new FragmentAdapter(
                getActivity().getSupportFragmentManager(), fragmentList, titleList);
        viewPager.setAdapter(fragmentAdapter);

        tabLayout.setupWithViewPager(viewPager);
    }

    void initOneFragement(String type)
    {
        ColumnFragment oneFrament = new ColumnFragment(type);
        fragmentList.add(oneFrament);

    }



}
