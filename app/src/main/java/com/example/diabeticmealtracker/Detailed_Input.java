package com.example.diabeticmealtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

public class Detailed_Input extends AppCompatActivity implements newSaveDialog.newSaveDialogListener, updateSaveDialog.updateSaveDialogListener {

    // element variables
    // Basic Inputs
    EditText txtName, txtServingSize, txtFats, txtCarbohydrates, txtSugar, txtFibre, txtCalories;
    // Additional Inputs
    EditText txtSFat, txtTFat, txtCholesterol, txtSodium, txtProtein, txtCalcium, txtPotassium;
    EditText txtIron, txtZinc, txtVitA, txtVitB, txtVitC;
    // Buttons
    Button done, clear;

    // initialize
    private String name = "";
    private String meal = "";
    private String servingSize = "0";
    private String fats = "0";
    private String carbohydrates = "0";
    private String sugar = "0";
    private String fibre = "0";
    private String calories = "0";
    private String sFat = "0";
    private String tFat = "0";
    private String cholesterol = "0";
    private String sodium = "0";
    private String protein = "0";
    private String calcium = "0";
    private String potassium = "0";
    private String iron = "0";
    private String zinc = "0";
    private String vitaminA = "0";
    private String vitaminB = "0";
    private String vitaminC = "0";

    private String savedServingSize = "0";

    // variables for dialog funtionality
    private boolean newSave, updateSave;
    private DocumentReference savedMeals;

    // Firestore
    private FirebaseAuth mAuth = FirebaseAuth.getInstance(); //Grabs current instance of database
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser(); //Grabs current user
    private Map<String, Object> userInfo = new HashMap<>(); //Hashmap push

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed__input);

        // Basic Input
        txtName = (EditText) findViewById(R.id.detailedName);
        txtServingSize = (EditText) findViewById(R.id.detailedServing);
        txtFats = (EditText) findViewById(R.id.detailedFats);
        txtCarbohydrates = (EditText) findViewById(R.id.detailedCarbs);
        txtSugar = (EditText) findViewById(R.id.detailedSugar);
        txtFibre = (EditText) findViewById(R.id.detailedFibre);
        txtCalories = (EditText) findViewById(R.id.detailedCalories);
        // Addition Input
        txtSFat = (EditText) findViewById(R.id.detailedSFat);
        txtTFat = (EditText) findViewById(R.id.detailedTFat);
        txtCholesterol = (EditText) findViewById(R.id.detailedCholesterol);
        txtSodium = (EditText) findViewById(R.id.detailedSodium);
        txtProtein = (EditText) findViewById(R.id.detailedProtein);
        txtCalcium = (EditText) findViewById(R.id.detailedCalcium);
        txtPotassium = (EditText) findViewById(R.id.detailedPotassium);
        txtIron = (EditText) findViewById(R.id.detailedIron);
        txtZinc = (EditText) findViewById(R.id.detailedZinc);
        txtVitA = (EditText) findViewById(R.id.detailedVitA);
        txtVitB = (EditText) findViewById(R.id.detailedVitB);
        txtVitC = (EditText) findViewById(R.id.detailedVitC);

        // submit button
        done = (Button) findViewById(R.id.detailedAdd);
        // clear button
        clear = (Button) findViewById(R.id.detailedClear);

        // Spinner for the meal times (breakfast, lunch, dinner)
        Spinner spnMeal = (Spinner) findViewById(R.id.detailedMeal);
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
                name = initializeName(txtName.getText().toString());
                servingSize = initializeInput(txtServingSize.getText().toString().trim());
                fats = initializeInput(txtFats.getText().toString().trim());
                carbohydrates = initializeInput(txtCarbohydrates.getText().toString().trim());
                sugar = initializeInput(txtSugar.getText().toString().trim());
                fibre = initializeInput(txtFibre.getText().toString().trim());
                calories = initializeInput(txtCalories.getText().toString().trim());
                meal = spnMeal.getSelectedItem().toString();
                // parsing detailed input
                sFat = initializeInput(txtSFat.getText().toString().trim());
                tFat = initializeInput(txtTFat.getText().toString().trim());
                cholesterol = initializeInput(txtCholesterol.getText().toString().trim());
                sodium = initializeInput(txtSodium.getText().toString().trim());
                protein = initializeInput(txtProtein.getText().toString().trim());
                calcium = initializeInput(txtCalcium.getText().toString().trim());
                potassium = initializeInput(txtPotassium.getText().toString().trim());
                iron = initializeInput(txtIron.getText().toString().trim());
                zinc = initializeInput(txtZinc.getText().toString().trim());
                vitaminA = initializeInput(txtVitA.getText().toString().trim());
                vitaminB = initializeInput(txtVitB.getText().toString().trim());
                vitaminC = initializeInput(txtVitC.getText().toString().trim());
                // push food object onto firebase based on the meal time selected
                // save food to an all time database
                savedMeals = db.collection("users").document(user.getUid().toString()).collection("userData").document("savedMeals").collection("Food").document(name);
                // check if the the meal already exists and if it does, add to the current one.
                DocumentReference mealRef = db.collection("users").document(user.getUid().toString()).collection("userData").document(date).collection("Food").document(name);
                mealRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult(); //Grab snapshot of requirements
                            userInfo = new HashMap<>();
                            // save new serving size in-case they want to update database
                            savedServingSize = servingSize;
                            if (!document.exists()) {
                                // setting basic input
                                userInfo.put("name", name);
                                userInfo.put("servingSize", servingSize);
                                userInfo.put("fats", fats);
                                userInfo.put("carbohydrates", carbohydrates);
                                userInfo.put("sugar", sugar);
                                userInfo.put("fibre", fibre);
                                userInfo.put("calories", calories);
                                userInfo.put("meal", meal);
                                // setting additional input
                                userInfo.put("saturatedFat", sFat);
                                userInfo.put("transFat", tFat);
                                userInfo.put("cholesterol", cholesterol);
                                userInfo.put("sodium", sodium);
                                userInfo.put("protein", protein);
                                userInfo.put("calcium", calcium);
                                userInfo.put("potassium", potassium);
                                userInfo.put("iron", iron);
                                userInfo.put("zinc", zinc);
                                userInfo.put("vitaminA", vitaminA);
                                userInfo.put("vitaminB", vitaminB);
                                userInfo.put("vitaminC", vitaminC);
                                mealRef.set(userInfo);
                            } else {
                                // basic
                                userInfo.put("name", name);
                                userInfo.put("servingSize", addTwoStrings(document.getString("servingSize"), servingSize));
                                userInfo.put("fats", fats);
                                userInfo.put("carbohydrates", carbohydrates);
                                userInfo.put("sugar", sugar);
                                userInfo.put("fibre", fibre);
                                userInfo.put("calories", calories);
                                userInfo.put("meal", meal);
                                // setting additional input
                                userInfo.put("saturatedFat", sFat);
                                userInfo.put("transFat", tFat);
                                userInfo.put("cholesterol", cholesterol);
                                userInfo.put("sodium", sodium);
                                userInfo.put("protein", protein);
                                userInfo.put("calcium", calcium);
                                userInfo.put("potassium", potassium);
                                userInfo.put("iron", iron);
                                userInfo.put("zinc", zinc);
                                userInfo.put("vitaminA", vitaminA);
                                userInfo.put("vitaminB", vitaminB);
                                userInfo.put("vitaminC", vitaminC);
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
                                userInfo.put("servingSize", savedServingSize);
                                newSaveDialog();
                            } else {
                                // Open Dialog that asks user if they want to update the food in their database
                                userInfo.put("servingSize", savedServingSize);
                                updateSaveDialog();
                            }
                        }
                    }
                });
                //db.collection("users").document(user.getUid().toString()).collection("userData").document(date).collection("Food").document(name).set(userInfo, SetOptions.merge());
                Map<String, Object> Date = new HashMap<>();
                Date.put("Date", date);
                // notification saying the input has been successfully added to firebase
                Toast.makeText(Detailed_Input.this, "Input Successful", Toast.LENGTH_LONG).show();

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
                                totals.put("Total carbs", carbohydrates);
                                totals.put("Total fats", fats);
                                totals.put("Total calories", calories);
                                totals.put("Total fiber", fibre);
                                totals.put("Total sugar", sugar);
                                // detailed
                                totals.put("Total saturated fats", sFat);
                                totals.put("Total trans fats", tFat);
                                totals.put("Total cholesterol", cholesterol);
                                totals.put("Total sodium", sodium);
                                totals.put("Total protein", protein);
                                totals.put("Total calcium", calcium);
                                totals.put("Total potassium", potassium);
                                totals.put("Total iron", iron);
                                totals.put("Total zinc", zinc);
                                totals.put("Total vitamin a", vitaminA);
                                totals.put("Total vitamin b", vitaminB);
                                totals.put("Total vitamin c", vitaminC);
                                db.collection("users").document(user.getUid()).collection("userData").document(date).collection("Total").document("Total").set(totals);
                            } else {
                                // basic
                                float totalServingSize = Float.parseFloat(document.getString("Total serving size"));
                                float totalFats = Float.parseFloat(document.getString("Total fats"));
                                float totalCarbs = Float.parseFloat(document.getString("Total carbs"));
                                float totalSugar = Float.parseFloat(document.getString("Total sugar"));
                                float totalFibre = Float.parseFloat(document.getString("Total fiber"));
                                float totalCalories = Float.parseFloat(document.getString("Total calories"));
                                // detailed
                                float totalSFat = Float.parseFloat(document.getString("Total saturated fats"));
                                float totalTFat = Float.parseFloat(document.getString("Total trans fats"));
                                float totalCholesterol = Float.parseFloat(document.getString("Total cholesterol"));
                                float totalSodium = Float.parseFloat(document.getString("Total sodium"));
                                float totalProtein = Float.parseFloat(document.getString("Total protein"));
                                float totalCalcium = Float.parseFloat(document.getString("Total calcium"));
                                float totalPotassium = Float.parseFloat(document.getString("Total potassium"));
                                float totalIron = Float.parseFloat(document.getString("Total iron"));
                                float totalZinc = Float.parseFloat(document.getString("Total zinc"));
                                float totalVitaminA = Float.parseFloat(document.getString("Total vitamin a"));
                                float totalVitaminB = Float.parseFloat(document.getString("Total vitamin b"));
                                float totalVitaminC = Float.parseFloat(document.getString("Total vitamin c"));
                                // adding to the total
                                // basic
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
                                // detailed
                                totalSFat += Float.parseFloat(multipleTwoStrings(servingSize, sFat));
                                totalTFat += Float.parseFloat(multipleTwoStrings(servingSize, tFat));
                                totalCholesterol += Float.parseFloat(multipleTwoStrings(servingSize, cholesterol));
                                totalSodium += Float.parseFloat(multipleTwoStrings(servingSize, sodium));
                                totalProtein += Float.parseFloat(multipleTwoStrings(servingSize, protein));
                                totalCalcium += Float.parseFloat(multipleTwoStrings(servingSize, calcium));
                                totalPotassium += Float.parseFloat(multipleTwoStrings(servingSize, potassium));
                                totalIron += Float.parseFloat(multipleTwoStrings(servingSize, iron));
                                totalZinc += Float.parseFloat(multipleTwoStrings(servingSize, zinc));
                                totalVitaminA += Float.parseFloat(multipleTwoStrings(servingSize, vitaminA));
                                totalVitaminB += Float.parseFloat(multipleTwoStrings(servingSize, vitaminB));
                                totalVitaminC += Float.parseFloat(multipleTwoStrings(servingSize, vitaminC));
                                totals.put("Total saturated fats", String.valueOf(totalSFat));
                                totals.put("Total trans fats", String.valueOf(totalTFat));
                                totals.put("Total cholesterol", String.valueOf(totalCholesterol));
                                totals.put("Total sodium", String.valueOf(totalSodium));
                                totals.put("Total protein", String.valueOf(totalProtein));
                                totals.put("Total calcium", String.valueOf(totalCalcium));
                                totals.put("Total potassium", String.valueOf(totalPotassium));
                                totals.put("Total iron", String.valueOf(totalIron));
                                totals.put("Total zinc", String.valueOf(totalZinc));
                                totals.put("Total vitamin a", String.valueOf(totalVitaminA));
                                totals.put("Total vitamin b", String.valueOf(totalVitaminB));
                                totals.put("Total vitamin c", String.valueOf(totalVitaminC));
                                // sending to the database
                                db.collection("users").document(user.getUid().toString()).collection("userData").document(date).collection("Total").document("Total").set(totals, SetOptions.merge());
                            }
                        }
                    }
                });
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            // clears all the input fields when the button is pressed
            public void onClick(View view) {
                //Basic Fields
                txtName.setText("");
                txtServingSize.setText("");
                txtFats.setText("");
                txtCarbohydrates.setText("");
                txtSugar.setText("");
                txtFibre.setText("");
                txtCalories.setText("");
                //Additional Fields
                txtSFat.setText("");
                txtTFat.setText("");
                txtCholesterol.setText("");
                txtSodium.setText("");
                txtProtein.setText("");
                txtCalcium.setText("");
                txtPotassium.setText("");
                txtIron.setText("");
                txtZinc.setText("");
                txtVitA.setText("");
                txtVitB.setText("");
                txtVitC.setText("");
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
//            db.collection("users").document(user.getUid().toString()).set(date);
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
            Toast.makeText(Detailed_Input.this, "Please enter in a number", Toast.LENGTH_LONG).show();
            isNum = false;
        }
        // if they input empty
        if (field.equals("") || isNum == false) {
            return "0";
        } else {
            return field;
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

    public void backDetailedInputPage(View view) {
        finish();
    }
}