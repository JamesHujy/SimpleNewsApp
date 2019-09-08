package com.example.simplenewsapp.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.simplenewsapp.R;
import com.example.simplenewsapp.Utils.Album;
import com.example.simplenewsapp.Utils.ShareInfoUtil;
import com.example.simplenewsapp.Utils.UserDataBaseHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;


public class RegisterActivity extends Activity {

    private EditText user_name, fist_password, second_password;
    //private String username_str, password_str_first, password_str_second;
    private Button confirm;
    private SQLiteOpenHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        confirm = findViewById(R.id.register_confirm);
        user_name = findViewById(R.id.register_username);
        fist_password = findViewById(R.id.register_password);
        second_password = findViewById(R.id.register_password_again);

        confirm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String username_str= user_name.getText().toString();
                String password_str_first = fist_password.getText().toString();
                String password_str_second = second_password.getText().toString();
                if (!password_str_first.equals(password_str_second))
                {
                    Toast.makeText(getBaseContext(), "两次密码不同，请重新输入", Toast.LENGTH_LONG).show();
                }
                else
                {
                    if (insertUser(username_str, password_str_first))
                    {
                        ShareInfoUtil.setParam(getBaseContext(), ShareInfoUtil.IS_LOGIN, true);
                        ShareInfoUtil.setParam(getBaseContext(), ShareInfoUtil.LOGIN_DATA, username_str);
                        Intent intent = new Intent(RegisterActivity.this, ChannelChooseActivity.class);
                        startActivity(intent);

                        finish();
                    }

                }
            }
        });
    }



    private boolean insertUser(String username, String password)
    {
        dbHelper = new UserDataBaseHelper(this, "User_Info.db", null, 1);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from User where name = ?", new String[]{username});
        if (cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put("name", username);
            values.put("password", password);
            db.insert("User", null, values);
            Toast.makeText(getBaseContext(), "用户名注册成功！", Toast.LENGTH_LONG).show();
            return true;

        }
        else {
            Toast.makeText(getBaseContext(), "用户名已存在！", Toast.LENGTH_LONG).show();
            return false;
        }
    }

}
