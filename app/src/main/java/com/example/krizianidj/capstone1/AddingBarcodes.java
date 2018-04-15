package com.example.krizianidj.capstone1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.net.URISyntaxException;

public class AddingBarcodes extends AppCompatActivity {
    Socket socket;
    Server server;
    private EditText barcodeText,nameText, descriptionText,categoriesText,brandText;
    Button AddDevice;
    ProgressBar progressBar;
    Button scan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_barcodes);
        barcodeText=(EditText) findViewById(R.id.editTextBarcode);
        nameText=(EditText) findViewById(R.id.editTextName);
        descriptionText=(EditText) findViewById(R.id.editTextDescription);
        categoriesText=(EditText) findViewById(R.id.editTextCategories);
        brandText=(EditText) findViewById(R.id.editTextBrand);
        AddDevice=(Button) findViewById(R.id.buttonAdd);
        progressBar=(ProgressBar) findViewById(R.id.progressbarProd);
        scan=(Button) findViewById(R.id.scanbtn);

        try {
            //server url
            server=new Server();
            socket = IO.socket(server.getAddress());

        } catch (URISyntaxException e) {
        }

        socket.connect();

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent= new Intent (getApplicationContext(), ScanB.class);
                  startActivityForResult(intent,0);

            }
        });




        AddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddProduct();


            }
        });


    }

    @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
               if (requestCode == 0) {
                        if(resultCode== CommonStatusCodes.SUCCESS)
                            {
                                        if(data!=null){
                                   Barcode barcode=data.getParcelableExtra("barcode");
                                   barcodeText.setText(barcode.displayValue);
                               }
                            else{
                                   barcodeText.setText("No Barcode Found");
                               }
                        }

                            } else {
                       super.onActivityResult(requestCode, resultCode, data);
                    }
           }

    private void AddProduct() {

        final String barcode,name,description,categories,brand;
        barcode=barcodeText.getText().toString().trim();
        name=nameText.getText().toString().trim();
        description=descriptionText.getText().toString().trim();
        categories=categoriesText.getText().toString().trim();
        brand=brandText.getText().toString().trim();



        if (barcode.isEmpty()) {
            barcodeText.setError("A barcode is required");
            barcodeText.requestFocus();
            return;
        }

        if (name.isEmpty()) {
            nameText.setError("A Product name is required");
            nameText.requestFocus();
            return;
        }
        if (categories.isEmpty()) {
            categoriesText.setError("At least one product category is required");
            categoriesText.requestFocus();
            return;
        }
        if (description.isEmpty()) {
            descriptionText.setError("A description of the product is required");
            descriptionText.requestFocus();
            return;
        }
        if (brand.isEmpty()) {
            brandText.setError("The product brand is required");
            brandText.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        try {
            //server url
            server=new Server();
            socket = IO.socket(server.getAddress());

        } catch (URISyntaxException e) {
        }

        socket.connect();
        socket.emit("AddBarcodeInfo", barcode,name,description,categories,brand);

        progressBar.setVisibility(View.GONE);

        Intent intent = new Intent(getApplicationContext(), MainMenu.class);
        startActivity(intent);
        finish();
    }




}

