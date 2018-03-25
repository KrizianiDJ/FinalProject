package com.example.krizianidj.capstone1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.krizianidj.capstone1.Camera;
import com.example.krizianidj.capstone1.R;
import com.example.krizianidj.capstone1.ScanBarcodeActivity;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

/**
 * Created by KrizianiDJ on 3/20/2018.
 */

public class BarcodeActivity extends Activity {

    TextView barcodeResult;
    Button barcode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.barcode_activity);
        barcodeResult=(TextView)findViewById(R.id.barcode_result);
       barcode=(Button) findViewById(R.id.scan_barcode);


        barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent (getApplicationContext(), ScanBarcodeActivity.class);
                startActivityForResult(intent,0);

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
                   barcodeResult.setText("Barcode value: "+barcode.displayValue);
                }
                else{
                    barcodeResult.setText("No Barcode Found");
                }
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
