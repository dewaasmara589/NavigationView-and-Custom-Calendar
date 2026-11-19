package com.alfastore.asasystem.v2;

import android.content.Context;
import android.graphics.Color;
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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarAdapterVH> {
    private ArrayList<String> daysOfMonth;
    private Context context;
    private String monthYear;
    private int heightParent;
    private boolean lastRowEmpty = false;
    private boolean last2RowEmpty = false;
    private int selectedPosition = RecyclerView.NO_POSITION;
    private int todayPosition = RecyclerView.NO_POSITION;
    private int sysDatePosition;

    public CalendarAdapter(ArrayList<String> daysOfMonth, String monthYear, Context context, int heightParent) {
        this.daysOfMonth = daysOfMonth;
        this.monthYear = monthYear;
        this.context = context;
        this.heightParent = heightParent;
        this.lastRowEmpty = checkLastRowEmpty();
        this.last2RowEmpty = checkLast2RowEmpty();
        this.todayPosition = findTodayPosition();
    }

    private boolean checkLastRowEmpty() {
        if (daysOfMonth.size() < 42) return false;
        int countFirstEmpty = 0;

        for (int i = 28; i < 35; i++) {
            if (daysOfMonth.get(i).equals("")) countFirstEmpty++;
        }

        return countFirstEmpty == 7;
    }

    private boolean checkLast2RowEmpty() {
        if (daysOfMonth.size() < 42) return false;
        int countLast2Empty = 0;

        for (int i = 35; i < 42; i++) {
            if (daysOfMonth.get(i).equals("")) countLast2Empty++;
        }

        return countLast2Empty == 7;
    }

    private int findTodayPosition() {
        String today = new SimpleDateFormat("d MMMM yyyy").format(Calendar.getInstance().getTime());
        for (int i = 0; i < daysOfMonth.size(); i++) {
            if (!daysOfMonth.get(i).equals("")
                    && (daysOfMonth.get(i) + " " + monthYear).equals(today)) {
                return i;
            }
        }
        return RecyclerView.NO_POSITION; // bulan ini bukan bulan sekarang
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
        String day = daysOfMonth.get(position);

        // 1. RESET state, karena ViewHolder bisa hasil recycle
        holder.cvBackgroundCalendar.setVisibility(View.VISIBLE);
        holder.cvBackgroundCalendar.setCardBackgroundColor(Color.WHITE);
        holder.cvBackgroundCalendar.setOnClickListener(null);
        holder.ivCircleBackground.setVisibility(View.GONE);
        holder.tvDay.setTextColor(ContextCompat.getColor(context, R.color.black));
        holder.tvDay.setText(day);

        // 2. Sel kosong
        if (day.equals("")) {
            holder.cvBackgroundCalendar.setVisibility(View.GONE);
            return;
        }else {
            float density = context.getResources().getDisplayMetrics().density;
            ViewGroup.MarginLayoutParams mlp =
                    (ViewGroup.MarginLayoutParams) holder.cvBackgroundCalendar.getLayoutParams();
            mlp.leftMargin = (int) (2 * density);
            mlp.rightMargin = (int) (2 * density);
            mlp.topMargin = (int) (1 * density);
            mlp.bottomMargin = (int) (1 * density);

            holder.cvBackgroundCalendar.setLayoutParams(mlp);
            holder.cvBackgroundCalendar.setRadius(10 * density);
            holder.cvBackgroundCalendar.setCardBackgroundColor(Color.parseColor("#A5D6A7"));
        }

        // 3. Sel berisi tanggal
        boolean isToday = (position == todayPosition);
        boolean isSelected = (position == selectedPosition);
        boolean otherDateSelected = selectedPosition != RecyclerView.NO_POSITION
                && selectedPosition != position;

        if (isSelected && !isToday) {
            holder.cvBackgroundCalendar.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.red));
        }

        if (isToday) {
            holder.ivCircleBackground.setVisibility(View.VISIBLE);

            if (otherDateSelected) {
                holder.tvDay.setTextColor(ContextCompat.getColor(context, R.color.black));
                holder.ivCircleBackground.setBackgroundResource(R.drawable.bg_sysdate_stroke);
            } else {
                holder.tvDay.setTextColor(ContextCompat.getColor(context, R.color.white));
                holder.ivCircleBackground.setBackgroundResource(R.drawable.bg_sysdate);
            }

            holder.cvBackgroundCalendar.post(() -> {
                int size = (int) (heightParent * 0.75f); // circle diameter, leaves some margin

                ViewGroup.LayoutParams params = holder.ivCircleBackground.getLayoutParams();
                params.width = size;
                params.height = size;
                holder.ivCircleBackground.setLayoutParams(params);
            });
        }

        holder.cvBackgroundCalendar.setOnClickListener(v -> {
            int previous = selectedPosition;
            int current = holder.getAdapterPosition();
            if (current == RecyclerView.NO_POSITION) return;

            selectedPosition = current;

            if (previous != RecyclerView.NO_POSITION) notifyItemChanged(previous);
            if (todayPosition != RecyclerView.NO_POSITION) notifyItemChanged(todayPosition);
            notifyItemChanged(selectedPosition);
        });
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
