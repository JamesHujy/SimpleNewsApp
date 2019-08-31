package com.example.myapplication;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class HtmlService {
    public static String getHtml(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("GET");

        conn.setConnectTimeout(5 * 1000);
        InputStream inStream;
        //InputStream inStream = conn.getInputStream();//通过输入流获取html数据
        int code = conn.getResponseCode();
        if (code == 200) {
            inStream = conn.getInputStream(); // 得到网络返回的输入流
        } else {
            inStream = conn.getErrorStream(); // 得到网络返回的输入流
        }

        byte[] data = readInputStream(inStream);//得到html的二进制数据
        String html = new String(data, "UTF-8");
        return html;
    }
    public static byte[] readInputStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while( (len=inStream.read(buffer)) != -1 ){
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }
    public static void parseDiffJson(String json, ArrayList<String> titlelist, ArrayList<String> contentlist, ArrayList<String> timelist, ArrayList<String> authorlist, ArrayList<String> pic_urllist) {
        //NewsDataBaseHelper dbHelper = new NewsDataBaseHelper(, "NewsDB,db", null, 1);
        synchronized(titlelist) {
            try {
                JSONObject news = new JSONObject(json);
                //Log.e("Json", json);
                String pageSize = news.getString("pageSize");
                Log.e("Json", pageSize);

                String total = news.getString("total");
                Log.e("Json", total);


                JSONArray data = news.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject jsonObject = (JSONObject) data.get(i);
                    //取出name
                    String image = jsonObject.getString("image");
                    Log.e("image", image);

                    pic_urllist.add(image);

                    String publishTime = jsonObject.getString("publishTime");
                    Log.e("publishTime", publishTime);
                    timelist.add(publishTime);
                    JSONArray keyWords = jsonObject.getJSONArray("keywords");
                    for (int j = 0; j < keyWords.length(); j++) {
                        JSONObject keyWord = (JSONObject) keyWords.get(j);
                        String score = keyWord.getString("score");
                        Log.e("score", score);
                        String word = keyWord.getString("word");
                        Log.e("word", word);
                    }
                    String language = jsonObject.getString("language");
                    Log.e("language", language);
                    String video = jsonObject.getString("video");
                    Log.e("video", video);
                    String title = jsonObject.getString("title");
                    Log.e("title", title);

                    titlelist.add(title);

                    JSONArray when = jsonObject.getJSONArray("when");

                    for (int j = 0; j < when.length(); j++) {
                        JSONObject keyWord = (JSONObject) when.get(j);
                        String score = keyWord.getString("score");
                        Log.e("score", score);
                        String word = keyWord.getString("word");
                        Log.e("word", word);

                    }

                    String content = jsonObject.getString("content");
                    Log.e("content", content);

                    contentlist.add(content);

                    String url = jsonObject.getString("url");

                    JSONArray persons = jsonObject.getJSONArray("persons");
                    for (int j = 0; j < persons.length(); j++) {
                        JSONObject person = (JSONObject) persons.get(j);
                        String count = person.getString("count");

                        String linkedURL = person.getString("linkedURL");

                        String mention = person.getString("mention");
                    }

                    String newsID = jsonObject.getString("newsID");
                    String crawlTime = jsonObject.getString("crawlTime");
                    JSONArray organizations = jsonObject.getJSONArray("organizations");
                    for (int j = 0; j < organizations.length(); j++) {
                        JSONObject person = (JSONObject) organizations.get(j);
                        String count = person.getString("count");

                        String linkedURL = person.getString("linkedURL");

                        String mention = person.getString("mention");
                    }

                    String publisher = jsonObject.getString("publisher");
                    authorlist.add(publisher);
                    JSONArray locations = jsonObject.getJSONArray("locations");
                    for (int j = 0; j < locations.length(); j++) {
                        JSONObject location = (JSONObject) locations.get(j);
                        //Log.e("test", "???");

                        if (!location.isNull("1ng")) {
                            String _1ng = location.getString("lng");
                            Log.e("1ng", _1ng);
                        } else {
                            //do something
                        }


                        Integer count = location.getInt("count");
                        Log.e("count", count.toString());
                        String linkedURL = location.getString("linkedURL");
                        Log.e("linkedURL", linkedURL);
                        if (!location.isNull("1at")) {
                            String _1at = location.getString("lat");
                            Log.e("1at", _1at);
                        } else {
                            //do something
                        }
                        String mention = location.getString("mention");
                    }
                    JSONArray where = jsonObject.getJSONArray("where");
                    for (int j = 0; j < where.length(); j++) {
                        JSONObject keyWord = (JSONObject) where.get(j);
                        String score = keyWord.getString("score");
                        Log.e("score", score);
                        String word = keyWord.getString("word");
                        Log.e("word", word);
                    }
                    String category = jsonObject.getString("category");

                    JSONArray who = jsonObject.getJSONArray("who");
                    for (int j = 0; j < who.length(); j++) {
                        JSONObject keyWord = (JSONObject) who.get(j);
                        String score = keyWord.getString("score");
                        Log.e("score", score);
                        String word = keyWord.getString("word");
                        Log.e("word", word);
                    }
                /*JSONArray jarray1 = jsonObject.getJSONArray("publicTime");
                JSONArray jarray2 = jsonObject.getJSONArray("data");
                Log.e("Json", jarray1.toString());
                Log.e("Json", jarray2.toString());*/
                }
            } catch (Exception e) {
                Log.e("error", "error in parseDiffJson");
                e.printStackTrace();
            }
            Log.e("JSON", "finish!");
        }

    }

}
