package com.example.moca;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class testing extends AppCompatActivity {

    private BarChart barChart;

    private RequestQueue requestQueue;
    private static final String TAG = "testing";
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        barChart = findViewById(R.id.barChart);
        requestQueue = Volley.newRequestQueue(this);

        String url = Api.api+"bar.php?id="+id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        processJSONData(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error: " + error.getMessage());
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }


    private void processJSONData(JSONObject jsonObject) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        try {
            JSONArray resultData = jsonObject.getJSONArray("resultData");
            JSONArray dateData = jsonObject.getJSONArray("dateData");

            for (int i = 0; i < resultData.length(); i++) {
                String value = resultData.getString(i);
                float floatValue = Float.parseFloat(value);
                entries.add(new BarEntry(i, floatValue));
            }

            for (int i = 0; i < dateData.length(); i++) {
                String date = dateData.getString(i);
                labels.add(date);
            }

            BarDataSet dataSet = new BarDataSet(entries, "Patients");
            dataSet.setColor(Color.parseColor("#00008B"));

            BarData barData = new BarData(dataSet);
            barChart.setData(barData);
            barData.setValueTextSize(12f);
            barData.setValueTextColor(Color.BLACK);
            barData.setValueTypeface(Typeface.DEFAULT_BOLD);

            XAxis xAxis = barChart.getXAxis();
            xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1f);

            dataSet.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.valueOf((int) value);
                }
            });

            // Additional logic to display date for adjacent bars with identical dates
            for (int i = 0; i < labels.size() - 1; i++) {
                if (labels.get(i).equals(labels.get(i + 1))) {
                    xAxis.setAvoidFirstLastClipping(false);
                    xAxis.setLabelCount(labels.size());
                    break;
                }
            }

            YAxis yAxisLeft = barChart.getAxisLeft();
            YAxis yAxisRight = barChart.getAxisRight();
            yAxisLeft.setDrawGridLines(false);
            yAxisRight.setDrawGridLines(false);
            yAxisLeft.setDrawLabels(false);
            yAxisRight.setDrawLabels(false);
            yAxisLeft.setAxisMinimum(0);
            yAxisRight.setAxisMinimum(0);
            barChart.setDrawValueAboveBar(true);
            barChart.setFitBars(false);
            barChart.setDrawGridBackground(false);
            barChart.getDescription().setEnabled(false);
            barChart.animateY(1000);
            barChart.setTouchEnabled(true);
            barChart.getAxisLeft().setDrawGridLines(false);
            barChart.getXAxis().setDrawGridLines(false);

            float barWidthPixels = 0.5f;
            barData.setBarWidth(barWidthPixels);
            barChart.invalidate();
            barChart.setVisibleXRangeMaximum(4);
            barChart.moveViewToX(1);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
