package com.developers.mytodoapp;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.w3c.dom.Text;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class InsightFragment extends Fragment {
    TextView tvNoOfCompTask;
    TextView tvNoOfInCompTask;
    TextView tvEfficiency;
    PieChart pieChart;
    Button btnReset;
    TextView tvNoData;
    SwipeRefreshLayout srlInsight;

    private float compTask;
    private float missedTask;
    private float totalTask;
    private int percentEfficiency;

    public PieDataSet dataSet;
    public PieData data;
    public ArrayList<Entry> yValues = new ArrayList<Entry>();
    public ArrayList<String> xValues = new ArrayList<String>();

    public InsightFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_insight, container, false);
        tvNoOfCompTask = (TextView)view.findViewById(R.id.tvNoOfCompTask);
        tvNoOfInCompTask = (TextView)view.findViewById(R.id.tvNoOfInCompTask);
        tvEfficiency = (TextView)view.findViewById(R.id.tvEfficiency);
        pieChart = (PieChart)view.findViewById(R.id.pcEfficiency);
        btnReset = (Button)view.findViewById(R.id.btnReset);
        tvNoData = (TextView)view.findViewById(R.id.tvNoData);

        // Fetching Data
        fetchData();

        // Setting the values of text view
        setTextViewData();

        // Plotting Pie Chart
        setYValues();
        setXValues();
        setDataSet();
        setData();
        toggleNoDataTextViewVisibility();
        pieChart.setData(data);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SqLiteTaskHelper.clearInsightTableData(getContext());
                        clearPieChart();
                        fetchData();
                        setTextViewData();
                        setYValues();
                        setXValues();
                        setDataSet();
                        setData();
                        pieChart.setData(data);
                        toggleNoDataTextViewVisibility();
                    }
                });
                builder.setNegativeButton("No",null);
                builder.setTitle("Reset");
                builder.setMessage("Are you sure you want to reset the Insight Data?");
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        return  view;
    }

    private void fetchData(){
        compTask = SqLiteTaskHelper.getTotalCompletedTask(getContext());
        missedTask = SqLiteTaskHelper.getMissedTask(getContext());
        totalTask = compTask+missedTask;
        percentEfficiency = (int) ((compTask/totalTask)*100);
    }

    private void setTextViewData(){
        tvNoOfCompTask.setText(Float.toString(compTask));
        tvNoOfInCompTask.setText(Float.toString(missedTask));
        tvEfficiency.setText(Integer.toString(percentEfficiency)+"%");
    }

    private void setYValues(){
        yValues.add(new Entry(compTask,0));
        yValues.add(new Entry(missedTask,1));
    }

    private void setXValues(){
        xValues.add("Completed Task");
        xValues.add("Missed Task");
    }

    private void setDataSet(){
        dataSet = new PieDataSet(yValues,"Task Insight");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
    }

    private void setData(){
        data = new PieData(xValues,dataSet);
    }

    private void clearPieChart(){
        dataSet.clear();
        data.clearValues();
        pieChart.clearValues();
        pieChart.clear();
    }
    private void toggleNoDataTextViewVisibility(){
        if(totalTask==0){
            tvNoData.setVisibility(View.VISIBLE);
        }else{
            tvNoData.setVisibility(View.INVISIBLE);
        }
    }
}
