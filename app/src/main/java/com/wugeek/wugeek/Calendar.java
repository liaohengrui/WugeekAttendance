package com.wugeek.wugeek;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haibin.calendarview.CalendarView;
import com.wugeek.wugeek.calendarviewproject.BaseActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Calendar extends BaseActivity implements
        CalendarView.OnDateSelectedListener,
        CalendarView.OnDateChangeListener,
        View.OnClickListener {

    private static final String TAG = "Calendar";

    TextView mTextMonthDay;

    TextView mTextYear;

    TextView mTextLunar;

    TextView mTextCurrentDay;

    CalendarView mCalendarView;

    RelativeLayout mRelativeTool;
    private int mYear;

    List<com.haibin.calendarview.Calendar> schemes = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_calendar;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initView() {
        setStatusBarDarkMode();
        mTextMonthDay = (TextView) findViewById(R.id.tv_month_day);
        mTextYear = (TextView) findViewById(R.id.tv_year);
        mTextLunar = (TextView) findViewById(R.id.tv_lunar);
        mRelativeTool = (RelativeLayout) findViewById(R.id.rl_tool);
        mCalendarView = (CalendarView) findViewById(R.id.calendarView);
        mTextCurrentDay = (TextView) findViewById(R.id.tv_current_day);
        mTextMonthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarView.showSelectLayout(mYear);
                mTextLunar.setVisibility(View.GONE);
                mTextYear.setVisibility(View.GONE);
                mTextMonthDay.setText(String.valueOf(mYear));
            }
        });
        findViewById(R.id.fl_current).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarView.scrollToCurrent();
            }
        });

        mCalendarView.setOnDateChangeListener(this);
        mCalendarView.setOnDateSelectedListener(this);
        mTextYear.setText(String.valueOf(mCalendarView.getCurYear()));
        mYear = mCalendarView.getCurYear();
        mTextMonthDay.setText(mCalendarView.getCurMonth() + "月" + mCalendarView.getCurDay() + "日");
        mTextLunar.setText("今日");
        mTextCurrentDay.setText(String.valueOf(mCalendarView.getCurDay()));
    }

    @Override
    protected void initData() {

        int year = mCalendarView.getCurYear();
        int month = mCalendarView.getCurMonth();
        int day = mCalendarView.getCurDay();
        schemes.add(getSchemeCalendar(year, month, day, 0xFFaacc44, "查"));
        mCalendarView.setSchemeDate(schemes);
        findViewById(R.id.ll_simple).setOnClickListener(this);

    }


    private com.haibin.calendarview.Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
        com.haibin.calendarview.Calendar calendar = new com.haibin.calendarview.Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text);
        return calendar;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onDateChange(com.haibin.calendarview.Calendar calendar) {
        mTextLunar.setVisibility(View.VISIBLE);
        mTextYear.setVisibility(View.VISIBLE);
        mTextMonthDay.setText(calendar.getMonth() + "月" + calendar.getDay() + "日");
        mTextYear.setText(String.valueOf(calendar.getYear()));
        mTextLunar.setText(calendar.getLunar());
        mYear = calendar.getYear();
    }

    @Override
    public void onDateSelected(com.haibin.calendarview.Calendar calendar) {
        Log.d(TAG, "onDateSelected: 被调用");
        onDateChange(calendar);
        showChooseData(calendar);

    }

    public void showChooseData(com.haibin.calendarview.Calendar calendar) {
        com.haibin.calendarview.Calendar chooseDay = getSchemeCalendar(calendar.getYear(), calendar.getMonth(), calendar.getDay(), 0xFFaacc44, "查");
        boolean itPresence = false;
        for (int i = 0; i < schemes.size(); i++) {
            if (schemes.get(i).equals(chooseDay)) {
                itPresence = true;
                break;
            }
        }
        if (itPresence) {
            schemes.remove(chooseDay);
        } else {
            schemes.add(chooseDay);
        }
    }


    @Override
    public void onYearChange(int year) {
        mTextMonthDay.setText(String.valueOf(year));
    }


    @Override
    public void onClick(View v) {
        sortTime(schemes);
        Log.d(TAG, "onClick: -------------------------------" + v);
        Intent intent = new Intent();
        intent.putExtra("time_data", (Serializable) schemes);
        setResult(RESULT_OK, intent);
        finish();
    }




    @Override
    public void onBackPressed() {
        sortTime(schemes);
        Intent intent = new Intent();
        intent.putExtra("time_data", (Serializable)  schemes);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void sortTime(List<com.haibin.calendarview.Calendar> schemes) {
        for (int y = 0; y < schemes.size(); y++) {
            for (int z = (y + 1); z < schemes.size(); z++) {
                int x1 = Integer.parseInt(schemes.get(y).toString());
                int x2 = Integer.parseInt(schemes.get(z).toString());
                if (x1 - x2 > 0) {
                    schemes.add(y, schemes.get(z));
                    schemes.add(z + 1, schemes.get(y + 1));
                    schemes.remove(y + 1);
                    schemes.remove(z + 1);
                }
            }
        }
    }

}
