package com.example.diabeticmealtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YogaInput extends AppCompatActivity {

    private double weight; // kg
    private double duration; // hrs
    private String currActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yoga_input);

        FirebaseAuth mAuth = FirebaseAuth.getInstance(); //Grabs current instance of database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser(); //Grabs current user
        DocumentReference docRef = db.collection("users").document(user.getUid().toString()).collection("userData").document("profile");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() { //Does the .get() command with a custom onComplete
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult(); //Grab snapshot of requirements
                    weight = Double.parseDouble(document.getString("Weight"));
                }
            }
        });

        currActivity = "";

        Date currentTime = Calendar.getInstance().getTime();
        String formattedDate = DateFormat.getDateInstance(DateFormat.LONG).format(currentTime);
        formattedDate = formattedDate.replace(",", "");
        String[] splitDate = formattedDate.split(" ");
        String month = convertMonthNum(splitDate[0]);
        String dateNum = formatDate(splitDate[1]);
        String year = splitDate[2];
        String date = year + month + dateNum;

        Spinner yogaSpinner = (Spinner) findViewById(R.id.danceSpinner);

        // List of Activities
        List<String> yogaArray = new ArrayList<String>();
        yogaArray.add("Nadisodhana");
        yogaArray.add("Hatha");
        yogaArray.add("Sitting & Stretching");
        yogaArray.add("Surya Namaskar");
        yogaArray.add("Power Yoga");


        // Array adapter (context, layout of spinner, and array values in the spinner)
        ArrayAdapter<String> yogaAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, yogaArray);

        // set drop down menu
        yogaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        yogaSpinner.setAdapter(yogaAdapter);

        yogaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                currActivity = yogaArray.get(pos);
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                // your code here
            }

        });
    }

    public void doneInput (View view){
        EditText durationInput = (EditText) findViewById(R.id.yogahoursInput);
        if (TextUtils.isEmpty(durationInput.getText())) {
            Toast.makeText(getApplicationContext(), "Please put an hour input", Toast.LENGTH_SHORT).show();
        }
        else if(Integer.parseInt(durationInput.getText().toString()) >=10) {
            Toast.makeText(getApplicationContext(), "Please put a realistic duration", Toast.LENGTH_SHORT).show();
        }
        else {
            float duration = Float.parseFloat(durationInput.getText().toString().trim());

            // The current date when the button was pressed
            Date currentTime = Calendar.getInstance().getTime();
            String formattedDate = DateFormat.getDateInstance(DateFormat.LONG).format(currentTime);
            formattedDate = formattedDate.replace(",", "");
            String[] splitDate = formattedDate.split(" ");
            String month = convertMonthNum(splitDate[0]);
            String dateNum = formatDate(splitDate[1]);
            String year = splitDate[2];
            String date = year + month + dateNum;


            FirebaseAuth mAuth = FirebaseAuth.getInstance(); //Grabs current instance of database
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseUser user = mAuth.getCurrentUser(); //Grabs current user

            DocumentReference oldRef = db.collection("users").document(user.getUid().toString()).collection("userData").document(date).collection("Exercise").document(this.currActivity);
            oldRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() { //Does the .get() command with a custom onComplete
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult(); //Grab snapshot of requirements
                        Map<String,Object> exerciseData = new HashMap<>();
                        //String calories = document.getString("CaloriesBurned").substring(0,document.getString("CaloriesBurned").indexOf("."));
                        if (!document.exists()) {
                            //Toast.makeText(getApplicationContext(), "pass", Toast.LENGTH_SHORT).show(); // Email is incorrectly formatted
                            exerciseData.put("Activity", currActivity);
                            exerciseData.put("CaloriesBurned", String.valueOf(caloriesBurned(METS(currActivity),duration)));
                            exerciseData.put("Duration", String.valueOf(duration));
                            db.collection("users").document(user.getUid()).collection("userData").document(date).collection("Exercise").document(currActivity).set(exerciseData);
                        }
                        else {
                            double calories = Double.parseDouble(document.getString("CaloriesBurned"));
                            double activeHours = Double.parseDouble(document.getString("Duration"));
                            calories+=caloriesBurned(METS(currActivity),duration);
                            activeHours+=duration;
                            exerciseData.put("CaloriesBurned",String.valueOf(calories));
                            exerciseData.put("Duration", String.valueOf(activeHours));
                            db.collection("users").document(user.getUid().toString()).collection("userData").document(date).collection("Exercise").document(currActivity).set(exerciseData, SetOptions.merge());

                        }
                    }
                }
            });

            DocumentReference docRef = db.collection("users").document(user.getUid().toString()).collection("userData").document(date).collection("Total").document("Total");
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() { //Does the .get() command with a custom onComplete
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult(); //Grab snapshot of requirements
                        Map<String,Object> totals = new HashMap<>();
                        //String calories = document.getString("CaloriesBurned").substring(0,document.getString("CaloriesBurned").indexOf("."));
                        if (!document.exists()) {
                            double calories = caloriesBurned(METS(currActivity),duration);
                            totals.put("Total Burned Calories", String.valueOf(calories));
                            totals.put("Total Active Hours", String.valueOf(duration));
                            // basic
                            totals.put("Total serving size", "");
                            totals.put("Total carbs", "");
                            totals.put("Total fats", "");
                            totals.put("Total calories", "");
                            totals.put("Total fiber", "");
                            totals.put("Total sugar", "");
                            // detailed
                            totals.put("Total sfat", "");
                            totals.put("Total tfat", "");
                            totals.put("Total cholesterol", "");
                            totals.put("Total sodium", "");
                            totals.put("Total protein", "");
                            totals.put("Total calcium", "");
                            totals.put("Total potassium", "");
                            totals.put("Total iron", "");
                            totals.put("Total zinc", "");
                            totals.put("Total vitamin a", "");
                            totals.put("Total vitamin b", "");
                            totals.put("Total vitamin c", "");
                            db.collection("users").document(user.getUid()).collection("userData").document(date).collection("Total").document("Total").set(totals);
                        }
                        else {
                            double calories = Float.parseFloat(document.getString("Total Burned Calories"));
                            double activeHours = Double.parseDouble(document.getString("Total Active Hours"));
                            calories+=caloriesBurned(METS(currActivity), duration);
                            activeHours+=duration;
                            totals.put("Total Burned Calories",String.valueOf(calories));
                            totals.put("Total Active Hours", String.valueOf(activeHours));
                            db.collection("users").document(user.getUid().toString()).collection("userData").document(date).collection("Total").document("Total").set(totals, SetOptions.merge());

                        }
                    }
                }
            });

            Map<String,Object> Date = new HashMap<>();
            Date.put("Date",date);
//            db.collection("users").document(user.getUid().toString()).set(date);
            db.collection("users").document(user.getUid().toString()).collection("userData").document(date).set(Date);

            finish();
            //Opening success page
            Intent intent = new Intent(getApplicationContext(), SuccessExerciseInput_Page.class);
            double calories = caloriesBurned(METS(this.currActivity),duration);
            String[] array = {this.currActivity,String.valueOf(calories)};
            startActivity(intent.putExtra("activity", array));

        }

    }

    public double METS(String activity){
        if(activity.equals("Nadisodhana")){
            return 2;
        }
        else if(activity.equals("Hatha")){
            return 2.5;
        }
        else if(activity.equals("Sitting & Stretching")){
            return 2.8;
        }
        else if(activity.equals("Surya Namaskar")){
            return 3.3;
        }
        else{
            return 4;
        }
    }

    public double caloriesBurned(double Mets, double duration){

        return (this.weight * Mets * 3.5) * duration * 60 / 200;
    }

    public void backExerciseInput (View view){
        finish();

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

    public String formatDate(String date){
        String newDate;
        if (date.length() == 1){
            newDate = "0" + date;
            return newDate;
        }else{
            return date;
        }
    }
}