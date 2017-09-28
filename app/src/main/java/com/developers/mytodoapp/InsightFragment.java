package com.developers.mytodoapp;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class InsightFragment extends Fragment {
    TextView tvNoOfCompTask;
    TextView tvNoOfInCompTask;
    TextView tvEfficiency;
    PieChart pieChart;
    TextView tvNoData;

    private float compTask;
    private float pendingTask;
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

        return  view;
    }

    private void fetchData(){
        compTask = SqLiteTaskHelper.getNoOfCompletedTask(getContext());
        pendingTask = SqLiteTaskHelper.getNoOfPendingTask(getContext());
        totalTask = compTask+pendingTask;
        percentEfficiency = (int) ((compTask/totalTask)*100);
    }

    private void setTextViewData(){
        tvNoOfCompTask.setText(String.format("%.0f",compTask));
        tvNoOfInCompTask.setText(String.format("%.0f",pendingTask));
        tvEfficiency.setText(Integer.toString(percentEfficiency)+"%");
    }

    private void setYValues(){
        yValues.add(new Entry(compTask,0));
        yValues.add(new Entry(pendingTask,1));
    }

    private void setXValues(){
        xValues.add("Completed Task");
        xValues.add("Pending Task");
    }

    private void setDataSet(){
        dataSet = new PieDataSet(yValues,"Task Insight");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
    }

    private void setData(){
        data = new PieData(xValues,dataSet);
    }

    private void toggleNoDataTextViewVisibility(){
        if(totalTask==0){
            tvNoData.setVisibility(View.VISIBLE);
        }else{
            tvNoData.setVisibility(View.INVISIBLE);
        }
    }
}
