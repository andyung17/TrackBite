package com.example.diabeticmealtracker;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

public class Data_Analysis_Page extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data__analysis__page);
//        Switch graphSwitch = (Switch) findViewById(R.id.graphSwitch);
//        graphSwitch.setTextOn("Pie Graph");
//        graphSwitch.setTextOff("Line Graph");
    }

    public void backDataAnalysis (View view){
        finish();
    }
    public void foodAnalysisClick (View view){
        Intent intent = new Intent (getApplicationContext(), FoodAnalysis_Page.class);
        startActivity(intent);
    }

    public void exerciseAnalysisClick (View view){
        Intent intent = new Intent (getApplicationContext(), ExerciseAnalysis_Page.class);
        startActivity(intent);
    }
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("diabetic-mealtime-tracker");
}