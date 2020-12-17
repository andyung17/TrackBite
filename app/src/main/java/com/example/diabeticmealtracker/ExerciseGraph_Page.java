package com.example.diabeticmealtracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.charts.Pie;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExerciseGraph_Page extends AppCompatActivity {

    public boolean displayCaloriesBurned, displayActiveHours;
    public String dateRange;
    public String[] months = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_graph__page);
        Button button = (Button) findViewById(R.id.button3);
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
                            Map<String, Double> validProperty = new HashMap<>();
                            Map<String, Double> validTrend = new HashMap<>();
                            ArrayList<String> activities = new ArrayList<String>();
                            Bundle extra = getIntent().getExtras();
                            String[] values = extra.getStringArray("values");
                            boolean displayActiveHours;
                            if (values[1].equals("Calories Burned")) {
                                displayActiveHours = false;
                            } else {
                                displayActiveHours = true;
                            }

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String docId = document.getId();
                                if (!docId.equals("FoodAnalysis")&&!docId.equals("profile") && !docId.equals("Analysis") && !docId.equals("LineAnalysis") && !docId.equals("savedMeals")) {
                                    int documentDate = Integer.parseInt(docId);
                                    if (documentDate >= Integer.parseInt(dateRange.substring(0, 8)) && documentDate <= Integer.parseInt(dateRange.substring(9))) {
                                        validDatesArray.add(docId);
                                        db.collection("users").document(user.getUid()).collection("userData").document(docId).collection("Exercise")
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                                String exerciseId = document.getId();
                                                                if (!activities.contains(exerciseId)) {
                                                                    activities.add(exerciseId);
                                                                }
                                                                db.collection("users").document(user.getUid()).collection("userData").document(docId).collection("Exercise").document(exerciseId)
                                                                        .get()
                                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    DocumentSnapshot document = task.getResult();
                                                                                    if (displayActiveHours == true) {
                                                                                        Double temp = Double.parseDouble(document.getString("Duration"));
                                                                                        if (!validProperty.containsKey(exerciseId)) {
                                                                                            validProperty.put(exerciseId, temp);
                                                                                        } else {
                                                                                            validProperty.put(exerciseId, validProperty.get(exerciseId) + temp);
                                                                                        }
                                                                                        if (!validTrend.containsKey(docId)) {
                                                                                            validTrend.put(docId, temp);
                                                                                        } else {
                                                                                            validTrend.put(docId, validTrend.get(docId) + temp);
                                                                                        }
                                                                                    } else {
                                                                                        Double temp = Double.parseDouble(document.getString("CaloriesBurned"));
                                                                                        if (!validProperty.containsKey(exerciseId)) {
                                                                                            validProperty.put(exerciseId, temp);
                                                                                        } else {
                                                                                            validProperty.put(exerciseId, validProperty.get(exerciseId) + temp);
                                                                                        }
                                                                                        if (!validTrend.containsKey(docId)) {
                                                                                            validTrend.put(docId, temp);
                                                                                        } else {
                                                                                            validTrend.put(docId, validTrend.get(docId) + temp);
                                                                                        }
                                                                                    }
                                                                                    //validProperties.put("Properties",validProperty);
                                                                                    db.collection("users").document(user.getUid()).collection("userData").document("Analysis").set(validProperty);
                                                                                    db.collection("users").document(user.getUid()).collection("userData").document("LineAnalysis").set(validTrend);

                                                                                }

                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    }

                                                });
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Error in retrieving documents from database", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle extra = getIntent().getExtras();
        String[] values = extra.getStringArray("values");

        FirebaseAuth mAuth = FirebaseAuth.getInstance(); //Grabs current instance of database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser(); //Grabs current user

        TextView timeRange = (TextView) findViewById(R.id.activityTimeRange);
        TextView exerciseAnalysis = (TextView) findViewById(R.id.exerciseAnalysis);

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
            exerciseAnalysis.setText("Analyzing all possible values");
            this.displayCaloriesBurned = true;
            this.displayActiveHours = true;
        } else {
            exerciseAnalysis.setText("Analyzing " + values[1]);
            if (values[1].equals("Calories Burned")) {
                this.displayCaloriesBurned = true;
                this.displayActiveHours = false;
            } else {
                this.displayCaloriesBurned = false;
                this.displayActiveHours = true;
            }
        }

    }

    public void generatePieGraph(View view) {
        Bundle extra = getIntent().getExtras();
        String[] values = extra.getStringArray("values");
        FirebaseAuth mAuth = FirebaseAuth.getInstance(); //Grabs current instance of database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser(); //Grabs current user
        db.collection("users").document(user.getUid()).collection("userData").document("Analysis")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            Map<String, Object> dbValues = document.getData();
                            AnyChartView anyChartView = findViewById(R.id.caloriesPieChart);
                            List<DataEntry> dataEntries = new ArrayList<>();
                            Pie pie = AnyChart.pie();
                            if (dbValues.containsKey("Biking")) {
                                Double biking = (Double) dbValues.get("Biking");
                                dataEntries.add(new ValueDataEntry("Biking", biking));
                            }
                            if (dbValues.containsKey("Walking")) {
                                Double walking = (Double) dbValues.get("Walking");
                                dataEntries.add(new ValueDataEntry("Walking", walking));
                            }
                            if (dbValues.containsKey("Running")) {
                                Double running = (Double) dbValues.get("Running");
                                dataEntries.add(new ValueDataEntry("Running", running));
                            }
                            if (dbValues.containsKey("Ballroom (slow)")) {
                                Double bSlow = (Double) dbValues.get("Ballroom (slow)");
                                dataEntries.add(new ValueDataEntry("Ballroom (slow)", bSlow));
                            }
                            if (dbValues.containsKey("Ballroom (fast)")) {
                                Double bFast = (Double) dbValues.get("Ballroom (fast)");
                                dataEntries.add(new ValueDataEntry("Ballroom (fast)", bFast));
                            }
                            if (dbValues.containsKey("Caribbean")) {
                                Double caribbean = (Double) dbValues.get("Caribbean");
                                dataEntries.add(new ValueDataEntry("Caribbean", caribbean));
                            }
                            if (dbValues.containsKey("Tap")) {
                                Double tap = (Double) dbValues.get("Tap");
                                dataEntries.add(new ValueDataEntry("Tap", tap));
                            }
                            if (dbValues.containsKey("Modern")) {
                                Double modern = (Double) dbValues.get("Modern");
                                dataEntries.add(new ValueDataEntry("Modern", modern));
                            }
                            if (dbValues.containsKey("Aerobic 4-inch step")) {
                                Double a4Step = (Double) dbValues.get("Aerobic 4-inch step");
                                dataEntries.add(new ValueDataEntry("Aerobic 4-inch step", a4Step));
                            }
                            if (dbValues.containsKey("Aerobic (General)")) {
                                Double aGeneral = (Double) dbValues.get("Aerobic (General)");
                                dataEntries.add(new ValueDataEntry("Aerobic (General)", aGeneral));
                            }
                            if (dbValues.containsKey("Aerobic (Low Impact)")) {
                                Double aLow = (Double) dbValues.get("Aerobic (Low Impact)");
                                dataEntries.add(new ValueDataEntry("Aerobic (Low Impact)", aLow));
                            }
                            if (dbValues.containsKey("Aerobic (High Impact)")) {
                                Double aHigh = (Double) dbValues.get("Aerobic (High Impact)");
                                dataEntries.add(new ValueDataEntry("Aerobic (High Impact)", aHigh));
                            }
                            if (dbValues.containsKey("Nadisodhana")) {
                                Double nad = (Double) dbValues.get("Nadisodhana");
                                dataEntries.add(new ValueDataEntry("Nadisodhana", nad));
                            }
                            if (dbValues.containsKey("Hatha")) {
                                Double hat = (Double) dbValues.get("Hatha");
                                dataEntries.add(new ValueDataEntry("Hatha", hat));
                            }
                            if (dbValues.containsKey("Sitting & Stretching")) {
                                Double sAndS = (Double) dbValues.get("Sitting & Stretching");
                                dataEntries.add(new ValueDataEntry("Sitting & Stretching", sAndS));
                            }
                            if (dbValues.containsKey("Surya Namaskar")) {
                                Double suN = (Double) dbValues.get("Surya Namaskar");
                                dataEntries.add(new ValueDataEntry("Surya Namaskar", suN));
                            }
                            if (dbValues.containsKey("Power Yoga")) {
                                Double pYoga = (Double) dbValues.get("Power Yoga");
                                dataEntries.add(new ValueDataEntry("Power Yoga", pYoga));
                            }
                            pie.data(dataEntries);
                            if (values[1].equals("Calories Burned")) {
                                pie.title("Pie graph for Calories Burned");
                            } else {
                                pie.title("Pie graph for Active Hours");
                            }
                            anyChartView.setChart(pie);
                        } else {
                            Toast.makeText(getApplicationContext(), "Task unsuccessful", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void generateLineGraph(View view) {
        Bundle extra = getIntent().getExtras();
        String[] values = extra.getStringArray("values");
        FirebaseAuth mAuth = FirebaseAuth.getInstance(); //Grabs current instance of database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser(); //Grabs current user
        //Line Graph Creation
        db.collection("users").document(user.getUid()).collection("userData").document("LineAnalysis")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            Map<String,Object> dbValues2 = document.getData();
                            AnyChartView anyChartView = findViewById(R.id.caloriesPieChart);
                            Cartesian cartesian = AnyChart.line();
                            if (values[1].equals("Calories Burned")) {
                                cartesian.yAxis(0).title("Calories burned");
                                cartesian.title("Line graph for Calories Burned");
                            } else {
                                cartesian.yAxis(0).title("Active Hours");
                                cartesian.title("Line graph for Active Hours");
                            }
                            List<DataEntry> seriesData = new ArrayList<>();
                            db.collection("users").document(user.getUid().toString()).collection("userData")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    String docId = document.getId();
                                                    if (!docId.equals("LineAnalysis") && !docId.equals("profile") && !docId.equals("Analysis")) {
                                                        if (dbValues2.containsKey(docId)) {
                                                            seriesData.add(new ValueDataEntry(convertStringToDate(docId), (Number) dbValues2.get(docId)));
                                                        }
                                                    }
                                                }
                                                cartesian.animation(true);
                                                cartesian.crosshair().enabled(true);
                                                cartesian.crosshair()
                                                        .yLabel(true)
                                                        .yStroke((Stroke)null,null,null,(String)null,(String)null);
                                                cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
                                                cartesian.xAxis(0).title("Date");
                                                cartesian.data(seriesData);
                                                anyChartView.setChart(cartesian);
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

    public void switchGraph(View view) {
        Bundle extra = getIntent().getExtras();
        String[] values = extra.getStringArray("values");
        Boolean state = Boolean.parseBoolean(values[2]);
        if (state) {
            generatePieGraph(view);
        }
        else {
            generateLineGraph(view);
        }
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

        db.collection("users").document(user.getUid()).collection("userData").document("Analysis")
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        db.collection("users").document(user.getUid()).collection("userData").document("LineAnalysis")
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
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }
}