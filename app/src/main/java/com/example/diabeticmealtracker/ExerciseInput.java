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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;
import java.util.Map;

public class ExerciseInput extends AppCompatActivity {

    private double weight; // kg
    private double height; // cm
    private int age; // yrs
    private String sex; // male or female
    private double duration; // hrs
    private String currActivity;
    private String currSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance(); //Grabs current instance of database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser(); //Grabs current user
        DocumentReference docRef = db.collection("users").document(user.getUid().toString()).collection("userData").document("profile");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() { //Does the .get() command with a custom onComplete
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult(); //Grab snapshot of requirements

                    age = Integer.parseInt(document.getString("Age"));
                    height = Double.parseDouble(document.getString("Height"));
                    sex = document.getString("Sex");
                    weight = Double.parseDouble(document.getString("Weight"));
                }
            }
        });

        currActivity = "";
        currSpeed = "";

        Date currentTime = Calendar.getInstance().getTime();
        String formattedDate = DateFormat.getDateInstance(DateFormat.LONG).format(currentTime);
        formattedDate = formattedDate.replace(",", "");
        String[] splitDate = formattedDate.split(" ");
        String month = convertMonthNum(splitDate[0]);
        String dateNum = formatDate(splitDate[1]);
        String year = splitDate[2];
        String date = year + month + dateNum;


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_input);

        Spinner actSpinner = (Spinner) findViewById(R.id.danceSpinner);
        Spinner bikeSpinner = (Spinner) findViewById(R.id.bikingSpinner);

        // List of Activities
        List<String> activityArray = new ArrayList<String>();
        activityArray.add("Biking");
        activityArray.add("Walking");
        activityArray.add("Running");

        List<String> bikingArray = new ArrayList<>();
        bikingArray.add("Slow");
        bikingArray.add("Moderate");
        bikingArray.add("Fast");


        // Array adapter (context, layout of spinner, and array values in the spinner)
        ArrayAdapter<String> actAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, activityArray);
        ArrayAdapter<String> bikingAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, bikingArray);

        // set drop down menu
        actAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bikingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        actSpinner.setAdapter(actAdapter);
        bikeSpinner.setAdapter(bikingAdapter);

        // Gets all dynamic elements


        actSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                currActivity = activityArray.get(pos);
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                // your code here
            }

        });

        bikeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                currSpeed = bikingArray.get(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private double BMR(){


        //https://www.omnicalculator.com/health/bmr#:~:text=This%20BMR%20formula%20is%20as,males%20and%20%2D161%20for%20females.
        double value = 0;
        value = 10 * this.weight + 6.25 * this.height - 5 * this.age;

        if (this.sex.equals("Male")){
            value+= 5;
        }
        else{
            value-=161;
        }

        return value;
    }

    private double METS (){
        if (currActivity.equals("Biking")) {
            if (this.currSpeed.equals("Slow")){
                return 3.5;
            }
            else if (this.currSpeed.equals("Moderate")){
                return 5.8;
            }
            else{
                return 8;
            }
        }
        else if (currActivity.equals("Walking")) {
            if (this.currSpeed.equals("Slow")) {
                return 2;
            }
            else if (this.currSpeed.equals("Moderate")) {
                return 2.9;
            }
            else {
                return 3.5;
            }
        }
        else { // Running
            if (this.currSpeed.equals("Slow")) {
                return 11;
            }
            else if (this.currSpeed.equals("Moderate")) {
                return 11.7;
            }
            else {
                return 12.5;
            }
        }

    }

    private double calories(){
        return BMR() * METS() / 24 * this.duration;
    }

    public void backExerciseInput (View view){
        finish();
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
            this.duration = Float.parseFloat(durationInput.getText().toString().trim());
            //Toast.makeText(getApplicationContext(), "Successfully added exercise", Toast.LENGTH_SHORT).show();

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
                            exerciseData.put("CaloriesBurned", String.valueOf(calories()));
                            exerciseData.put("Duration", String.valueOf(duration));
                            db.collection("users").document(user.getUid()).collection("userData").document(date).collection("Exercise").document(currActivity).set(exerciseData);
                        }
                        else {
                            double calories = Double.parseDouble(document.getString("CaloriesBurned"));
                            double activeHours = Double.parseDouble(document.getString("Duration"));
                            calories+=calories();
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
                            totals.put("Total Burned Calories", String.valueOf(calories()));
                            totals.put("Total Active Hours", String.valueOf(duration));
                            totals.put("Total carbs","0");
                            totals.put("Total fats", "0");
                            totals.put("Total proteins","0");
                            totals.put("Total calories","0");
                            totals.put("Fiber","0");
                            totals.put("Sugar","0");
                            db.collection("users").document(user.getUid()).collection("userData").document(date).collection("Total").document("Total").set(totals);
                        }
                        else {
                            float calories = Float.parseFloat(document.getString("Total Burned Calories"));
                            double activeHours = Double.parseDouble(document.getString("Total Active Hours"));
                            calories+=calories();
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

            //finish();
            //Opening success page
            Intent intent = new Intent(getApplicationContext(), SuccessExerciseInput_Page.class);
            String[] array = {this.currActivity,String.valueOf(calories())};
            startActivity(intent.putExtra("activity", array));
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