package com.example.diabeticmealtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class DailyIntakePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_intake_page);

        Date currentTime = Calendar.getInstance().getTime();
        String formattedDate = DateFormat.getDateInstance(DateFormat.LONG).format(currentTime);
        formattedDate = formattedDate.replace(",", "");
        String[] splitDate = formattedDate.split(" ");
        String month = convertMonthNum(splitDate[0]);
        String dateNum = formatDate(splitDate[1]);
        String year = splitDate[2];
        String date = year + month + dateNum;
        String date2 = year + "-" + month + "-"+ dateNum;

        FirebaseAuth mAuth = FirebaseAuth.getInstance(); //Grabs current instance of database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser(); //Grabs current user
        DocumentReference newRef = db.collection("users").document(user.getUid().toString()).collection("userData").document(date).collection("Total").document("Total");
        newRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() { //Does the .get() command with a custom onComplete
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult(); //Grab snapshot of requirements
                    TextView calories = (TextView) findViewById(R.id.calView);
                    TextView proteins = (TextView) findViewById(R.id.proView);
                    TextView carbs = (TextView) findViewById(R.id.carbView);
                    TextView fats = (TextView) findViewById(R.id.fatView);
                    TextView sugar = (TextView) findViewById(R.id.sugarView);
                    TextView chol = (TextView) findViewById(R.id.cholView);
                    TextView intakeDate = (TextView) findViewById(R.id.textViewDate);
                    intakeDate.setText(date2);

                    if(document.exists()){
                        calories.setText(document.getString("Total calories") + "g");
                        proteins.setText(document.getString("Total protein") + "g");
                        carbs.setText(document.getString("Total carbs") + "g");
                        fats.setText(document.getString("Total fats") + "g");
                        sugar.setText(document.getString("Total sugar") + "g");
                        chol.setText(document.getString("Total cholesterol") + "g");
                    }
                    else{
                        calories.setText("0g");
                        proteins.setText("0g");
                        carbs.setText("0g");
                        fats.setText("0g");
                        sugar.setText("0g");
                        chol.setText("0g");
                    }
                }
            }
        });

    }

    public void doneButtonClick (View view){
        finish();
    }

    // converts the month into its numeric form
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

    public String formatDate(String date) {
        String newDate;
        if (date.length() == 1) {
            newDate = "0" + date;
            return newDate;
        } else {
            return date;
        }
    }

}