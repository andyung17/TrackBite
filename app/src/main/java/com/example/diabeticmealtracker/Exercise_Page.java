package com.example.diabeticmealtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Exercise_Page extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise__page);
    }
    public void wellBeingChoiceClick (View view){
        Intent intent = new Intent (getApplicationContext(), ExerciseChoice.class);
        startActivity(intent);
    }
    public void backExercisePage (View view){
        finish();
    }
    public void cardioClick (View view){
        Intent intent = new Intent (getApplicationContext(), ExerciseInput.class);
        startActivity(intent);
    }
}