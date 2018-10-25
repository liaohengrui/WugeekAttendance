package com;

import java.util.ArrayList;


public class OnlineInfo {
    private int code;
    private String msg;
    private timeInfo timeInfo;

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

    public com.timeInfo getTimeInfo() {
        return timeInfo;
    }

    public void setTimeInfo(com.timeInfo timeInfo) {
        this.timeInfo = timeInfo;
    }
}


class timeInfo {
    private long date;
    private String sum;
    private ArrayList<times> times;

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

    public ArrayList<com.times> getTimes() {
        return times;
    }

    public void setTimes(ArrayList<com.times> times) {
        this.times = times;
    }
}


class times {
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