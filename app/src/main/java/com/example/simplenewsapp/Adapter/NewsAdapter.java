package com.example.simplenewsapp.Adapter;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.simplenewsapp.Utils.BitmapHelper;
import com.example.simplenewsapp.Utils.News;
import com.example.simplenewsapp.R;
import java.util.List;

public class NewsAdapter extends ArrayAdapter<News> implements View.OnClickListener
{
    private int resourceId;
    private CallBack mCallBack;

    public NewsAdapter(Context context, int textViewResourceId, List<News> objects, CallBack callBack) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        mCallBack = callBack;
    }


    public interface CallBack {
        public void click(View view);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        News news = getItem(position);
        View view;
        ViewHolder viewHolder;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.newsImg = view.findViewById(R.id.news_item_img);
            viewHolder.newsTitle = view.findViewById(R.id.news_item_title);
            viewHolder.newsDelete = view.findViewById(R.id.delete_item);
            viewHolder.newsAuthor = view.findViewById(R.id.news_item_author);
            viewHolder.newsDate = view.findViewById(R.id.news_item_date);
            view.setTag(viewHolder);

        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        System.out.println("In adapter.....");
        System.out.println(news.get_title());
        System.out.println(news.get_pic());

        Bitmap bitmap = news.get_pic();
        if(bitmap != null && bitmap.getHeight() < bitmap.getWidth())
        {
            System.out.println("NewsImgShow");
            viewHolder.newsImg.setImageBitmap(bitmap);
        }
        else{
            System.out.println("NewsImgGone");
            viewHolder.newsImg.setVisibility(View.GONE);
        }

        viewHolder.newsTitle.setText(news.get_title());
        if (news.get_click())
            viewHolder.newsTitle.setTextColor(Color.parseColor("#696969"));
        viewHolder.newsAuthor.setText(news.get_author());
        viewHolder.newsDate.setText(news.get_date());

        viewHolder.newsDelete.setTag(position);
        viewHolder.newsDelete.setOnClickListener(this);

        return view;
    }


    static class ViewHolder {
        ImageView newsImg;
        TextView newsTitle;
        TextView newsAuthor;
        TextView newsDate;
        ImageView newsDelete;
    }

    @Override
    public void onClick(View view) {
        mCallBack.click(view);
    }

    
}
