package com.example.simplenewsapp.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;


public class RegisterActivity extends Activity {

    private EditText user_name, fist_password, second_password;
    //private String username_str, password_str_first, password_str_second;
    private Button confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_register);
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
                else if(!judgeExit(username_str))
                {
                    insertUser(username_str, password_str_first);
                }
            }
        });
    }

    private boolean judgeExit(String username_str)
    {
        return false;
    }

    private void insertUser(String user_name, String password)
    {

    }

}
