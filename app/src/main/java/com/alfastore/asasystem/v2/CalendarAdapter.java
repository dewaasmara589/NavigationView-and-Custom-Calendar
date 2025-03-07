package com.alfastore.asasystem.v2;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarAdapterVH> {
    private ArrayList<String> daysOfMonth;
    private Context context;
    private String monthYear;
    private int heightParent;

    public CalendarAdapter(ArrayList<String> daysOfMonth, String monthYear, Context context, int heightParent) {
        this.daysOfMonth = daysOfMonth;
        this.monthYear = monthYear;
        this.context = context;
        this.heightParent = heightParent;
    }

    @NonNull
    @Override
    public CalendarAdapter.CalendarAdapterVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) heightParent;
        return new CalendarAdapter.CalendarAdapterVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarAdapter.CalendarAdapterVH holder, int position) {
        String fullFormatDate = daysOfMonth.get(position) + " " + monthYear;

        if (daysOfMonth.get(position) == ""){
            holder.cvBackgroundCalendar.setBackgroundResource(R.color.gray);
        }else {
            holder.cvBackgroundCalendar.setOnClickListener(view -> {
                Toast.makeText(context.getApplicationContext(), "Selected Date = " + fullFormatDate, Toast.LENGTH_SHORT).show();
            });
        }

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy");
        String date = dateFormat.format(calendar.getTime());

        if (fullFormatDate.equals(date)){
            Log.d("TAG", "sysdate : " + date);

            holder.cvBackgroundCalendar.post(new Runnable() {
                @Override
                public void run() {
                    int width = holder.cvBackgroundCalendar.getWidth();
                    int height = holder.cvBackgroundCalendar.getHeight();

                    ViewGroup.LayoutParams params = holder.ivCircleBackground.getLayoutParams();
                    params.width = width / 2;
                    params.height = height / 2;

                    holder.ivCircleBackground.setLayoutParams(params);
                }
            });

            holder.tvDay.setTextColor(context.getResources().getColor(R.color.white));
            holder.ivCircleBackground.setVisibility(View.VISIBLE);
        }

        holder.tvDay.setText(daysOfMonth.get(position));
    }

    @Override
    public int getItemCount() {
        return daysOfMonth.size();
    }

    public class CalendarAdapterVH extends RecyclerView.ViewHolder {
        TextView tvDay;
        CardView cvBackgroundCalendar;
        ImageView ivCircleBackground;
        ConstraintLayout clParentCalendarCell;

        public CalendarAdapterVH(@NonNull View itemView) {
            super(itemView);

            tvDay = itemView.findViewById(R.id.tvDay);
            cvBackgroundCalendar = itemView.findViewById(R.id.cvBackgroundCalendar);
            ivCircleBackground = itemView.findViewById(R.id.ivCircleBackground);
            clParentCalendarCell = itemView.findViewById(R.id.clParentCalendarCell);
        }
    }
}
