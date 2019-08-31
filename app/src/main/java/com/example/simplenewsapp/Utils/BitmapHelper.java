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

    public Bitmap getBitmapFromUrl(String url)
    {
        Bitmap bitmap;
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
