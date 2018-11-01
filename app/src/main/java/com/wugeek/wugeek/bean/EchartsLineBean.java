package com.wugeek.wugeek.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Data;

/**
 * Created by dell1 on 2017/5/28.
 */
@Data
public class EchartsLineBean {

    public ArrayList times;
    public ArrayList hours;

    public EchartsLineBean() {
        times = new ArrayList();
        hours = new ArrayList();
    }
}
