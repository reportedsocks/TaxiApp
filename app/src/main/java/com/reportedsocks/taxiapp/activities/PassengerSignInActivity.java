package com.reportedsocks.taxiapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reportedsocks.taxiapp.R;
import com.reportedsocks.taxiapp.model.User;
import com.reportedsocks.taxiapp.utils.Utils;

public class PassengerSignInActivity extends AppCompatActivity {

    private TextInputLayout textInputEmailPassenger;
    private TextInputLayout textInputNamePassenger;
    private TextInputLayout textInputPasswordPassenger;
    private TextInputLayout textInputConfirmPasswordPassenger;
    private Button loginSignUpButton;
    private TextView toggleLoginSignUpTextView;
    private boolean isLoginModeActive;
    private FirebaseDatabase database;
    private DatabaseReference usersDatabaseReference;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_sign_in);
        Utils.setupUI(findViewById(R.id.passengerSignInParentView), PassengerSignInActivity.this);


        textInputEmailPassenger = findViewById(R.id.textInputEmailPassenger);
        textInputNamePassenger = findViewById(R.id.textInputNamePassenger);
        textInputPasswordPassenger = findViewById(R.id.textInputPasswordPassenger);
        textInputConfirmPasswordPassenger = findViewById(R.id.textInputConfirmPasswordPassenger);
        loginSignUpButton = findViewById(R.id.loginSignUpButton);
        toggleLoginSignUpTextView = findViewById(R.id.toggleLoginSingUpTextView);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        usersDatabaseReference = database.getReference().child("users");
    }

    private boolean validateEmail(){
        String emailInput = textInputEmailPassenger.getEditText().getText().toString().trim();
        if(emailInput.isEmpty()){
            textInputEmailPassenger.setError("Please input your email");
            return false;
        } else {
            textInputEmailPassenger.setError("");
            return true;
        }
    }
    private boolean validateName(){
        String nameInput = textInputNamePassenger.getEditText().getText().toString().trim();
        if(nameInput.isEmpty()){
            textInputNamePassenger.setError("Please input your name");
            return false;
        } else if(nameInput.length() > 15){
            textInputNamePassenger.setError("Name has to be less than 15 characters");
            return false;
        } else {
            textInputNamePassenger.setError("");
            return true;
        }
    }
    private boolean validatePassword(){
        String passwordInput = textInputPasswordPassenger.getEditText().getText().toString().trim();
        String confirmPasswordInput = textInputConfirmPasswordPassenger.getEditText().getText().toString().trim();
        if(passwordInput.isEmpty()){
            textInputPasswordPassenger.setError("Please input your password");
            return false;
        } else if(passwordInput.length() < 6){
            textInputPasswordPassenger.setError("Password has to beat least 6 characters long");
            return false;
        }else if( !isLoginModeActive && !passwordInput.equals(confirmPasswordInput)){
            textInputPasswordPassenger.setError("Passwords have to match");
            return false;
        } else {
            textInputPasswordPassenger.setError("");
            return true;
        }
    }

    public void loginSignUpUser(View view) {
        if(isLoginModeActive){
            if(!validateEmail() | !validatePassword()){
                return;
            }
            auth.signInWithEmailAndPassword(textInputEmailPassenger.getEditText().getText().toString().trim(),
                    textInputPasswordPassenger.getEditText().getText().toString().trim())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("FirebaseAuth", "signInWithEmail:success");
                                FirebaseUser user = auth.getCurrentUser();
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("FirebaseAuth", "signInWithEmail:failure", task.getException());
                                Toast.makeText(PassengerSignInActivity.this, "Sign in error",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }
                    });
        } else {
            if(!validateEmail() | !validateName() | !validatePassword()){
                return;
            }
            auth.createUserWithEmailAndPassword(textInputEmailPassenger.getEditText().getText().toString().trim(),
                    textInputPasswordPassenger.getEditText().getText().toString().trim())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("FirebaseAuth", "createUserWithEmail:success");
                                FirebaseUser user = auth.getCurrentUser();
                                updateUI(user);

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("FirebaseAuth", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(PassengerSignInActivity.this, "Registration error",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }
                    });
        }

    }
    private void updateUI(FirebaseUser firebaseUser) {
        if(isLoginModeActive){
            checkIfPassengerIsLoggedIn(firebaseUser);
        } else {
            if(firebaseUser != null){
                User user = new User();
                user.setDriver(false);
                user.setEmail(textInputEmailPassenger.getEditText().getText().toString().trim());
                user.setName(textInputNamePassenger.getEditText().getText().toString().trim());
                user.setId(firebaseUser.getUid());
                usersDatabaseReference.child(firebaseUser.getUid()).setValue(user);
                startActivity(new Intent(PassengerSignInActivity.this, PassengerMapsActivity.class));
            } else {
                return;
            }
        }
    }

    private void checkIfPassengerIsLoggedIn(final FirebaseUser firebaseUser) {
        if(firebaseUser != null){
            ValueEventListener userListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                        User user = userSnapshot.getValue(User.class);

                        if(user.getId().equals(firebaseUser.getUid())){
                            if(!user.isDriver()){
                                startActivity(new Intent(PassengerSignInActivity.this, PassengerMapsActivity.class));
                            } else {
                                Toast.makeText(PassengerSignInActivity.this, "Log in as a driver", Toast.LENGTH_LONG).show();
                                auth.signOut();
                            }
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("DriverSignInActivity", "loadPost:onCancelled", databaseError.toException());
                    // ...
                }
            };
            usersDatabaseReference.addListenerForSingleValueEvent(userListener);

        }
    }

    public void toggleLoginSignUp(View view) {
        if(isLoginModeActive){
            isLoginModeActive = false;
            loginSignUpButton.setText("Sign Up");
            toggleLoginSignUpTextView.setText("Tap to login");
            textInputNamePassenger.setVisibility(View.VISIBLE);
            textInputConfirmPasswordPassenger.setVisibility(View.VISIBLE);
        } else {
            isLoginModeActive = true;
            loginSignUpButton.setText("Log In");
            toggleLoginSignUpTextView.setText("Tap to sign up");
            textInputNamePassenger.setVisibility(View.GONE);
            textInputConfirmPasswordPassenger.setVisibility(View.GONE);
        }
    }
}
