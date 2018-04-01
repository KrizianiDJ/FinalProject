package com.example.krizianidj.capstone1;

import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ListMenuActivity extends AppCompatActivity {

    Button camScan,systemScan,inFridge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_menu);

        camScan=(Button) findViewById(R.id.phoneCam_Btn);
        systemScan=(Button) findViewById(R.id.systemCam_Btn);
        inFridge=(Button) findViewById(R.id.InFridge_Btn);

        camScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),ScanBarcodeActivity.class);
                startActivity(i);

            }
        });

        systemScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),PiBarcodeScan.class);
                startActivity(i);

            }
        });

        inFridge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),InventoryActivity.class);
                startActivity(i);


            }
        });


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainMenu.class);
        startActivity(intent);
        this.finish();
    }
}
