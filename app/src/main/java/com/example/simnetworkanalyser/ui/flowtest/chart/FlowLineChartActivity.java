package com.example.simnetworkanalyser.ui.flowtest.chart;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.example.simnetworkanalyser.R;
import com.example.simnetworkanalyser.ui.BaseActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

public class FlowLineChartActivity extends BaseActivity {

    private LineChart lineChart;
    private TextView txtFlowAverage, txtFlowStatus;

    private ArrayList<Entry> entries = new ArrayList<>();

    NetworkCapabilities nc;

    // Create the Handler object (on the main thread by default)
    Handler handler = new Handler();

    int measuresCount = 0, secondsCount = 0;

    float measuresSum = 0;

    Runnable mFlowStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                updateFlowChart();
            } finally {
                handler.postDelayed(mFlowStatusChecker, 2000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_float_chart);

        // --

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        nc = cm.getNetworkCapabilities(cm.getActiveNetwork());

        // --

        txtFlowAverage = findViewById(R.id.txt_average_flow);
        txtFlowStatus = findViewById(R.id.txt_flow_status);

        lineChart = findViewById(R.id.linechart);
        lineChart.setTouchEnabled(true);
        lineChart.setPinchZoom(true);

        CustomMarkerView mv = new CustomMarkerView(getApplicationContext(), R.layout.custom_marker_view);
        mv.setChartView(lineChart);

        lineChart.setMarker(mv);

        // --

        setUpChart();
        setUpAxes();
        setUpData();
        setLegend();

        // --

        startFlowObservationTask();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopFlowObservationTask();
    }

    // -------------------------------------------------------------- //
    // -------------------------------------------------------------- //

    Random random = new Random();

    private void updateFlowChart() {

        float downStreamMeasure = ((float) nc.getLinkDownstreamBandwidthKbps() / 1000);

        downStreamMeasure -= 60 + random.nextFloat() * (70 - 60);

        measuresSum += downStreamMeasure;
        measuresCount++;

        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        float averageFlowValue = (float) (measuresSum / measuresCount);

        txtFlowAverage.setText(decimalFormat.format(averageFlowValue));

        if (averageFlowValue < 0.15) {
            txtFlowStatus.setText("POOR");
            txtFlowStatus.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        } else if (averageFlowValue > 0.15 && averageFlowValue < 0.55) {
            txtFlowStatus.setText("MODERATE");
            txtFlowStatus.setTextColor(getResources().getColor(android.R.color.holo_orange_light));
        } else if (averageFlowValue > 0.55 && averageFlowValue < 2) {
            txtFlowStatus.setText("GOOD");
            txtFlowStatus.setTextColor(getResources().getColor(android.R.color.holo_green_light));
        } else if (averageFlowValue > 2) {
            txtFlowStatus.setText("Excellent");
            txtFlowStatus.setTextColor(getResources().getColor(android.R.color.holo_purple));
        }

        // --

        addEntry(new Flow(downStreamMeasure));
        secondsCount += 2;
    }

    private void startFlowObservationTask() {
        mFlowStatusChecker.run();
    }

    private void stopFlowObservationTask() {
        handler.removeCallbacks(mFlowStatusChecker);
    }


    // -------------------------------------------------------------- //
    // -------------------------------------------------------------- //

    private void setUpChart() {

        // Disable description text
        lineChart.getDescription().setEnabled(false);

        // Enable touch gestures
        lineChart.setTouchEnabled(true);

        // If disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(true);

        // Enable scaling
        lineChart.setScaleEnabled(true);
        lineChart.setAutoScaleMinMaxEnabled(true);

        lineChart.setDrawGridBackground(false);

        // Set an alternative background color
        lineChart.setBackgroundColor(Color.DKGRAY);
    }

    private void setUpAxes() {

        XAxis xl = lineChart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

        // Add a limit line
        LimitLine vhl = new LimitLine(45f, "Very High Flow");
        vhl.setLineWidth(2f);
        vhl.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        vhl.setTextSize(10f);
        vhl.setTextColor(Color.WHITE);

        // Add a limit line
        LimitLine hl = new LimitLine(15f, "High Flow");
        hl.setLineWidth(2f);
        hl.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        hl.setTextSize(10f);
        hl.setTextColor(Color.WHITE);

        // Add a limit line
        LimitLine ll = new LimitLine(.2f, "Low Flow");
        ll.setLineWidth(2f);
        ll.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll.setTextSize(10f);
        ll.setTextColor(Color.WHITE);

        // reset all limit lines to avoid overlapping lines
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(vhl);
        leftAxis.addLimitLine(hl);
        leftAxis.addLimitLine(ll);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);
    }

    private void setUpData() {

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        // add empty data
        lineChart.setData(data);
    }

    private void setLegend() {

        // get the legend (only possible after setting data)
        Legend l = lineChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setTextColor(Color.WHITE);
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "Flow test : Mo/s");

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColors(ColorTemplate.VORDIPLOM_COLORS[0]);
        set.setCircleColor(Color.WHITE);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(10f);

        // To show values of each point
        set.setDrawValues(true);

        return set;
    }

    private void addEntry(Flow flow) {

        LineData data = lineChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            data.addEntry(new Entry(secondsCount, flow.getDebit()), 0);

            // let the chart know it's data has changed
            data.notifyDataChanged();
            lineChart.notifyDataSetChanged();

            // limit the number of visible entries
            lineChart.setVisibleXRangeMaximum(15);

            // move to the latest entry
            lineChart.moveViewToX(data.getEntryCount());
        }
    }

}
