package com.wugeek.wugeek.utils;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.haibin.calendarview.Calendar;
import com.wugeek.wugeek.bean.EchartsLineBean;
import com.wugeek.wugeek.bean.OnlineInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TimeQuery  {
    public List<OnlineInfo> onlineInfo;
    private List<com.haibin.calendarview.Calendar> schemes;
    private String token;
    OkHttpClient okHttpClient = new OkHttpClient.Builder().readTimeout(5, TimeUnit.SECONDS).build();
    private EchartsLineBean echartsLineBean;

    public TimeQuery(List<OnlineInfo> onlineInfo, List<Calendar> schemes, String token, EchartsLineBean echartsLineBean) {
        this.onlineInfo = onlineInfo;
        this.schemes = schemes;
        this.token = token;
        this.echartsLineBean = echartsLineBean;
    }

    public void run() {
        onlineInfo = new ArrayList<>();
        for (com.haibin.calendarview.Calendar calendar : schemes) {
            long endTime = TimeUtils.toTimestamp(calendar.getYear() + "-" + calendar.getMonth() + "-" + calendar.getDay()) + 10800000L;
            final String json = "{\"start_time\":" + TimeUtils.toTimestamp(calendar.getYear() + "-" + calendar.getMonth() + "-" + calendar.getDay()) +
                    ",\"end_time\":" + endTime + "}";
            //查询在线时长
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);

            final Request request = new Request.Builder()
                    .url("http://qiuluo.xin/attendanceapi/attendance/user/time/getTable")
                    .addHeader("token", token)
                    .post(requestBody)
                    .build();
            Call call = okHttpClient.newCall(request);
            try {
                Response response = null;
                response = call.execute();
                String responseBody = response.body().string();
                echartsLineBean.times.add(calendar.getYear() + "-" + calendar.getMonth() + "-" + calendar.getDay());
                com.wugeek.wugeek.bean.OnlineInfo onlineInfoItem = JSON.parseObject(responseBody, com.wugeek.wugeek.bean.OnlineInfo.class);
                echartsLineBean.hours.add(TimeUtils.toHours(Double.valueOf(onlineInfoItem.getData().get(0).getSum()) / 3600000));
                onlineInfo.add(onlineInfoItem);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
