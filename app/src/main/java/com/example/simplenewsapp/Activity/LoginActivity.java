package com.example.simplenewsapp.Activity;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.simplenewsapp.R;
import com.example.simplenewsapp.Utils.ShareInfoUtil;

public class LoginActivity extends Activity
{
    private Button loginButton;
    private EditText username, password;
    private String username_str, password_str;
    private ImageView login_head;

    private SQLiteOpenHelper dbHelper;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();

        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                username_str= username.getText().toString();
                password_str = password.getText().toString();
                judgePassword(username_str, password_str);
            }
        });
    }

    void initView()
    {
        loginButton = findViewById(R.id.login_button);
        username = findViewById(R.id.login_username);
        password = findViewById(R.id.login_password);
        login_head = findViewById(R.id.login_head);
    }


    void judgePassword(String username_str, String password_str)
    {
        Intent intent = new Intent(LoginActivity.this, ChannelChooseActivity.class);
        startActivity(intent);
        finish();
        ShareInfoUtil.setParam(LoginActivity.this, ShareInfoUtil.IS_LOGIN, true);
        ShareInfoUtil.setParam(LoginActivity.this, ShareInfoUtil.LOGIN_DATA, username_str);
    }


}
