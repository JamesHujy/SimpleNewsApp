package com.example.simplenewsapp.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class NetCache
{
    private LocalCache mLocalCache;
    private MemoryCache mMemoryCache;
    private Bitmap mBitmap;

    public NetCache(LocalCache localCache, MemoryCache memoryCache)
    {
        mLocalCache = localCache;
        mMemoryCache = memoryCache;
    }

    public Bitmap getBitmapFromNet(String url)
    {
        new LoadPic().execute(url);
        return mBitmap;
    }

    class LoadPic extends AsyncTask<Object, Void, Bitmap>
    {
        String url;
        @Override
        public Bitmap doInBackground(Object[] params)
        {
            url = (String) params[0];
            mBitmap = getBitmap(url);
            return mBitmap;
        }

        @Override
        public void onProgressUpdate(Void[] values)
        {
            super.onProgressUpdate(values);
        }

        @Override
        public void onPostExecute(Bitmap result)
        {
            if(result != null)
            {
                mMemoryCache.setBitmapToMemory(url,result);
                mLocalCache.setBitmapToLocal(url,result);
            }
        }
    }

    private Bitmap getBitmap(String pic_url)
    {


        Bitmap bm = null;
        try {
            URL iconUrl = new URL(pic_url);
            URLConnection conn = iconUrl.openConnection();
            HttpURLConnection http = (HttpURLConnection) conn;

            int length = http.getContentLength();

            conn.connect();

            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is, length);
            bm = BitmapFactory.decodeStream(bis);
            System.out.println("In helper print..");
            System.out.println(bm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bm;
    }
}
