package com.example.diabeticmealtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class FoodAnalysis_Page extends AppCompatActivity {

    private String currRange;
    private String currFood;
    private boolean isCustom;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_analysis_page);
        isCustom = true;

        EditText firstRange = (EditText) findViewById(R.id.customRange1);
        EditText lastRange = (EditText) findViewById(R.id.customRange2);

        Spinner rangeSpinner = (Spinner) findViewById(R.id.rangeSpinner);
        Spinner foodSpinner = (Spinner) findViewById(R.id.foodSpinner);

        // List of Activities
        List<String> rangeArray = new ArrayList<String>();
        rangeArray.add("None");
        rangeArray.add("Week");
        rangeArray.add("Month");
        rangeArray.add("Year");

        List<String> foodArray = new ArrayList<>();
        foodArray.add("Calories");
        foodArray.add("Carbohydrates");
        foodArray.add("Fats");
        foodArray.add("Protein");
        foodArray.add("Sugar");

        // Array adapter (context, layout of spinner, and array values in the spinner)
        ArrayAdapter<String> rangeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, rangeArray);
        ArrayAdapter<String> foodAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, foodArray);

        // set drop down menu
        rangeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rangeSpinner.setAdapter(rangeAdapter);

        foodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        foodSpinner.setAdapter(foodAdapter);

        rangeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                currRange = rangeArray.get(pos);
                if (currRange.equals("None")) {
                    firstRange.getText().clear();
                    lastRange.getText().clear();
                    firstRange.setEnabled(true);
                    lastRange.setEnabled(true);
                    firstRange.setHint("dd/mm/yyyy");
                    lastRange.setHint("dd/mm/yyyy");
                    isCustom = true;
                }
                else {
                    firstRange.getText().clear();
                    lastRange.getText().clear();
                    firstRange.setEnabled(false);
                    lastRange.setEnabled(false);
                    firstRange.setHint("N/A");
                    lastRange.setHint("N/A");
                    isCustom = false;
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }

        });

        foodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                currFood = foodArray.get(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    public void backFoodDataAnalysis (View view){
        finish();
    }
    public void nextFoodDataAnalysis (View view) {
        EditText firstRange = (EditText) findViewById(R.id.customRange1);
        EditText lastRange = (EditText) findViewById(R.id.customRange2);

        String firstRangeText = firstRange.getText().toString();
        String lastRangeText = lastRange.getText().toString();

        if (this.isCustom == true) {
            if (this.currFood == null || (this.currRange.equals("None") && (TextUtils.isEmpty(firstRangeText) || TextUtils.isEmpty(lastRangeText)))) {
                Toast.makeText(getApplicationContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else if (firstRangeText.length() > 8 || lastRangeText.length() > 8) {
                Toast.makeText(getApplicationContext(), "Too many numbers inputted for date", Toast.LENGTH_SHORT).show();
            } else if (firstRangeText.length() < 8 || lastRangeText.length() < 8) {
                Toast.makeText(getApplicationContext(), "Too few numbers inputted for date", Toast.LENGTH_SHORT).show();
            } else {
                int firstDate = Integer.parseInt(firstRangeText.substring(4) + firstRangeText.substring(2, 4) + firstRangeText.substring(0, 2));
                int lastDate = Integer.parseInt(lastRangeText.substring(4) + lastRangeText.substring(2, 4) + lastRangeText.substring(0, 2));
                int firstDay = Integer.parseInt(firstRangeText.substring(0, 2));
                int firstMonth = Integer.parseInt(firstRangeText.substring(2, 4));
                int firstYear = Integer.parseInt(firstRangeText.substring(4));
                int lastDay = Integer.parseInt(lastRangeText.substring(0, 2));
                int lastMonth = Integer.parseInt(lastRangeText.substring(2, 4));
                int lastYear = Integer.parseInt(lastRangeText.substring(4));

                boolean isDaysProperlyFormatted = (firstDay >= 1 && firstDay <= 31 && lastDay >= 1 && lastDay <= 31);
                boolean isMonthsProperlyFormatted = (firstMonth >= 1 && firstMonth <= 12 && lastMonth >= 1 && lastMonth <= 12);
                boolean isYearsProperlyFormatted = (firstYear >= 2020 && lastYear >= 2020);
                boolean isLastDateTheSameAsFirstDate = (lastDate == firstDate);
                boolean isLastDateGreaterThanFirstDate = (lastDate > firstDate);

                if ((this.currRange.equals("None") && isDaysProperlyFormatted && isMonthsProperlyFormatted && isYearsProperlyFormatted) == false) {
                    Toast.makeText(getApplicationContext(), "Date is incorrectly formatted", Toast.LENGTH_SHORT).show();
                } else if (isLastDateTheSameAsFirstDate == true) {
                    Toast.makeText(getApplicationContext(), "Both dates are the exact same", Toast.LENGTH_SHORT).show();
                } else if (isLastDateGreaterThanFirstDate == false) {
                    Toast.makeText(getApplicationContext(), "The last date comes before the first date", Toast.LENGTH_SHORT).show();
                } else {
                    String[] graphValues = new String[2];
                    if (this.currRange.equals("None") == false) {
                        graphValues[0] = this.currRange;
                    } else {
                        graphValues[0] = firstRangeText + "-" + lastRangeText;
                    }
                    graphValues[1] = this.currFood;
                    Intent intent = new Intent(getApplicationContext(), FoodGraph_Page.class);
                    startActivity(intent.putExtra("values", graphValues));
                }

            }
        }
        else {
            String[] graphValues = new String[2];
            graphValues[0] = this.currRange;
            graphValues[1] = this.currFood;
            Intent intent = new Intent(getApplicationContext(), FoodGraph_Page.class);
            startActivity(intent.putExtra("values", graphValues));
        }
    }
}