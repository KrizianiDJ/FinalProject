package com.example.krizianidj.capstone1;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class SignIn extends AppCompatActivity {


    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private ProgressBar progressBar;
    private TextView  textViewRegister;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();
        buttonRegister= (Button) findViewById(R.id.sign);
        editTextEmail=(EditText) findViewById(R.id.emailS);
        editTextPassword=(EditText) findViewById(R.id.passwordS);
        progressBar=(ProgressBar)findViewById(R.id.progressbar) ;
        textViewRegister=(TextView) findViewById(R.id.textViewRegister);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();

            }
        });

        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Registration.class);
                startActivity(intent);
                finish();

            }
        });

    }


    private void registerUser()
    {
        String email=editTextEmail.getText().toString().trim();
        String password=editTextPassword.getText().toString().trim();

        if(email.isEmpty())
        {
            //email is empty
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            editTextEmail.setError("Please enter a valid email address");
            editTextEmail.requestFocus();
            return;
        }

        if(password.isEmpty())
        {
            //password is empty
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        if(password.length()<6)
        {
            editTextPassword.setError("Minimum length of password should be 6");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);


        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener
                (new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            progressBar.setVisibility(View.GONE);
                            Intent intent = new Intent(getApplicationContext(), MainMenu.class);
                            startActivity(intent);
                            finish();

                        }

                        else {


                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), task.getException().getMessage()
                                        , Toast.LENGTH_SHORT).show();


                        }

                    }
                });


        //if validations are ok
    }
}
