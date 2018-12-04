package com.wugeek.wugeek.base;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.wugeek.wugeek.DayOnlineInfo;
import com.wugeek.wugeek.R;
import com.wugeek.wugeek.bean.EchartsLineBean;
import com.wugeek.wugeek.bean.OnlineInfo;
import com.wugeek.wugeek.utils.TimeUtils;

import java.io.Serializable;

public class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.viewHolder> {
    private EchartsLineBean echartsLineBean;
    private static final String TAG = "TimeAdapter";

    public TimeAdapter(EchartsLineBean echartsLineBean) {
        this.echartsLineBean = echartsLineBean;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_time, viewGroup, false);
        viewHolder viewHolder = new viewHolder(view);

        view.setId(i);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder viewHolder, int i) {
        viewHolder.yearTime.setText(echartsLineBean.times.get(i).toString());
        double k = Double.valueOf(echartsLineBean.hours.get(i).toString());
        viewHolder.hoursTime.setText("当日时长： " + TimeUtils.toHours(k) + "小时");

        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // 点击事件
                Intent intent = new Intent(arg0.getContext(), DayOnlineInfo.class);
                intent.putExtra("data", (Serializable) echartsLineBean.onlineInfo.get(i));
                echartsLineBean = new EchartsLineBean();
                arg0.getContext().startActivity(intent);

            }
        });


    }


    @Override
    public int getItemCount() {
        return echartsLineBean.times.size();
    }


    static class viewHolder extends RecyclerView.ViewHolder {
        TextView yearTime;
        TextView hoursTime;
        View view;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            yearTime = itemView.findViewById(R.id.year_time);
            hoursTime = itemView.findViewById(R.id.hours_time);
        }
    }

}
