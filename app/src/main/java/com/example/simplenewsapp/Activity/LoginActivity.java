package com.example.simplenewsapp.Activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.simplenewsapp.R;
import com.example.simplenewsapp.Utils.ShareInfoUtil;
import com.example.simplenewsapp.Utils.UserDataBaseHelper;

public class LoginActivity extends Activity
{
    private Button loginButton, registerButton;
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
        dbHelper = new UserDataBaseHelper(this, "User_Info.db", null, 1);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    void initView()
    {
        loginButton = findViewById(R.id.login_button);
        registerButton = findViewById(R.id.login_register);
        username = findViewById(R.id.login_username);
        password = findViewById(R.id.login_password);
        login_head = findViewById(R.id.login_head);
    }


    void judgePassword(String username_str, String password_str)
    {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from User where name = ?", new String[]{username_str});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String storePassword = cursor.getString(cursor.getColumnIndex("password"));
            if (storePassword.equals(password_str)) {
                Toast.makeText(getBaseContext(), "登录成功！", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(LoginActivity.this, ChannelChooseActivity.class);
                startActivity(intent);

                finish();
                ShareInfoUtil.setParam(LoginActivity.this, ShareInfoUtil.IS_LOGIN, true);
                ShareInfoUtil.setParam(LoginActivity.this, ShareInfoUtil.LOGIN_DATA, username_str);
            }
            else {
                Toast.makeText(getBaseContext(), "密码不符！", Toast.LENGTH_LONG).show();
            }

        }
        else {
            Toast.makeText(getBaseContext(), "用户名不存在！", Toast.LENGTH_LONG).show();
        }

    }


}
