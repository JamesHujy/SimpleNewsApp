package com.example.simplenewsapp.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.simplenewsapp.Adapter.FragmentAdapter;
import com.example.simplenewsapp.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class DiscoverFragment extends Fragment implements View.OnClickListener
{
    private View view;
    private TabLayout tabLayout;
    private List<String> titleList;
    private ViewPager viewPager;

    private FragmentAdapter fragmentAdapter;
    private List<Fragment> fragmentList;

    public DiscoverFragment()
    {
        titleList = new ArrayList<>();
        titleList.add("推荐");
        titleList.add("发布");
        titleList.add("搜索");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_discover, container, false);
        init();
        return view;
    }

    void init()
    {
        tabLayout = view.findViewById(R.id.discover_layout);
        viewPager = view.findViewById(R.id.discover_pager);
        RecommendFragment recommendFragment = new RecommendFragment();
        SearchFragment searchFragment = new SearchFragment();
        SearchFragment searchFragment1 = new SearchFragment();

        fragmentList = new ArrayList<>();
        fragmentList.add(recommendFragment);
        fragmentList.add(searchFragment);
        fragmentList.add(searchFragment1);

        fragmentAdapter = new FragmentAdapter(getActivity().getSupportFragmentManager(), fragmentList, titleList);
        viewPager.setAdapter(fragmentAdapter);

        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onClick(View view)
    {

    }
}
