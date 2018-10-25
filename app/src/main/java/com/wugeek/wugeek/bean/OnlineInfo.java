package com.wugeek.wugeek.bean;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.Data;

@Data
public class OnlineInfo implements Serializable {

    private int code;
    private String msg;
    private ArrayList<data> data;

    @Data
    public class data implements Serializable {
        private long date;
        private String sum;
        private ArrayList times;

    }

    @Data
    public class times implements Serializable {
        private String arrival_time;
        private String left_time;
        private String place;
    }

}





