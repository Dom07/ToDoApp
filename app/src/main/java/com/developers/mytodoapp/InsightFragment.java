package com.developers.mytodoapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class InsightFragment extends Fragment {
    TextView tvNoOfCompTask;
    TextView tvNoOfInCompTask;
    TextView tvEfficiency;
    PieChart pieChart;

    public InsightFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_insight, container, false);
        tvNoOfCompTask = (TextView)view.findViewById(R.id.tvNoOfCompTask);
        tvNoOfInCompTask = (TextView)view.findViewById(R.id.tvNoOfInCompTask);
        tvEfficiency = (TextView)view.findViewById(R.id.tvEfficiency);
        pieChart = (PieChart)view.findViewById(R.id.pcEfficiency);

        float compTask = SqLiteTaskHelper.getTotalCompletedTask(getContext());
        float missedTask = SqLiteTaskHelper.getMissedTask(getContext());
        tvNoOfCompTask.setText(Float.toString(compTask));
        tvNoOfInCompTask.setText(Float.toString(missedTask));
        float totalTask = compTask+missedTask;
        int percentEfficiency = (int) ((compTask/totalTask)*100);
        tvEfficiency.setText(Integer.toString(percentEfficiency)+"%");
        return  view;
    }

}
