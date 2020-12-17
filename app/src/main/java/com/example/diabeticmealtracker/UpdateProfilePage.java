package com.example.diabeticmealtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateProfilePage extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Spinner spinner;
    ArrayAdapter<CharSequence> adapter;
    Map<String, Object> userInfo = new HashMap<>(); //Creates a new map, contains data that will be placed in the user's extraData file
    String currSex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile_page);

        spinner = (Spinner) findViewById(R.id.sex);
        adapter = ArrayAdapter.createFromResource(this,R.array.sex, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                userInfo.put("Sex", currSex);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        FirebaseAuth mAuth = FirebaseAuth.getInstance(); //Grabs current instance of database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser(); //Grabs current user
        DocumentReference newRef = db.collection("users").document(user.getUid().toString()).collection("userData").document("profile");
        newRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() { //Does the .get() command with a custom onComplete
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult(); //Grab snapshot of requirements
                    EditText age = (EditText) findViewById(R.id.editTextNumberSigned);
                    EditText height = (EditText) findViewById(R.id.editTextNumberSigned2);
                    EditText weight = (EditText) findViewById(R.id.editTextNumberSigned4);
                    Spinner sexSpinner = (Spinner) findViewById(R.id.sex);

                    age.setText(document.getString("Age"));
                    height.setText(document.getString("Height"));
                    weight.setText(document.getString("Weight"));
                    currSex = document.getString("Sex");

                    sexSpinner.setSelection(adapter.getPosition(currSex));

                    Button finishButton = (Button) findViewById(R.id.button28);

                    if(document.getString("setup").equals("true")) {
                        finishButton.setVisibility(View.VISIBLE);
                    }
                    else{
                        finishButton.setVisibility(View.INVISIBLE);
                    }

                }
            }
        });
        List<String> sexArray = new ArrayList<String>();
        sexArray.add("Male");
        sexArray.add("Female");
        Spinner sexSpinner = (Spinner) findViewById(R.id.sex);
        sexSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                currSex = sexArray.get(pos);
            }

            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });
    }

    public void finishUpdateMyProfile (View view){
        finish();
    }

    public void onUpdate(View view){
        FirebaseAuth mAuth = FirebaseAuth.getInstance(); //Grabs current instance of database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser(); //Grabs current user

        EditText age = (EditText) findViewById(R.id.editTextNumberSigned);
        EditText height = (EditText) findViewById(R.id.editTextNumberSigned2);
        EditText weight = (EditText) findViewById(R.id.editTextNumberSigned4);
        Spinner sexSpinner = (Spinner) findViewById(R.id.sex);

        sexSpinner.setSelection(adapter.getPosition(currSex));


        List<String> sexArray = new ArrayList<String>();
        sexArray.add("Male");
        sexArray.add("Female");
        sexSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                currSex = sexArray.get(pos);
            }
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        if (currSex == null) {
            currSex = "Male";
        }
        userInfo.put("Age", age.getText().toString());
        userInfo.put("Height", height.getText().toString());
        userInfo.put("Weight", weight.getText().toString());
        userInfo.put("Sex", currSex);
        if((age.getText().toString().equals("")) || (height.getText().toString().equals("")) || (weight.getText().toString().equals(""))){
            Toast.makeText(getApplicationContext(), "Fill in fields", Toast.LENGTH_SHORT).show();
        }
        else if (Integer.parseInt(age.getText().toString()) >= 130){
            Toast.makeText(getApplicationContext(), "Please Enter an Actual Age", Toast.LENGTH_SHORT).show();
        }
        else if (Integer.parseInt(height.getText().toString()) >= 300){
            Toast.makeText(getApplicationContext(), "Please Enter an Actual Age", Toast.LENGTH_SHORT).show();
        }
        else if (Integer.parseInt(weight.getText().toString()) >= 600){
            Toast.makeText(getApplicationContext(), "Please Enter an Actual Age", Toast.LENGTH_SHORT).show();
        }
        else{
            String flag;
            flag = "true";
            userInfo.put("setup", flag);
            db.collection("users").document(user.getUid().toString()).collection("userData").document("profile").set(userInfo,SetOptions.merge());
            finish();
        }
    }
}