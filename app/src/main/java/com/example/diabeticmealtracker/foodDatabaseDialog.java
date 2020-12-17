package com.example.diabeticmealtracker;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class foodDatabaseDialog extends AppCompatDialogFragment {

    // variables
    private foodDatabaseDialogListener listener;
    private EditText servingSize;
    private Spinner spnMeal;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // MyDiaLogTheme is found in themes.xml
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);


        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.food_database_layout, null);

        builder.setView(view)
                .setTitle("Add food to your daily intake?")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String serving = servingSize.getText().toString().trim();
                        String meal = spnMeal.getSelectedItem().toString();
                        boolean add = false;
                        listener.add(add, serving, meal);
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String serving = servingSize.getText().toString().trim();
                        String meal = spnMeal.getSelectedItem().toString();
                        boolean add = true;
                        listener.add(add, serving, meal);
                    }
                });

        servingSize = (EditText) view.findViewById(R.id.foodDatabaseServing);
        spnMeal = (Spinner) view.findViewById(R.id.databaseSpinner);
        List<String> mealArray = new ArrayList<String>();
        mealArray.add("Breakfast");
        mealArray.add("Lunch");
        mealArray.add("Dinner");
        mealArray.add("Snack");
        // Array adapter (context, layout of spinner, and array values in the spinner)
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, mealArray);
        // set drop down menu
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnMeal.setAdapter(adapter);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (foodDatabaseDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement foodDatabaseDialogListener");
        }
    }

    public interface foodDatabaseDialogListener {
        void add(boolean add, String servingSize, String meal);
    }
}
