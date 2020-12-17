package com.example.diabeticmealtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterPage extends AppCompatActivity {

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);
    }
    public void backRegisterPage (View view){
        Intent intent = new Intent (getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
    public void FinishRegister (View view){
        mAuth = FirebaseAuth.getInstance(); //Grabs current instance of database
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        EditText email = (EditText) findViewById(R.id.textEmailRegister);
        EditText username = (EditText) findViewById(R.id.textUsernameRegister);
        EditText password = (EditText) findViewById(R.id.textPasswordRegister);
        EditText confirmPassword = (EditText) findViewById(R.id.textConfirmPasswordRegister);

        if (!email.getText().toString().equals("") && !username.getText().toString().equals("") && !password.getText().toString().equals("") && !confirmPassword.getText().toString().equals("")) {
            if (password.getText().toString().equals(confirmPassword.getText().toString()) && email.getText().toString().contains("@") && email.getText().toString().contains(".") && email.getText().toString().indexOf("@") < email.getText().toString().indexOf(".")) { // checks that passwords match, email contains an @ as well as a period signifying proper email format
                mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information

                                    FirebaseUser user = mAuth.getCurrentUser(); //Grabs current user

                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder() //Creates list of changes (this case, only display name gets changed)
                                            .setDisplayName(username.getText().toString())
                                            .build();

                                    user.updateProfile(profileUpdates); //Updates current user with display name

                                    Map<String, Object> userInfo = new HashMap<>(); //Creates a new map, contains data that will be placed in the user's extraData file

                                        userInfo.put("email", email.getText().toString());
                                        userInfo.put("name", username.getText().toString());
                                        userInfo.put("setup", "false");


                                    db.collection("users").document(user.getUid().toString()).collection("userData").document("profile").set(userInfo); //Fills document with data in map
                                    db.collection("users").document(user.getUid().toString()).set(userInfo);
                                    finish(); //Finish
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(getApplicationContext(), "Firebase Authentication Error", Toast.LENGTH_SHORT).show();
                                }

                                // ...
                            }
                        });

            }
            else {
                if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Password fields do not match", Toast.LENGTH_SHORT).show(); // Passwords don't match
                }
                else {
                    Toast.makeText(getApplicationContext(), "Incorrect e-mail format", Toast.LENGTH_SHORT).show(); // Email is incorrectly formatted
                }
            }
        }

        else{
            Toast.makeText(getApplicationContext(), "Please enter all valid fields", Toast.LENGTH_SHORT).show();
        }




    }
}