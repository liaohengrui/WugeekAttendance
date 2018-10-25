package com.wugeek.wugeek;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qmuiteam.qmui.widget.QMUILoadingView;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;
import com.wugeek.wugeek.R;
import com.wugeek.wugeek.utils.TimeUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class OnlineInfo extends AppCompatActivity {
    List<com.haibin.calendarview.Calendar> schemes = new ArrayList<>();
    private Handler handler = null;
    com.wugeek.wugeek.bean.OnlineInfo onlineInfo;
    private static final String TAG = "OnlineInfo";
    // 用来计算返回键的点击间隔时间
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        setContentView(R.layout.activity_online_info);
        initButton();
    }

    public void initButton() {

        ImageView imageView = findViewById(R.id.ib_calendar);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OnlineInfo.this, Calendar.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        final QMUIGroupListView mGroupListView = findViewById(R.id.groupListView);
        mGroupListView.removeAllViews();
        for (com.haibin.calendarview.Calendar calendar : schemes) {
            final QMUICommonListItemView normalItem = mGroupListView.createItemView(calendar.getYear() + "-" + calendar.getMonth() + "-" + calendar.getDay());
            final String token = getSharedPreferences("user", MODE_PRIVATE).getString("token", "");
            long endTime = TimeUtils.toTimestamp(calendar.getYear() + "-" + calendar.getMonth() + "-" + calendar.getDay()) + 10800000L;
            final String json = "{\"start_time\":" + TimeUtils.toTimestamp(calendar.getYear() + "-" + calendar.getMonth() + "-" + calendar.getDay()) +
                    ",\"end_time\":" + endTime + "}";

            final Runnable runnableUi = new Runnable() {
                @Override
                public void run() {
                    normalItem.setDetailText("当日时长： " + onlineInfo.getData().get(0).getSum() + "小时");
                }
            };
            //查询在线时长

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)//设置连接超时时间
                    .readTimeout(20, TimeUnit.SECONDS)//设置读取超时时间
                    .build();
            final Request request = new Request.Builder()
                    .url("http://api.wugeek.vczyh.com/attendance/user/time/getTable")
                    .addHeader("token", token)
                    .post(requestBody)//默认就是GET请求，可以不写
                    .build();
            Log.d(TAG, json);
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d(TAG, "onFailure:sssssss " + e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String token = response.body().string();
                    Log.d(TAG, "onResponse: " + token);
                    onlineInfo = JSON.parseObject(token, com.wugeek.wugeek.bean.OnlineInfo.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            normalItem.setDetailText("当日时长： " + JSON.parseObject(token, com.wugeek.wugeek.bean.OnlineInfo.class).getData().get(0).getSum() + "小时");
                            View.OnClickListener onClickListener = new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (v instanceof QMUICommonListItemView) {
                                        CharSequence text = ((QMUICommonListItemView) v).getText();
                                        Intent intent = new Intent(OnlineInfo.this, DayOnlineInfo.class);
                                        intent.putExtra("data", (Serializable) JSON.parseObject(token, com.wugeek.wugeek.bean.OnlineInfo.class));
                                        startActivity(intent);
                                    }
                                }
                            };//默认文字在左边   自定义加载框按钮

                            QMUIGroupListView.newSection(OnlineInfo.this)
                                    .setTitle("考勤时间概况")
                                    .addItemView(normalItem, onClickListener)
                                    .addTo(mGroupListView);
                        }
                    });

                }
            });



        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        schemes = (List<com.haibin.calendarview.Calendar>) data.getSerializableExtra("time_data");

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                //弹出提示，可以有多种方式
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}