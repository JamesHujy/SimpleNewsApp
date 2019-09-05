package com.example.simplenewsapp.Utils;

import android.content.Context;
import android.graphics.Bitmap;

public class BitmapHelper
{
    private NetCache netCache;
    private LocalCache localCache;
    private MemoryCache memoryCache;

    public BitmapHelper(Context context)
    {
        memoryCache = new MemoryCache();
        localCache = new LocalCache(context);
        netCache = new NetCache(localCache,memoryCache);
    }

    private String refineString(String pic_url)
    {
        if (pic_url.equals("[]"))
            return "None";
        int urllength = pic_url.length();
        pic_url = pic_url.substring(1,urllength-1);
        pic_url = pic_url.split(",")[0];
        return pic_url;
    }

    public Bitmap getBitmapFromUrl(String url)
    {
        Bitmap bitmap;
        url = refineString(url);
        if (url.equals("None"))
            return null;

        bitmap = memoryCache.getBitmapFromMemory(url);
        if(bitmap != null)
            return bitmap;

        bitmap = localCache.getBitmapFromLocal(url);
        if(bitmap != null)
        {
            memoryCache.setBitmapToMemory(url,bitmap);
            return bitmap;
        }

        bitmap = netCache.getBitmapFromNet(url);
        return bitmap;
    }
}
