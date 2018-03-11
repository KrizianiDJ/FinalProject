package com.example.krizianidj.capstone1;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import java.net.URISyntaxException;

public class Registration extends AppCompatActivity{
    private Socket socket;

    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextPasswordRe ;
    private EditText editTextFirst ;
    private EditText editTextLast ;
    private EditText editTextPhone ;
    private TextView textViewSignIn;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private Server server;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        buttonRegister= (Button) findViewById(R.id.buttonRegister);
        editTextEmail=(EditText) findViewById(R.id.editTextEmail);
        editTextPassword=(EditText) findViewById(R.id.editTextPassword);
        editTextPasswordRe=(EditText) findViewById(R.id.editTextPasswordRe);
        editTextFirst=(EditText) findViewById(R.id.editTextFirstName);
        editTextLast=(EditText) findViewById(R.id.editTextLastName);
        editTextPhone=(EditText) findViewById(R.id.editTextPhone);
        progressBar=(ProgressBar)findViewById(R.id.progressbar) ;
        textViewSignIn=(TextView) findViewById(R.id.textViewSignin);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();

            }
        });

        textViewSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignIn.class);
                startActivity(intent);
                finish();

            }
        });




    }


    private void registerUser()
    {
        final String email=editTextEmail.getText().toString().trim();
        final String firstname=editTextFirst.getText().toString().trim();
        final String lastname=editTextLast.getText().toString().trim();
        final String phone=editTextPhone.getText().toString().trim();
        String password=editTextPassword.getText().toString().trim();
        String password2=editTextPasswordRe.getText().toString().trim();

        if(email.isEmpty())
        {
            //email is empty
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }
        if(firstname.isEmpty())
        {
            //first name is empty
            editTextFirst.setError("First Name is required");
            editTextFirst.requestFocus();
            return;
        }
        if(lastname.isEmpty())
        {
            //last name is empty
            editTextLast.setError("Last Name is required");
            editTextLast.requestFocus();
            return;
        }
        if(phone.isEmpty())
        {
            //phone is empty
            editTextPhone.setError("Phone is required");
            editTextPhone.requestFocus();
            return;
        }

        if(!Patterns.PHONE.matcher(phone).matches())
        {
            editTextPhone.setError("Please enter a valid phone number");
            editTextPhone.requestFocus();
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

        if(!password.matches(password2))
        {
            editTextPassword.setError("Password must match");
            editTextPassword.requestFocus();
            editTextPasswordRe.setError("Password must match");
            editTextPasswordRe.requestFocus();
            return;

        }

        progressBar.setVisibility(View.VISIBLE);


        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener
                (new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            progressBar.setVisibility(View.GONE);
                            FirebaseUser mUser=mAuth.getCurrentUser();
                            String id=mUser.getUid();
                            Addusertodatabase(id,email,phone,firstname,lastname);
                            Intent intent = new Intent(getApplicationContext(), Registration2.class);
                            startActivity(intent);
                            finish();

                        }

                        else {

                            if(task.getException() instanceof FirebaseAuthUserCollisionException)
                            {
                                progressBar.setVisibility(View.GONE);
                                editTextEmail.setError("Email already registered");
                                editTextEmail.requestFocus();
                                return;
                            }
                            else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), task.getException().getMessage()
                                        , Toast.LENGTH_SHORT).show();

                            }
                        }

                    }
                });


    }

    private void Addusertodatabase(String id,String email, String phone, String firstname, String lastname)
    {

        try{
            //server url
            server=new Server();
            socket= IO.socket(server.getAddress());




        }
        catch(URISyntaxException e){}

        socket.connect();
        socket.emit("AddUser",id,firstname,lastname,email,phone);
}



}
