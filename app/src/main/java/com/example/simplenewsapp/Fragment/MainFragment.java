package com.example.simplenewsapp.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.simplenewsapp.Activity.ChannelChooseActivity;
import com.example.simplenewsapp.Activity.MainActivity;
import com.example.simplenewsapp.Adapter.FragmentAdapter;
import com.example.simplenewsapp.R;
import com.example.simplenewsapp.Utils.ThemeManager;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment implements View.OnClickListener
{
    private View view;

    private TabLayout tabLayout;
    private HorizontalScrollView scrollView;
    private ViewPager viewPager;
    private ImageView expandButton;

    private List<String> titleList;
    private List<ColumnFragment> fragmentList;

    private FragmentAdapter fragmentAdapter;

    public MainFragment(){}


    public MainFragment(List<String> channelList)
    {
        titleList = new ArrayList<>();
        fragmentList = new ArrayList<>();
        titleList.clear();
        if(channelList != null)
            titleList.addAll(channelList);
        else
            setDefaultTypeList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_main,container,false);
        initView();
        //setDefaultTypeList();
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
        expandButton = view.findViewById(R.id.expand);
        expandButton.setOnClickListener(this);
        tabLayout.setBackgroundColor(getResources().getColor(ThemeManager.getCurrentThemeRes(getContext(), R.color.backgroundColor)));
        tabLayout.setTabTextColors(getResources().getColor(ThemeManager.getCurrentThemeRes(getContext(), R.color.textColor)),
                getResources().getColor(ThemeManager.getCurrentThemeRes(getContext(), R.color.colorRed)));
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

    @Override
    public void onClick(View view)
    {
        switch (view.getId()){
            case R.id.expand:
            {
                System.out.println("clicked in fragment!");
                Intent intent = new Intent(this.getContext(), ChannelChooseActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
            break;
            default:
                break;
        }

    }


}
