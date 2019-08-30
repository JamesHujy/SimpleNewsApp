package com.example.simplenewsapp.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.simplenewsapp.Activity.LoginActivity;
import com.example.simplenewsapp.Activity.NigntModeActivity;
import com.example.simplenewsapp.Activity.OfflineReadActivity;
import com.example.simplenewsapp.Activity.ReviewActivity;
import com.example.simplenewsapp.Activity.SecurityActivity;
import com.example.simplenewsapp.Activity.CollectionActivity;
import com.example.simplenewsapp.R;

public class MineFragment extends Fragment implements View.OnClickListener
{
    private View view;

    private TextView exit_login, collection, user_name, account_security,
                     reading_time, night_mode, offline_read, review;
    private ImageView my_head;

    private static final int CHOOSR_PHOTO = 1;

    public MineFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflator.inflate(R.layout.fragment_mine,container,false);
        initView();

        exit_login.setOnClickListener(this);
        collection.setOnClickListener(this);
        my_head.setOnClickListener(this);
        account_security.setOnClickListener(this);
        night_mode.setOnClickListener(this);
        offline_read.setOnClickListener(this);
        review.setOnClickListener(this);

        return view;
    }

    void initView()
    {
        user_name = view.findViewById(R.id.mine_user_name);
        exit_login = view.findViewById(R.id.exit_login);
        account_security = view.findViewById(R.id.account_security);
        collection = view.findViewById(R.id.collection);
        my_head = view.findViewById(R.id.my_head);
        reading_time = view.findViewById(R.id.reading_time);
        night_mode = view.findViewById(R.id.night_mode);
        offline_read = view.findViewById(R.id.offline_read);
        review = view.findViewById(R.id.review);
    }

    @Override
    public void onClick(View view)
    {
        switch(view.getId())
        {
            case R.id.account_security:
            {
                Intent intent = new Intent(getContext(), SecurityActivity.class);
                startActivity(intent);
            }
            break;

            case R.id.exit_login:
            {
                exit_login();

                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
            break;

            case R.id.collection:
            {
                Intent intent = new Intent(getContext(), CollectionActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.night_mode:
            {
                Intent intent = new Intent(getContext(), NigntModeActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.review:
            {
                Intent intent = new Intent(getContext(), ReviewActivity.class);
                startActivity(intent);
            }
            break;

            case R.id.offline_read:
            {
                Intent intent = new Intent(getContext(), OfflineReadActivity.class);
                startActivity(intent);
            }
            break;
            default:
                break;
        }
    }

    void exit_login()
    {

    }
}
