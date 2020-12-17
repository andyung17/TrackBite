package com.example.diabeticmealtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class ProfilePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        FirebaseAuth mAuth = FirebaseAuth.getInstance(); //Grabs current instance of database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser(); //Grabs current user

        DocumentReference docRef = db.collection("users").document(user.getUid().toString()).collection("userData").document("profile");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() { //Does the .get() command with a custom onComplete
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult(); //Grab snapshot of requirements

                    TextView age = (TextView) findViewById(R.id.ageTextView);
                    TextView height = (TextView) findViewById(R.id.heightTextView);
                    TextView sex = (TextView) findViewById(R.id.sexTextView);
                    TextView weight = (TextView) findViewById(R.id.weightTextView);
                    age.setText(document.getString("Age"));
                    height.setText(document.getString("Height"));
                    sex.setText(document.getString("Sex"));
                    weight.setText(document.getString("Weight"));
                }
            }
        });
    }

    @Override
    public void onRestart(){
        super.onRestart();
        FirebaseAuth mAuth = FirebaseAuth.getInstance(); //Grabs current instance of database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser(); //Grabs current user
        DocumentReference docRef = db.collection("users").document(user.getUid().toString()).collection("userData").document("profile");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() { //Does the .get() command with a custom onComplete
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult(); //Grab snapshot of requirements

                    TextView age = (TextView) findViewById(R.id.ageTextView);
                    TextView height = (TextView) findViewById(R.id.heightTextView);
                    TextView sex = (TextView) findViewById(R.id.sexTextView);
                    TextView weight = (TextView) findViewById(R.id.weightTextView);
                    age.setText(document.getString("Age"));
                    height.setText(document.getString("Height"));
                    sex.setText(document.getString("Sex"));
                    weight.setText(document.getString("Weight"));
                }
            }
        });
        onStart();
    }

    public void backMyProfileClick (View view){
        finish();
    }
    public void changeMyProfile (View view){
        Intent intent = new Intent (getApplicationContext(), UpdateProfilePage.class);
        startActivity(intent);
    }



}