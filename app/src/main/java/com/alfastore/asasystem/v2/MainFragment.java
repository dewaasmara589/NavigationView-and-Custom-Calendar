package com.alfastore.asasystem.v2;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MainFragment extends Fragment {
    CalendarView calendarView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_content_fragment, container, false);

        calendarView = (CalendarView) view.findViewById(R.id.calendarView);
        calendarView.setDate(System.currentTimeMillis(), false, true);
        
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                Toast.makeText(getActivity().getApplicationContext(), "Selected date Day: " + i2 + " Month : " + (i1 + 1) + " Year " + i, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
