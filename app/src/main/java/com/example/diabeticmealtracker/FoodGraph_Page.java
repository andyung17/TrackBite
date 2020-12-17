package com.example.diabeticmealtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoodGraph_Page extends AppCompatActivity {

    public String dateRange, displayedContent, whichGraph;
    public String[] months = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_graph__page);
        Button button = (Button) findViewById(R.id.button9);
        new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {
                button.setBackgroundResource(R.drawable.my_button_design);
                button.setText("seconds remaining: " + millisUntilFinished / 1000);
                button.setTextColor(getApplication().getResources().getColor(R.color.textColor));
                button.setBackgroundColor(getApplication().getResources().getColor(R.color.button_background2));
                button.setEnabled(false);
                button.setBackgroundResource(R.drawable.my_button_design);
            }

            public void onFinish() {
                button.setText("Generate");
                button.setTextColor(getApplication().getResources().getColor(R.color.white));
                button.setBackgroundResource(R.drawable.my_button_design);
                button.setEnabled(true);
            }
        }.start();

        FirebaseAuth mAuth = FirebaseAuth.getInstance(); //Grabs current instance of database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser(); //Grabs current user
        db.collection("users").document(user.getUid().toString()).collection("userData")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<String> validDatesArray = new ArrayList<>();
                            Map<String, Double> validPortions = new HashMap<>();
                            Map<String, Double> validTrend = new HashMap<>();
                            ArrayList<String> activities = new ArrayList<String>();
                            Bundle extra = getIntent().getExtras();
                            String[] values = extra.getStringArray("values");
                            boolean displayActiveHours;
                            if (values[1].equals("All")) {
                                whichGraph = "Pie";
                                displayedContent = "All";
                            } else {
                                whichGraph = "Line";
                                displayedContent = values[1];
                            }

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String docId = document.getId();
                                if (!docId.equals("profile") && !docId.equals("Analysis") && !docId.equals("LineAnalysis") && !docId.equals("FoodAnalysis") && !docId.equals("savedMeals")) {
                                    int documentDate = Integer.parseInt(docId);
                                    if (documentDate >= Integer.parseInt(dateRange.substring(0, 8)) && documentDate <= Integer.parseInt(dateRange.substring(9))) {
                                        validDatesArray.add(docId);
                                        db.collection("users").document(user.getUid()).collection("userData").document(docId).collection("Total").document("Total")
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot document = task.getResult();
                                                            if (!validTrend.containsKey(docId)) {
                                                                if (displayedContent == "Carbohydrates") {
                                                                    Double tCarb = Double.parseDouble(document.getString("Total carbs"));
                                                                    validTrend.put(docId, tCarb);
                                                                } else if (displayedContent == "Fats") {
                                                                    Double tFat = Double.parseDouble(document.getString("Total fats"));
                                                                    validTrend.put(docId, tFat);
                                                                } else if (displayedContent == "Protein") {
                                                                    Double tProt = Double.parseDouble(document.getString("Total protein"));
                                                                    validTrend.put(docId, tProt);
                                                                } else if (displayedContent == "Calories") {
                                                                    Double tCal = Double.parseDouble(document.getString("Total calories"));
                                                                    validTrend.put(docId, tCal);
                                                                } else {
                                                                    Double tSugar = Double.parseDouble(document.getString("Total sugar"));
                                                                    validTrend.put(docId, tSugar);
                                                                }
                                                            } else {
                                                                if (displayedContent == "Carbohydrates") {
                                                                    Double tCarb = Double.parseDouble(document.getString("Total carbs"));
                                                                    validTrend.put(docId, validTrend.get(tCarb));
                                                                } else if (displayedContent == "Fats") {
                                                                    Double tFat = Double.parseDouble(document.getString("Total fats"));
                                                                    validTrend.put(docId, validTrend.get(tFat));
                                                                } else if (displayedContent == "Protein") {
                                                                    Double tProt = Double.parseDouble(document.getString("Total protein"));
                                                                    validTrend.put(docId, validTrend.get(tProt));
                                                                } else if (displayedContent == "Calories") {
                                                                    Double tCal = Double.parseDouble(document.getString("Total calories"));
                                                                    validTrend.put(docId, validTrend.get(tCal));
                                                                } else {
                                                                    Double tSugar = Double.parseDouble(document.getString("Total sugar"));
                                                                    validTrend.put(docId, validTrend.get(tSugar));
                                                                }
                                                            }
                                                            db.collection("users").document(user.getUid()).collection("userData").document("FoodAnalysis").set(validTrend);
                                                        } else {
                                                            Toast.makeText(getApplicationContext(), "Error in retrieving documents from database", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }
                        }
                    }
                });
    }

    protected void onStart() {
        super.onStart();
        Bundle extra = getIntent().getExtras();
        String[] values = extra.getStringArray("values");


        FirebaseAuth mAuth = FirebaseAuth.getInstance(); //Grabs current instance of database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser(); //Grabs current user

        TextView timeRange = (TextView) findViewById(R.id.timeRange);
        TextView foodAnalysed = (TextView) findViewById(R.id.foodAnalysed); //exerciseAnalysis

        timeRange.setText(values[0]);
        foodAnalysed.setText(values[1]);

        if (values[0].equals("Week") || values[0].equals("Month") || values[0].equals("Year")) {
            timeRange.setText("Data for the last " + values[0]);
            String currDate = dateRetriever();
            this.dateRange = dateDecrementer(currDate, values[0]) + "-" + currDate;
        } else {
            String firstDate = convertStringToDate(dateReorganizer(values[0].substring(0, 8)));
            String secondDate = convertStringToDate(dateReorganizer(values[0].substring(9)));
            timeRange.setText("From " + firstDate + " to " + secondDate);
            this.dateRange = dateReorganizer(values[0].substring(0, 8)) + "-" + dateReorganizer(values[0].substring(9));
        }
        if (values[1].equals("All")) {
            foodAnalysed.setText("Analyzing all possible values as portions");
            this.whichGraph = "Pie";
            this.displayedContent = "All";
        } else {
            foodAnalysed.setText("Analyzing " + values[1] + " in grams");
        }

    }

    public void switchGraph(View view) {

//        if (state) {
//            generatePieGraph(view);
//        }
//        else {
//            generateLineGraph(view);
//        }
    }
    public String convertMonthNum(String month) {
        if (month.equals("January")) {
            return "01";
        } else if (month.equals("February")) {
            return "02";
        } else if (month.equals("March")) {
            return "03";
        } else if (month.equals("April")) {
            return "04";
        } else if (month.equals("May")) {
            return "05";
        } else if (month.equals("June")) {
            return "06";
        } else if (month.equals("July")) {
            return "07";
        } else if (month.equals("August")) {
            return "08";
        } else if (month.equals("September")) {
            return "09";
        } else if (month.equals("October")) {
            return "10";
        } else if (month.equals("November")) {
            return "11";
        } else if (month.equals("December")) {
            return "12";
        } else {
            return "MONTH ERROR";
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    public String dateRetriever() {
        Date currentTime = Calendar.getInstance().getTime();
        String formattedDate = DateFormat.getDateInstance(DateFormat.LONG).format(currentTime);
        formattedDate = formattedDate.replace(",", "");
        String[] splitDate = formattedDate.split(" ");
        String month = convertMonthNum(splitDate[0]);
        String dateNum = formatDate(splitDate[1]);
        String year = splitDate[2];
        String date = year + month + dateNum;
        return date;
    }

    public String dateDecrementer(String date, String decrementInterval) { // date = yyyymmdd
        int[] daysPerMonth = new int[]{31,28,31,30,31,30,31,31,30,31,30,31};
        String newDate;
        int year = Integer.parseInt(date.substring(0,4));
        int month = Integer.parseInt(date.substring(4,6));
        int day = Integer.parseInt(date.substring(6));


        if (decrementInterval.equals("Week")) {
            day -= 7;
            if (day <= 0) {
                month-=1;
                if (month == 0) {
                    year-=1;
                    month = 12;
                    day = daysPerMonth[month-1];
                }
                else {
                    day = daysPerMonth[month-1]+day;
                }
            }

        }
        else if (decrementInterval.equals("Month")) {
            month-=1;
            if (month == 0) {
                year-=1;
                month = 12;
            }

        }
        else { // Year
            year-=1;
        }
        if (day < 10) {
            return String.valueOf(year)+String.valueOf(month)+"0"+String.valueOf(day);
        }
        newDate = String.valueOf(year)+String.valueOf(month)+String.valueOf(day);
        return newDate;
    }

    public String dateReorganizer(String date) { // Input as ddmmyyyy, output as yyyymmdd
        return date.substring(4)+date.substring(2,4)+date.substring(0,2);
    }

    public String convertStringToDate(String date) { // Format of yyyymmdd
        int monthIndex = Integer.parseInt(date.substring(4,6))-1;
        return this.months[monthIndex] + " " + date.substring(6) + ", " + date.substring(0,4);
    }


    public void generateNewLineGraph(View view) {
        Bundle extra = getIntent().getExtras();
        String[] values = extra.getStringArray("values");
        FirebaseAuth mAuth = FirebaseAuth.getInstance(); //Grabs current instance of database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser(); //Grabs current user
        //Line Graph Creation
        db.collection("users").document(user.getUid()).collection("userData").document("FoodAnalysis")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            Map<String,Object> dbValuesThree = document.getData();
                            AnyChartView anyChartViewThree = findViewById(R.id.foodLineChart);
                            Cartesian foodCartesian = AnyChart.line();
                            foodCartesian.yAxis(0).title("Total "+values[1]);
                            List<DataEntry> seriesData = new ArrayList<>();
                            db.collection("users").document(user.getUid().toString()).collection("userData")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    String docId = document.getId();
                                                    if (!docId.equals("FoodAnalysis") && !docId.equals("profile") && !docId.equals("Analysis") && !docId.equals("LineAnalysis")) {
                                                        if (dbValuesThree.containsKey(docId)) {
                                                            seriesData.add(new ValueDataEntry(convertStringToDate(docId), (Number) dbValuesThree.get(docId)));
                                                        }
                                                    }
                                                }
                                                foodCartesian.animation(true);
                                                foodCartesian.crosshair().enabled(true);
                                                foodCartesian.crosshair()
                                                        .yLabel(true)
                                                        .yStroke((Stroke)null,null,null,(String)null,(String)null);
                                                foodCartesian.tooltip().positionMode(TooltipPositionMode.POINT);
                                                foodCartesian.xAxis(0).title("Date");
                                                foodCartesian.title("Line graph for Total "+values[1]);
                                                foodCartesian.data(seriesData);
                                                anyChartViewThree.setChart(foodCartesian);
                                            }
                                            else {
                                                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    public String formatDate(String date){
        String newDate;
        if (date.length() == 1){
            newDate = "0" + date;
            return newDate;
        }else{
            return date;
        }
    }

    public void backExerciseGraphPage (View view){
        FirebaseAuth mAuth = FirebaseAuth.getInstance(); //Grabs current instance of database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser(); //Grabs current user

        db.collection("users").document(user.getUid()).collection("userData").document("FoodAnalysis")
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }
}