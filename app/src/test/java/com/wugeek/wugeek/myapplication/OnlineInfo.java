package com.wugeek.wugeek.myapplication;

import java.util.ArrayList;


public class OnlineInfo {
    private int code;
    private String msg;
    private ArrayList<data> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ArrayList getData() {
        return data;
    }

    public void setData(ArrayList data) {
        this.data = data;
    }

    public class data {
        private long date;
        private String sum;
        private ArrayList<com.wugeek.wugeek.bean.OnlineInfo.times> times;

        public long getDate() {
            return date;
        }

        public void setDate(long date) {
            this.date = date;
        }

        public String getSum() {
            return sum;
        }

        public void setSum(String sum) {
            this.sum = sum;
        }

        public ArrayList<com.wugeek.wugeek.bean.OnlineInfo.times> getTimes() {
            return times;
        }

        public void setTimes(ArrayList<com.wugeek.wugeek.bean.OnlineInfo.times> times) {
            this.times = times;
        }
    }


    public class times {
        private String arrival_time;
        private String left_time;
        private String place;

        public String getArrival_time() {
            return arrival_time;
        }

        public void setArrival_time(String arrival_time) {
            this.arrival_time = arrival_time;
        }

        public String getLeft_time() {
            return left_time;
        }

        public void setLeft_time(String left_time) {
            this.left_time = left_time;
        }

        public String getPlace() {
            return place;
        }

        public void setPlace(String place) {
            this.place = place;
        }
    }

}
