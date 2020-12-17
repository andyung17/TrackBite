package com.example.diabeticmealtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ExerciseChoice extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_choice);
    }
    public void backExerciseChoice (View view){
        finish();
    }
    public void dancingClick (View view){
        Intent intent = new Intent (getApplicationContext(), DanceInput.class);
        startActivity(intent);
    }

    public void yogaClick (View view) {
        Intent intent  = new Intent (getApplicationContext(), YogaInput.class);
        startActivity(intent);
    }
}