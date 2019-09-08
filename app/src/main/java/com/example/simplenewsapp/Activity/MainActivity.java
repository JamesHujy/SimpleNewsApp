package com.example.simplenewsapp.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import com.cheng.channel.Channel;

import com.example.simplenewsapp.Fragment.MainFragment;

import com.example.simplenewsapp.Fragment.RecommendFragment;
import com.example.simplenewsapp.Fragment.SearchFragment;

import com.example.simplenewsapp.R;
import com.example.simplenewsapp.Utils.ShareInfoUtil;
import com.example.simplenewsapp.Utils.ThemeManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.View;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.graphics.Color;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener{

    private LinearLayout ll_main, ll_video, ll_discover;

    private MainFragment mainFragment;
    //private VideoFragment videoFragment;
    //private MineFragment mineFragment;
    private RecommendFragment recommendFragment;
    private SearchFragment searchFragment;
    private LinearLayout linearLayout;
    //private DiscoverFragment discoverFragment;
    private List<Fragment> fragmentList = new ArrayList<>();
    private ImageView img_main, img_video, img_dicover;
    private TextView text_main, text_video, text_discover;
    private TextView user_name;


    private int theme = R.style.AppTheme;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /*
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);


        initView();

        initFragment();

        ll_main.setOnClickListener(this);
        ll_discover.setOnClickListener(this);
        ll_video.setOnClickListener(this);
        //ll_mine.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_nightmode) {
            ThemeManager.setThemeMode(ThemeManager.getThemeMode() == ThemeManager.ThemeMode.DAY
                    ? ThemeManager.ThemeMode.NIGHT : ThemeManager.ThemeMode.DAY);
            System.out.println("ChangeState");
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        else if (id == R.id.nav_collect) {
            Intent intent = new Intent(this,CollectionActivity.class);
            intent.putExtra("type","collect");
            startActivity(intent);

        }
        else if (id == R.id.nav_mask)
        {
            startActivity(new Intent(this,MaskActivity.class));
        }
        else if (id == R.id.nav_offlineread)
        {
            Intent intent = new Intent(this,CollectionActivity.class);
            intent.putExtra("type","history");
            startActivity(intent);

        }
        else if (id == R.id.nav_logout)
        {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }


/*      else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.layout_main:
            {
                if (mainFragment == null)
                    initFragment();
                addFragment(mainFragment);
                showFragment(mainFragment);
                text_main.setTextColor(Color.RED);
                text_video.setTextColor(Color.BLACK);
                text_discover.setTextColor(Color.BLACK);
                //text_mine.setTextColor(Color.BLACK);

                //img_mine.setImageResource(R.drawable.mine);
            }
            break;
            case R.id.layout_recommend: {
                if (recommendFragment == null) {
                    recommendFragment = new RecommendFragment();
                }
                addFragment(recommendFragment);
                showFragment(recommendFragment);
                text_video.setTextColor(Color.RED);
                text_main.setTextColor(Color.BLACK);
                text_discover.setTextColor(Color.BLACK);
                //text_mine.setTextColor(Color.BLACK);

                //img_mine.setImageResource(R.drawable.mine);
            }
            break;
            case R.id.layout_discover:
            {
                if(searchFragment == null)
                {
                    searchFragment = new SearchFragment();
                }
                addFragment(searchFragment);
                showFragment(searchFragment);

                text_discover.setTextColor(Color.RED);
                //text_mine.setTextColor(Color.BLACK);
                text_main.setTextColor(Color.BLACK);
                text_video.setTextColor(Color.BLACK);

            }
            default:
                break;
        }
    }

    void initView()
    {
        ll_main = findViewById(R.id.layout_main);
        ll_video = findViewById(R.id.layout_recommend);
        ll_discover = findViewById(R.id.layout_discover);


        //ll_mine = findViewById(R.id.layout_mine);

        text_main = findViewById(R.id.text_main);
        text_main.setTextColor(getResources().getColor(ThemeManager.getCurrentThemeRes(this, R.color.textColor)));
        text_video = findViewById(R.id.text_video);
        text_video.setTextColor(getResources().getColor(ThemeManager.getCurrentThemeRes(this, R.color.textColor)));
        //text_mine = findViewById(R.id.text_mine);
        text_discover = findViewById(R.id.text_discover);
        text_discover.setTextColor(getResources().getColor(ThemeManager.getCurrentThemeRes(this, R.color.textColor)));

        linearLayout = findViewById(R.id.column);
        linearLayout.setBackgroundColor(getResources().getColor(ThemeManager.getCurrentThemeRes(this, R.color.backgroundColor)));
        //img_mine = findViewById(R.id.img_mine);


        text_main.setTextColor(Color.RED);
    }

    private void initFragment()
    {
        String getChannel = (String) ShareInfoUtil.getParam(this, ShareInfoUtil.CHOSEN_CHANNEL, "");//注意一下
        String[] strings = getChannel.split(" ");
        ArrayList<String> listObj = new ArrayList<>();

        for(int i = 0; i < strings.length;i++)
            listObj.add(strings[i]);

        mainFragment = new MainFragment(listObj);
        addFragment(mainFragment);
        showFragment(mainFragment);



    }

    private void addFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!fragment.isAdded()) {
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).add(R.id.main_content, fragment).commit();
            fragmentList.add(fragment);
        }
    }

    private void showFragment(Fragment fragment) {
        for (Fragment frag : fragmentList) {
            if (frag != fragment) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.hide(frag).commit();
            }
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.show(fragment).commit();
    }
}
