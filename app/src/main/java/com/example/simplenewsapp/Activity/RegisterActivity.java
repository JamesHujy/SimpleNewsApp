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
    //private MyDatabaseHelper dbHelper;

    private TextView save_user;
    private ImageView shangchuan_head;
    private EditText username, userpassword, repassword;
    private CheckBox checkBox;

    private static final int CHOSSE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //dbHelper = new (this, "UserDB.db", null, 1);

        save_user = findViewById(R.id.save_user);
        //shangchuan_head = findViewById(R.id.shangchuan_head);

        username = findViewById(R.id.register_username);
        userpassword = findViewById(R.id.register_password);
        repassword = findViewById(R.id.register_repassword);
        checkBox = findViewById(R.id.checkbox_tiaokuan);

        shangchuan_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(RegisterActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(RegisterActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbum();
                }
            }
        });

        save_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkBox.isChecked()){
                    //SQLiteDatabase db = dbHelper.getWritableDatabase();

                    String username_str = username.getText().toString();
                    String userpassword_str = userpassword.getText().toString();
                    String repassword_str = repassword.getText().toString();

                    if (userpassword_str.equals(repassword_str)) {
                        ContentValues values = new ContentValues();
                        //组装数据
                        values.put("name", username_str);
                        values.put("password", userpassword_str);

                        //db.insert("User", null, values);

                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "两次密码不一致，请重新输入", Toast.LENGTH_SHORT).show();
                    }
                    //db.close();
                }else {
                    Toast.makeText(RegisterActivity.this, "请勾选同意使用条款", Toast.LENGTH_SHORT).show();
                }
            }
        });



        //ApplicationUtil.getInstance().addActivity(this);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOSSE_PHOTO);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CHOSSE_PHOTO:
                if (resultCode == -1) {
                    String imgPath = Album.handleImageOnKitKat(this, data);
                    setHead(imgPath);
                }
                break;
            default:
                break;
        }
    }
    private void setHead(String imgPath) {
        if (imgPath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
            Bitmap round = Album.toRoundBitmap(bitmap);
            try {
                String path = getCacheDir().getPath();
                File file = new File(path,"user_head");
                round.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            shangchuan_head.setImageBitmap(round);
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }
}
