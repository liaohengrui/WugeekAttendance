package com.wugeek.wugeek;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qmuiteam.qmui.widget.QMUILoadingView;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;
import com.wugeek.wugeek.R;
import com.wugeek.wugeek.base.TimeAdapter;
import com.wugeek.wugeek.bean.EchartsLineBean;
import com.wugeek.wugeek.component.RecycleViewDivider;
import com.wugeek.wugeek.utils.TimeUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


@SuppressWarnings("ALL")
public class OnlineInfo extends AppCompatActivity {
    List<com.haibin.calendarview.Calendar> schemes = new ArrayList<>();
    private Handler handler = null;
    com.wugeek.wugeek.bean.OnlineInfo onlineInfo;
    private static final String TAG = "OnlineInfo";
    // 用来计算返回键的点击间隔时间
    private long exitTime = 0;
    private EchartsLineBean echartsLineBean = new EchartsLineBean();
    WebView webView;
    RecyclerView recyclerView;

    TimeAdapter timeAdapter;

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
                echartsLineBean = new EchartsLineBean();
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        OkHttpClient okHttpClient = new OkHttpClient.Builder().readTimeout(5, TimeUnit.SECONDS).build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (com.haibin.calendarview.Calendar calendar : schemes) {
                    final String token = getSharedPreferences("user", MODE_PRIVATE).getString("token", "");
                    long endTime = TimeUtils.toTimestamp(calendar.getYear() + "-" + calendar.getMonth() + "-" + calendar.getDay()) + 10800000L;
                    final String json = "{\"start_time\":" + TimeUtils.toTimestamp(calendar.getYear() + "-" + calendar.getMonth() + "-" + calendar.getDay()) +
                            ",\"end_time\":" + endTime + "}";
                    //查询在线时长
                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);

                    final Request request = new Request.Builder()
                            .url("http://api.wugeek.vczyh.com/attendance/user/time/getTable")
                            .addHeader("token", token)
                            .post(requestBody)
                            .build();
                    Log.d(TAG, json);
                    Call call = okHttpClient.newCall(request);
                    try {
                        Response response = call.execute();
                        String responseBody = response.body().string();
                        echartsLineBean.times.add(calendar.getYear() + "-" + calendar.getMonth() + "-" + calendar.getDay());
                        onlineInfo = JSON.parseObject(responseBody, com.wugeek.wugeek.bean.OnlineInfo.class);
                        echartsLineBean.hours.add(TimeUtils.toHours(Double.valueOf(onlineInfo.getData().get(0).getSum())/3600000));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView = findViewById(R.id.time_scroll);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(OnlineInfo.this);
                        recyclerView.setLayoutManager(layoutManager);
                        timeAdapter = new TimeAdapter(echartsLineBean);
                        recyclerView.setAdapter(timeAdapter);
                        RecycleViewDivider recycleViewDivider = new RecycleViewDivider(OnlineInfo.this, LinearLayoutManager.HORIZONTAL, 1, ContextCompat.getColor(OnlineInfo.this, R.color.bg_toolbar));
                        recyclerView.addItemDecoration(recycleViewDivider);

                        webView = findViewById(R.id.chartshow_wb);
                        webView.getSettings().setAllowFileAccess(true);
                        webView.getSettings().setJavaScriptEnabled(true);
                        webView.loadUrl("file:///android_asset/myechart.html");
                        // disable scroll on touch
                        webView.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                return (event.getAction() == MotionEvent.ACTION_MOVE);
                            }
                        });
                        webView.setWebViewClient(new WebViewClient() {
                            @Override
                            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                view.loadUrl(url);
                                return super.shouldOverrideUrlLoading(view, url);
                            }

                            @Override
                            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                                super.onPageStarted(view, url, favicon);
                            }

                            @Override
                            public void onPageFinished(WebView view, String url) {
                                //最好在这里调用js代码 以免网页未加载完成
                                webView.loadUrl("javascript:createLineChart(" + JSONObject.toJSONString(echartsLineBean) + ");");
                            }
                        });

                    }
                });
            }
        }).start();


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