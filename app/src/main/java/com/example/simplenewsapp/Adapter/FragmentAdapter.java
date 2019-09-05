package com.example.simplenewsapp.Adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.simplenewsapp.R;

import java.util.List;

public class FragmentAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentList;
    private List<String> titleList;

    public FragmentAdapter(FragmentManager fragmentManager, List<Fragment> fragmentList,
                           List<String> titleList)
    {
        super(fragmentManager);
        this.fragmentList = fragmentList;
        this.titleList = titleList;
    }

    @Override
    public Fragment getItem(int position)
    {
        return fragmentList.get(position);
    }

    @Override
    public int getCount()
    {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return titleList.get(position % titleList.size());
    }
}
