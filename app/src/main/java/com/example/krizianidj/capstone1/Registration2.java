package com.example.krizianidj.capstone1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

public class Registration2 extends AppCompatActivity {

    private EditText editTextFullName, editTextDisplayName, editTextPhone;
    private Button btnSave;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration2);

        editTextFullName=(EditText) findViewById(R.id.editTextFullName);
        editTextDisplayName=(EditText) findViewById(R.id.editTextDisplayName);
        editTextPhone=(EditText) findViewById(R.id.editTextPhone);
        btnSave=(Button) findViewById(R.id.buttonSave);
        progressBar=(ProgressBar) findViewById(R.id.progressbar);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveInfo();
            }
        });

    }

    private void saveInfo(){

        String fullname, displayname, phone;
        fullname=editTextFullName.getText().toString().trim();
        displayname=editTextDisplayName.getText().toString().trim();
        phone=editTextPhone.getText().toString().trim();

        if(fullname.isEmpty())
        {
            editTextFullName.setError("Full Name is required");
            editTextFullName.requestFocus();
            return;
        }

        if(phone.isEmpty())
        {
            editTextPhone.setError("Phone is required");
            editTextPhone.requestFocus();
            return;
        }

        if(!Patterns.PHONE.matcher(phone).matches())
        {
            editTextPhone.setError("Invalid phone format");
            editTextPhone.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);




    }
}
