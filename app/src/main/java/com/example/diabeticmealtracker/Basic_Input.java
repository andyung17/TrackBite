package com.example.diabeticmealtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class Basic_Input extends AppCompatActivity implements newSaveDialog.newSaveDialogListener, updateSaveDialog.updateSaveDialogListener {

    // element variables
    EditText txtName, txtServingSize, txtFats, txtCarbohydrates, txtSugar, txtFibre, txtCalories;
    Button done, clear;
    private boolean newSave, updateSave;
    private Map<String, Object> userInfo;
    private DocumentReference savedMeals;

    // initialize
    private String name = "";
    private String meal = "";
    private String servingSize = "0";
    private String fats = "0";
    private String carbohydrates = "0";
    private String sugar = "0";
    private String fibre = "0";
    private String calories = "0";

    private String savedServingSize = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic__input);

        // add all the edit text
        txtName = (EditText) findViewById(R.id.basicInputName);
        txtServingSize = (EditText) findViewById(R.id.basicInputServing);
        txtFats = (EditText) findViewById(R.id.basicInputFat);
        txtCarbohydrates = (EditText) findViewById(R.id.basicInputCarbs);
        txtSugar = (EditText) findViewById(R.id.basicInputSugar);
        txtFibre = (EditText) findViewById(R.id.basicInputFibre);
        txtCalories = (EditText) findViewById(R.id.basicInputCalories);
        // submit button
        done = (Button) findViewById(R.id.basicInputAdd);
        // clear button
        clear = (Button) findViewById(R.id.basicInputClear);
        // Firestore
        FirebaseAuth mAuth = FirebaseAuth.getInstance(); //Grabs current instance of database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser(); //Grabs current user


        // Spinner for the meal times (breakfast, lunch, dinner)
        Spinner spnMeal = (Spinner) findViewById(R.id.mealSpinner);
        // List of meals
        List<String> mealArray = new ArrayList<String>();
        mealArray.add("Breakfast");
        mealArray.add("Lunch");
        mealArray.add("Dinner");
        mealArray.add("Snack");
        // Array adapter (context, layout of spinner, and array values in the spinner)
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mealArray);
        // set drop down menu
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnMeal.setAdapter(adapter);

        // done button
        done.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // The current date when the button was pressed
                Date currentTime = Calendar.getInstance().getTime();
                String formattedDate = DateFormat.getDateInstance(DateFormat.LONG).format(currentTime);
                formattedDate = formattedDate.replace(",", "");
                String[] splitDate = formattedDate.split(" ");
                String month = convertMonthNum(splitDate[0]);
                String dateNum = formatDate(splitDate[1]);
                String year = splitDate[2];
                String date = year + month + dateNum;

                // parsing the input from the input fields
                name = initializeName(txtName.getText().toString()); // convert all names to only lowercase
                servingSize = initializeInput(txtServingSize.getText().toString().trim());
                fats = initializeInput(txtFats.getText().toString().trim());
                carbohydrates = initializeInput(txtCarbohydrates.getText().toString().trim());
                sugar = initializeInput(txtSugar.getText().toString().trim());
                fibre = initializeInput(txtFibre.getText().toString().trim());
                calories = initializeInput(txtCalories.getText().toString().trim());
                meal = spnMeal.getSelectedItem().toString();
                // push food object onto firebase based on the meal time selected
                // check if the the meal already exists and if it does, add to the current one.
                DocumentReference mealRef = db.collection("users").document(user.getUid().toString()).collection("userData").document(date).collection("Food").document(name);
                // save food to an all time database
                savedMeals = db.collection("users").document(user.getUid().toString()).collection("userData").document("savedMeals").collection("Food").document(name);
                mealRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult(); //Grab snapshot of requirements
                            userInfo = new HashMap<>();
                            // save new serving size in-case they want to update database
                            savedServingSize = servingSize;
                            if (!document.exists()) {
                                userInfo.put("name", name);
                                userInfo.put("servingSize", servingSize);
                                userInfo.put("fats", fats);
                                userInfo.put("carbohydrates", carbohydrates);
                                userInfo.put("sugar", sugar);
                                userInfo.put("fibre", fibre);
                                userInfo.put("calories", calories);
                                userInfo.put("meal", meal);
                                // setting additional input
                                userInfo.put("saturatedFat", "0");
                                userInfo.put("transFat", "0");
                                userInfo.put("cholesterol", "0");
                                userInfo.put("sodium", "0");
                                userInfo.put("protein", "0");
                                userInfo.put("calcium", "0");
                                userInfo.put("potassium", "0");
                                userInfo.put("iron", "0");
                                userInfo.put("zinc", "0");
                                userInfo.put("vitaminA", "0");
                                userInfo.put("vitaminB", "0");
                                userInfo.put("vitaminC", "0");
                                mealRef.set(userInfo);
                            } else {
                                // setting basic input
                                userInfo.put("name", name);
                                userInfo.put("servingSize", addTwoStrings(servingSize, document.getString("servingSize")));
                                userInfo.put("fats", fats);
                                userInfo.put("carbohydrates", carbohydrates);
                                userInfo.put("sugar", sugar);
                                userInfo.put("fibre", fibre);
                                userInfo.put("calories", calories);
                                userInfo.put("meal", meal);
                                // setting additional input
                                userInfo.put("saturatedFat", document.getString("saturatedFat"));
                                userInfo.put("transFat", document.getString("transFat"));
                                userInfo.put("cholesterol", document.getString("cholesterol"));
                                userInfo.put("sodium", document.getString("sodium"));
                                userInfo.put("protein", document.getString("protein"));
                                userInfo.put("calcium", document.getString("calcium"));
                                userInfo.put("potassium", document.getString("potassium"));
                                userInfo.put("iron", document.getString("iron"));
                                userInfo.put("zinc", document.getString("zinc"));
                                userInfo.put("vitaminA", document.getString("vitaminA"));
                                userInfo.put("vitaminB", document.getString("vitaminB"));
                                userInfo.put("vitaminC", document.getString("vitaminC"));
                                mealRef.set(userInfo);
                            }
                        }
                    }
                });
                // Check if the meal is a saved meal
                savedMeals.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult(); //Grab snapshot of requirements
                            if (!document.exists()) {
                                // Open Dialog that asks user if they want to save a new food to an all time database.
                                //userInfo.put("servingSize", savedServingSize);
                                newSaveDialog();
                            } else {
                                // Open Dialog that asks user if they want to update the food in their database
                                //userInfo.put("servingSize", savedServingSize);
                                updateSaveDialog();
                            }
                        }
                    }
                });
                //db.collection("users").document(user.getUid().toString()).collection("userData").document(date).collection("Food").document(name).set(userInfo, SetOptions.merge());
                Map<String, Object> Date = new HashMap<>();
                Date.put("Date", date);
                // Total
                DocumentReference docRef = db.collection("users").document(user.getUid().toString()).collection("userData").document(date).collection("Total").document("Total");
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() { //Does the .get() command with a custom onComplete
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult(); //Grab snapshot of requirements
                            Map<String, Object> totals = new HashMap<>();
                            //String calories = document.getString("CaloriesBurned").substring(0,document.getString("CaloriesBurned").indexOf("."));
                            if (!document.exists()) {
                                // exercise
                                totals.put("Total Burned Calories", "0");
                                totals.put("Total Active Hours", "0");
                                // basic
                                totals.put("Total serving size", servingSize);
                                totals.put("Total carbs", multipleTwoStrings(servingSize, carbohydrates));
                                totals.put("Total fats", multipleTwoStrings(servingSize, fats));
                                totals.put("Total calories", multipleTwoStrings(servingSize, calories));
                                totals.put("Total fiber", multipleTwoStrings(servingSize, fibre));
                                totals.put("Total sugar", multipleTwoStrings(servingSize, sugar));
                                // detailed
                                totals.put("Total saturated fats", "0");
                                totals.put("Total trans fats", "0");
                                totals.put("Total cholesterol", "0");
                                totals.put("Total sodium", "0");
                                totals.put("Total protein", "0");
                                totals.put("Total calcium", "0");
                                totals.put("Total potassium", "0");
                                totals.put("Total iron", "0");
                                totals.put("Total zinc", "0");
                                totals.put("Total vitamin a", "0");
                                totals.put("Total vitamin b", "0");
                                totals.put("Total vitamin c", "0");
                                // Add to the database
                                db.collection("users").document(user.getUid()).collection("userData").document(date).collection("Total").document("Total").set(totals, SetOptions.merge());
                            } else {
                                // basic
                                float totalServingSize = Float.parseFloat(document.getString("Total serving size"));
                                float totalFats = Float.parseFloat(document.getString("Total fats"));
                                float totalCarbs = Float.parseFloat(document.getString("Total carbs"));
                                float totalSugar = Float.parseFloat(document.getString("Total sugar"));
                                float totalFibre = Float.parseFloat(document.getString("Total fiber"));
                                float totalCalories = Float.parseFloat(document.getString("Total calories"));
                                // adding to the total
                                totalServingSize += Float.parseFloat(servingSize);
                                totalFats += Float.parseFloat(multipleTwoStrings(servingSize, fats));
                                totalCarbs += Float.parseFloat(multipleTwoStrings(servingSize, carbohydrates));
                                totalSugar += Float.parseFloat(multipleTwoStrings(servingSize, sugar));
                                totalFibre += Float.parseFloat(multipleTwoStrings(servingSize, fibre));
                                totalCalories += Float.parseFloat(multipleTwoStrings(servingSize, calories));
                                totals.put("Total serving size", String.valueOf(totalServingSize));
                                totals.put("Total fats", String.valueOf(totalFats));
                                totals.put("Total carbs", String.valueOf(totalCarbs));
                                totals.put("Total sugar", String.valueOf(totalSugar));
                                totals.put("Total fiber", String.valueOf(totalFibre));
                                totals.put("Total calories", String.valueOf(totalCalories));
                                db.collection("users").document(user.getUid().toString()).collection("userData").document(date).collection("Total").document("Total").set(totals, SetOptions.merge());
                            }
                        }
                    }
                });
                // notification saying the input has been successfully added to firebase
                Toast.makeText(Basic_Input.this, "Input Successful", Toast.LENGTH_LONG).show();
            }
        });
        // clear button
        clear.setOnClickListener(new View.OnClickListener() {
            // clears all the input fields when the button is pressed
            public void onClick(View view) {
                txtName.setText("");
                txtServingSize.setText("");
                txtFats.setText("");
                txtCarbohydrates.setText("");
                txtSugar.setText("");
                txtFibre.setText("");
                txtCalories.setText("");
            }
        });

        Date currentTime = Calendar.getInstance().getTime();
        String formattedDate = DateFormat.getDateInstance(DateFormat.LONG).format(currentTime);
        formattedDate = formattedDate.replace(",", "");
        String[] splitDate = formattedDate.split(" ");
        String month = convertMonthNum(splitDate[0]);
        String dateNum = formatDate(splitDate[1]);
        String year = splitDate[2];
        String date = year + month + dateNum;

        Map<String, Object> Date = new HashMap<>();
        Date.put("Date", date);
        // db.collection("users").document(user.getUid().toString()).set(date);
        db.collection("users").document(user.getUid().toString()).collection("userData").document(date).set(Date);
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

    // initialize the input
    public String initializeInput(String field) {
        Float testFloat;
        boolean isNum = true;
        // if they input not a number
        try {
            testFloat = Float.parseFloat(field);
        } catch (NumberFormatException e) {
            Toast.makeText(Basic_Input.this, "Please enter in a number", Toast.LENGTH_LONG).show();
            isNum = false;
        }
        // if they input empty
        if (field.equals("") || isNum == false) {
            return "0";
        } else {
            return field.trim();
        }
    }

    public String initializeName(String name) {
        if (!name.equals(null) && !name.equals("")) {
            return name.trim().toLowerCase();
        } else {
            return "unnamedfood";
        }
    }


    // add two numerical string values
    public String addTwoStrings(String value1, String value2) {
        return String.valueOf(Float.parseFloat(value1) + Float.parseFloat(value2));
    }

    // multiply two numerical string values
    public String multipleTwoStrings(String value1, String value2) {
        return String.valueOf(Float.parseFloat(value1) * Float.parseFloat(value2));
    }

    // basic input dialog method

    // saving new food dialog
    public void newSaveDialog() {
        newSaveDialog newSaveDialog = new newSaveDialog();
        newSaveDialog.show(getSupportFragmentManager(), "new save dialog");
    }

    // When they click yes to saving new food
    @Override
    public void save(boolean save) {
        newSave = save;
        if (newSave == true) {
            setMeal(savedMeals, userInfo);
        }
    }

    // updating old food dialog
    public void updateSaveDialog() {
        updateSaveDialog updateSaveDialog = new updateSaveDialog();
        updateSaveDialog.show(getSupportFragmentManager(), "new save dialog");
    }

    // when they click yes to updating new food
    @Override
    public void update(boolean update) {
        updateSave = update;
        if (updateSave == true) {
            setMeal(savedMeals, userInfo);
        }
    }

    // used to set a hash map into a document reference
    public void setMeal(DocumentReference dr, Map<String, Object> map) {
        dr.set(map);
    }

    //back button
    public void backBasicInputPage(View view) {
        finish();
    }

}