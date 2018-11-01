package com.wugeek.wugeek;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUIPullRefreshLayout;
import com.wugeek.wugeek.bean.OnlineInfo;
import com.wugeek.wugeek.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import butterknife.BindView;

public class DayOnlineInfo extends Activity {
    private QMUIPullRefreshLayout mPullRefreshLayout;
    private ListView mListView;
    private static final String TAG = "DayOnlineInfo";
    OnlineInfo onlineInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_online_info);
        mPullRefreshLayout = findViewById(R.id.pull_to_refresh);
        mListView = findViewById(R.id.listview);
        onlineInfo = (OnlineInfo) getIntent().getSerializableExtra("data");
        Log.d(TAG, "onCreate: " + mListView + mPullRefreshLayout);
        initData();

    }

    @SuppressLint("ResourceType")
    private void initData() {
        List<String> data = new ArrayList<>();
        Log.d(TAG, "initData: kkkk" + onlineInfo);
        if (!onlineInfo.getData().get(0).getTimes().isEmpty()) {
            for (Object times : onlineInfo.getData().get(0).getTimes()) {
                Log.d(TAG, "initData: ------------------------------------");
                data.add("签到时间" + TimeUtils.toChinaTime(((JSONObject) times).getString("arrival_time")));
                data.add("离开时间" + TimeUtils.toChinaTime(((JSONObject) times).getString("left_time")));
            }
        } else {
            Log.d(TAG, "initData: 空数据");
            ((ImageView) findViewById(R.id.kong_image)).setImageResource(R.drawable.kongshuju);
        }
        mListView.setAdapter(new ArrayAdapter<>(DayOnlineInfo.this, android.R.layout.simple_list_item_1, data));
        mPullRefreshLayout.setOnPullListener(new QMUIPullRefreshLayout.OnPullListener() {
            @Override
            public void onMoveTarget(int offset) {
            }

            @Override
            public void onMoveRefreshView(int offset) {
            }

            @Override
            public void onRefresh() {
                mPullRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullRefreshLayout.finishRefresh();
                    }
                }, 2000);
            }
        });


    }
}

