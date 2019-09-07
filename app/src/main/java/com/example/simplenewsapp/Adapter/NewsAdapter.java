package com.example.simplenewsapp.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.example.simplenewsapp.Utils.LoadListView;
import com.example.simplenewsapp.Utils.News;
import com.example.simplenewsapp.R;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class NewsAdapter extends ArrayAdapter<News> implements View.OnClickListener
{
    private int resourceId;
    private CallBack mCallBack;
    boolean isConnected = true;
    private Bitmap bitmap;

    public NewsAdapter(Context context, int textViewResourceId, List<News> objects, CallBack callBack) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        mCallBack = callBack;
    }


    public interface CallBack {
        public void click(View view);
    }



    private class LoadPic extends AsyncTask<Object, Void, Bitmap>
    {
        String url;
        ImageView imageView;
        String title;

        private String refineString(String pic_url)
        {
            if (pic_url.equals("[]"))
                return "None";
            int urllength = pic_url.length();
            pic_url = pic_url.substring(1,urllength-1);
            pic_url = pic_url.split(",")[0];
            return pic_url;
        }

        private Bitmap getBitmap(String url)
        {
            url = refineString(url);
            Bitmap bm = null;
            try {
                URL iconUrl = new URL(url);
                URLConnection conn = iconUrl.openConnection();
                HttpURLConnection http = (HttpURLConnection) conn;

                int length = http.getContentLength();

                conn.connect();

                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is, length);
                bm = BitmapFactory.decodeStream(bis);
                System.out.println(bm);
                is.close();
                bis.close();
            }
            catch (Exception e)
            {

                System.out.println(title+"Load picture excepition !!!!!!!!!!!!!!!!");
                System.out.println(e);
            }
            return bm;
        }

        LoadPic(String url, ImageView imageView, String title)
        {
            this.url = url;
            this.title = title;
            this.imageView = imageView;
        }
        @Override
        public Bitmap doInBackground(Object[] params)
        {
            url = (String)params[0];
            Bitmap bitmap = getBitmap(url);
            return bitmap;
        }

        @Override
        public void onProgressUpdate(Void[] values)
        {
            super.onProgressUpdate(values);
        }

        @Override
        public void onPostExecute(Bitmap result)
        {
            if(result != null && result.getHeight() < result.getWidth())
            {
                System.out.println(title+"NewsImgShow");
                imageView.setImageBitmap(result);

            }
            else{
                System.out.println(title+"NewsImgGone");
                imageView.setVisibility(View.GONE);
            }
        }
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

        if (isConnected)
        {
            LoadPic loadPic = new LoadPic(news.get_picurl(),viewHolder.newsImg,news.get_title());
            loadPic.execute(news.get_picurl());
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


    public void setOffline()
    {
        isConnected = false;
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
