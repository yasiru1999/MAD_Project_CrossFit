package com.example.crossfitappv1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;


import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    EditText fullname,email,password,dob,CurWeight,GoalWeight,height;
    Button signup;
    TextView Login;
    RadioButton isMale,isFemale;
    ProgressBar progressBar;

    private FirebaseAuth mAuth;
    boolean valid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        fullname = findViewById(R.id.editTxtRegFullName);
        email = findViewById(R.id.editTxtEmail);
        password = findViewById(R.id.editTxtPassword);
        dob = findViewById(R.id.editTxtDOB);
        CurWeight = findViewById(R.id.etCWeight);
        GoalWeight = findViewById(R.id.etGWeight);
        height = findViewById(R.id.etHeight);
        isMale = findViewById(R.id.radioBtnMale);
        isFemale = findViewById(R.id.radioBtnFemale);
        signup = findViewById(R.id.btnSignUp);
        Login = findViewById(R.id.RegToLogin);

        progressBar = findViewById(R.id.progressbar);

        signup.setOnClickListener(this);


        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSignUp:
                registerUser();
                break;

        }
    }

    private void registerUser() {
        String Email = email.getText().toString().trim();
        String Fullname = fullname.getText().toString().trim();
        String Password = password.getText().toString().trim();
        String DOB = dob.getText().toString().trim();
        String CurrentWei = CurWeight.getText().toString().trim();
        String GoalWei = GoalWeight.getText().toString().trim();
        String Height = height.getText().toString().trim();
        String male = isMale.getText().toString().trim();
        String female = isFemale.getText().toString().trim();

        checkField(fullname);
        checkField(email);
        checkField(password);
        checkField(dob);
        checkField(CurWeight);
        checkField(GoalWeight);
        checkField(height);

        //radiobutton validation
        if( ! (isMale.isChecked() || isFemale.isChecked())){
            Toast.makeText(RegisterActivity.this,"Select the geneder",Toast.LENGTH_SHORT).show();
            return;
        }


        if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
            email.setError("Please provide valid email");
            email.requestFocus();
            return;
        }
        if (valid) {
            progressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(Email, Password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                Users user = new Users(Fullname, Email, DOB, CurrentWei, GoalWei, Height);
                                if (isMale.isChecked()){
                                    user.setMale("1");
                                }
                                if (isFemale.isChecked()){
                                    user.setFemale("1");
                                }
                                user.setUser("1");
                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            Toast.makeText(RegisterActivity.this, "Registration Successfully", Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.VISIBLE);
                                            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(RegisterActivity.this, "Registration 2 Failed", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
        }
    }


    private boolean checkField(EditText textField) {
        if (textField.getText().toString().isEmpty()){
            textField.setError("Error");
            valid = false;
        }else
        {
            valid = true;
        }
        return valid;
    }

}