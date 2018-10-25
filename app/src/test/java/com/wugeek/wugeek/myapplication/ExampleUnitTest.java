package com.wugeek.wugeek.myapplication;


import com.alibaba.fastjson.JSON;
import com.wugeek.wugeek.myapplication.OnlineInfo;
import com.wugeek.wugeek.utils.TimeUtils;

import org.junit.Test;

public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        long endTime = TimeUtils.toTimestamp("2018-02-01") + 10800000L;
        System.out.println(endTime);
    }


}