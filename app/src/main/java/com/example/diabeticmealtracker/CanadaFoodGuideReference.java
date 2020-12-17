package com.example.diabeticmealtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.view.View;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;

import java.util.ArrayList;
import java.util.List;

public class CanadaFoodGuideReference extends AppCompatActivity {

    AnyChartView anyChartView;

    String[] foodGroups = {"Fruits & Vegetables", "Protein", "Whole Grains" };
    int [] values = {50,25,25};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canada_food_guide_reference);

        anyChartView = findViewById(R.id.any_chart_view);

        setupPieChart();

    }

    private void setupPieChart() {
        Pie pie = AnyChart.pie();
        List<DataEntry> dataEntries = new ArrayList<>();

        for (int i = 0; i < foodGroups.length;i++){
            dataEntries.add(new ValueDataEntry(foodGroups[i],values[i]));
        }
        pie.data(dataEntries);
        //pie.title("Canada Food Guide Reference");
        anyChartView.setChart(pie);
    }
    public void backCanadaFoodGuideReference (View view){
        finish();
    }

    public void onlineCFG (View view){
        Uri uri = Uri.parse("https://food-guide.canada.ca/en/");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

}