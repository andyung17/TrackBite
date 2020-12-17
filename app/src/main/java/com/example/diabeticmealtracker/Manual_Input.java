package com.example.diabeticmealtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Manual_Input extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual__input);
    }

    public void onBasicInput(View view){
        Intent intent = new Intent (Manual_Input.this, Basic_Input.class);
        startActivity(intent);
    }

    public void databaseButtonClick (View view){
        Intent intent = new Intent (getApplicationContext(), Database_Page.class);
        startActivity(intent);
    }
    public void onDetailedInput(View view){
        Intent intent = new Intent (Manual_Input.this, Detailed_Input.class);
        startActivity(intent);
    }
    public void backManualInputPage (View view){
        finish();
    }
}