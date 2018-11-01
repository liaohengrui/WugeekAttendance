package com.wugeek.wugeek.base;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wugeek.wugeek.R;
import com.wugeek.wugeek.bean.EchartsLineBean;
import com.wugeek.wugeek.utils.TimeUtils;

public class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.viewHolder> {
    private EchartsLineBean echartsLineBean;

    public TimeAdapter(EchartsLineBean echartsLineBean) {
        this.echartsLineBean = echartsLineBean;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_time, viewGroup, false);
        viewHolder viewHolder = new viewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder viewHolder, int i) {
        Log.d("ssssssssssssssssssss", "onBindViewHolder: " + i);
        viewHolder.yearTime.setText(echartsLineBean.times.get(i).toString());
        double k = Double.valueOf(echartsLineBean.hours.get(i).toString());
        Log.d("dsadasdsadasdasdas", "onBindViewHolder: "+k);
        viewHolder.hoursTime.setText("当日时长： " + TimeUtils.toHours(k) + "小时");
    }


    @Override
    public int getItemCount() {
        return echartsLineBean.times.size();
    }

    static class viewHolder extends RecyclerView.ViewHolder {
        TextView yearTime;
        TextView hoursTime;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            yearTime = itemView.findViewById(R.id.year_time);
            hoursTime = itemView.findViewById(R.id.hours_time);
        }
    }


    public void addItem(int position, String time, String hours) {
        echartsLineBean.times.add(position, time);
        echartsLineBean.hours.add(position, hours);
        notifyItemInserted(position);//通知演示插入动画
        notifyItemRangeChanged(position, echartsLineBean.times.size() - position);//通知数据与界面重新绑定
    }
}
