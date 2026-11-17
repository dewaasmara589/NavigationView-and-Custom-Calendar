package com.alfastore.asasystem.v2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.time.YearMonth;

public class SecondFragment extends Fragment {
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDate;
    private Button btnBack, btnForward;
    private Context context;
    private ImageView ivArrowView;
    private FrameLayout flCalendarContainer;

    private ValueAnimator animator;

    private static final int CELL_HEIGHT_DP = 48;
    private static final int ROWS_COLLAPSED = 1;
    private static final int ROWS_EXPANDED = 6;

    private float progress = 0f;

    private int cellHeight;
    private boolean isExpanded = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);

        context = container.getContext();
        cellHeight = (int) (CELL_HEIGHT_DP * getResources().getDisplayMetrics().density);

        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);
        monthYearText = view.findViewById(R.id.monthYearTV);
        ivArrowView = view.findViewById(R.id.ivArrowView);
        btnBack = view.findViewById(R.id.btnBack);
        btnForward = view.findViewById(R.id.btnForward);
        flCalendarContainer = view.findViewById(R.id.flCalendarContainer);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            selectedDate = LocalDate.now();
        }

        // Pasang sekali saja
        calendarRecyclerView.setLayoutManager(new GridLayoutManager(context, 7) {
            @Override
            public boolean canScrollVertically() {
                return false; // posisi hanya diatur lewat kode
            }
        });
        calendarRecyclerView.setItemAnimator(null);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(calendarRecyclerView);

        setMonthView(context);

        btnBack.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                selectedDate = selectedDate.minusMonths(1);
            }
            setMonthView(context);
        });

        btnForward.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                selectedDate = selectedDate.plusMonths(1);
            }
            setMonthView(context);
        });

        ivArrowView.setOnClickListener(v -> toggleCalendar());

        ViewGroup.LayoutParams rvLp = calendarRecyclerView.getLayoutParams();
        rvLp.height = cellHeight * ROWS_EXPANDED;
        calendarRecyclerView.setLayoutParams(rvLp);

        applyProgress(0f);

        return view;
    }

    private int getCollapsedRow() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return 0;

        LocalDate today = LocalDate.now();
        if (YearMonth.from(today).equals(YearMonth.from(selectedDate))) {
            int offset = today.withDayOfMonth(1).getDayOfWeek().getValue() - 1;
            return (offset + today.getDayOfMonth() - 1) / 7;
        }
        return 0;
    }

    private void applyProgress(float p) {
        progress = p;

        int collapsed = cellHeight * ROWS_COLLAPSED;
        int expanded = cellHeight * ROWS_EXPANDED;

        ViewGroup.LayoutParams lp = flCalendarContainer.getLayoutParams();
        lp.height = (int) (collapsed + (expanded - collapsed) * p);
        flCalendarContainer.setLayoutParams(lp);

        // Geser baris sysdate ke atas saat collapsed, kembali ke 0 saat expanded
        calendarRecyclerView.setTranslationY(-getCollapsedRow() * cellHeight * (1f - p));
    }

    private void toggleCalendar() {
        isExpanded = !isExpanded;

        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }

        animator = ValueAnimator.ofFloat(progress, isExpanded ? 1f : 0f);
        animator.setDuration(300);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(a -> applyProgress((float) a.getAnimatedValue()));
        animator.start();

        ivArrowView.animate()
                .rotation(isExpanded ? 180f : 0f)
                .setDuration(300)
                .start();
    }

    ItemTouchHelper.SimpleCallback simpleCallback =
            new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView,
                                      @NonNull RecyclerView.ViewHolder viewHolder,
                                      @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        selectedDate = (direction == ItemTouchHelper.RIGHT)
                                ? selectedDate.minusMonths(1)
                                : selectedDate.plusMonths(1);
                    }
                    setMonthView(context);
                }

                @Override
                public long getAnimationDuration(@NonNull RecyclerView recyclerView,
                                                 int animationType, float animateDx, float animateDy) {
                    return 0;
                }
            };

    private void setMonthView(Context context) {
        String getMonthYear = monthYearFromDate(selectedDate);
        monthYearText.setText(getMonthYear);
        ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);

        // Tinggi sel tetap, tidak lagi bergantung pada tinggi RecyclerView
        CalendarAdapter calendarAdapter =
                new CalendarAdapter(daysInMonth, getMonthYear, context, cellHeight);
        calendarRecyclerView.setAdapter(calendarAdapter);

        applyProgress(progress);
    }

    private ArrayList<String> daysInMonthArray(LocalDate date) {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = null;
        int daysInMonth = 0, dayOfWeek = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            yearMonth = YearMonth.from(date);

            daysInMonth = yearMonth.lengthOfMonth();

            LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
            // Setting urutan days atau setting tanggal untuk start hari dalam seminggu
            dayOfWeek = firstOfMonth.getDayOfWeek().getValue()-1;
        }

        for(int i = 1; i <= 42; i++) {
            if(i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                daysInMonthArray.add("");
            } else{
                daysInMonthArray.add(String.valueOf(i - dayOfWeek));
            }
        }
        return  daysInMonthArray;
    }

    private String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
            return date.format(formatter);
        }else {
            return "";
        }
    }
}