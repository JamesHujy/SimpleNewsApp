package com.example.simplenewsapp.Utils;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

import java.util.Iterator;
import java.util.Map;

public class MemoryCache
{
    private LruCache<String, Bitmap> mMemoryCache;
    private static final String TAG = "MemoryCacheUtils";
    public MemoryCache()
    {
        long maxMemory = Runtime.getRuntime().maxMemory() / 8;
        mMemoryCache = new LruCache<String, Bitmap>((int) maxMemory)
        {
            @Override
            public int sizeOf(String key, Bitmap value)
            {
                int byteCOunt = value.getByteCount();
                return byteCOunt;
            }
        };
    }

    public Bitmap getBitmapFromMemory(String url) {
        //Bitmap bitmap = mMemoryCache.get(url);//1.强引用方法
            /*2.弱引用方法
            SoftReference<Bitmap> bitmapSoftReference = mMemoryCache.get(url);
            if (bitmapSoftReference != null) {
                Bitmap bitmap = bitmapSoftReference.get();
                return bitmap;
            }
            */
        //Log.d(TAG, "getBitmapFromMemory: " + mMemoryCache.createCount());
        Bitmap bitmap = mMemoryCache.get(url);

        Map<String, Bitmap> snapshot = mMemoryCache.snapshot();
        Log.d(TAG, "getBitmapFromMemory-->url:" + url + "******size:--->" + snapshot.size());
        Iterator<Map.Entry<String, Bitmap>> it = snapshot.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Bitmap> entry = it.next();
            Log.d("Map", entry.getKey() + "******" + entry.getValue());
        }
        return bitmap;
    }

    public void setBitmapToMemory(String url, Bitmap bitmap) {
        //2. 弱引用方法
        //mMemoryCache.put(url, new SoftReference<>(bitmap));

        //1.强引用方法
        try {

            mMemoryCache.put(url, bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "setBitmapToMemory: " + url + "***------->" + bitmap.getByteCount());
    }
}
