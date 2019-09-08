package com.example.simplenewsapp.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.cheng.channel.Channel;
import com.cheng.channel.ChannelView;
import com.example.simplenewsapp.R;
import com.example.simplenewsapp.Utils.ShareInfoUtil;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
////
public class ChannelChooseActivity extends Activity implements ChannelView.OnChannelListener
{
    private ChannelView channelView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_choose);

        channelView = findViewById(R.id.channelView);
        init();
    }

    void init()
    {
        String[] myChannel = {};
        String[] recommendChannel1 = { "要闻","国内","国际","科技", "娱乐", "体育", "教育","财经"};
        String[] recommendChannel2 = {"汽车","文化", "军事", "综艺", "美食", "NBA", "电影", "健康", "足球", "网球", "电竞","电视剧"};
        String[] recommendChannel3 = { "詹姆斯", "泰勒", "梅西", "C罗", "费德勒", "蔡徐坤", "洛杉矶湖人", "男篮世界杯"};


        List<Channel> myChannelList = new ArrayList<>();
        List<Channel> recommendChannelList1 = new ArrayList<>();
        List<Channel> recommendChannelList2 = new ArrayList<>();
        List<Channel> recommendChannelList3 = new ArrayList<>();

        for (int i = 0; i < myChannel.length; i++) {
            String aMyChannel = myChannel[i];
            Channel channel;
            if (i > 2 && i < 6) {
                //可设置频道归属板块（channelBelong），当前设置此频道归属于第二板块，当删除该频道时该频道将回到第二板块
                channel = new Channel(aMyChannel, 2, i);
            } else if (i > 7 && i < 10) {
                //可设置频道归属板块（channelBelong），当前设置此频道归属于第三板块，当删除该频道时该频道将回到第三板块中
                channel = new Channel(aMyChannel, 3, i);
            } else {
                channel = new Channel(aMyChannel, (Object) i);
            }
            myChannelList.add(channel);
        }

        for (String aMyChannel : recommendChannel1) {
            Channel channel = new Channel(aMyChannel);
            recommendChannelList1.add(channel);
        }

        for (String aMyChannel : recommendChannel2) {
            Channel channel = new Channel(aMyChannel);
            recommendChannelList2.add(channel);
        }

        for (String aMyChannel : recommendChannel3) {
            Channel channel = new Channel(aMyChannel);
            recommendChannelList3.add(channel);
        }

        channelView.setChannelFixedCount(3);
        channelView.addPlate("推荐频道", recommendChannelList1);
        channelView.addPlate("其他类别", recommendChannelList2);
        channelView.addPlate("猜你感兴趣", recommendChannelList3);
        channelView.inflateData();
        channelView.setOnChannelItemClickListener(this);
    }

    @Override
    public void channelItemClick(int position, Channel channel)
    {

    }

    @Override
    public void channelEditFinish(List<Channel> channelList)
    {
        Intent intent = new Intent(this, MainActivity.class);
        String sending = "";
        List<Channel> channel = channelView.getMyChannel();

        for(Channel channelitem:channel)
        {
            sending = sending + channelitem.getChannelName() + " ";
        }
        ShareInfoUtil.setParam(ChannelChooseActivity.this, ShareInfoUtil.CHOSEN_CHANNEL, sending);
        startActivity(intent);
        finish();
    }

    @Override
    public void channelEditStart()
    {

    }
}
