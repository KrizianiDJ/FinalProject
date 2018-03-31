package com.example.krizianidj.capstone1;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.hardware.Camera;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by KrizianiDJ on 3/20/2018.
 */

public class ScanBarcodeActivity extends AppCompatActivity {
    SurfaceView cameraPreview;
    private static final int MY_PERMISSION_REQUEST_CAMERA = 2569;
    BarcodeTempStorage BarcodeList;
    private Button doneBtn;
    private EditText resultView;
    private Server server;
    private Socket socket;
    private FirebaseAuth mAuth;
    String id,url;
    String barcodedata;
    ArrayList<String> BarcodeList2= new ArrayList<String>();
    ArrayList<String> AddedList= new ArrayList<String>();
    ArrayList<String> ErrorList= new ArrayList<String>();

    {
        try {
            //server url
            server = new Server();
            socket = IO.socket(server.getAddress());


        } catch (URISyntaxException e){}

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_barcode);
        BarcodeList=new BarcodeTempStorage();

        socket.connect();
        mAuth= FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String id = mUser.getUid();
        socket.emit("Getaddres",id);
        doneBtn=(Button)findViewById(R.id.done_Btn);
        resultView=(EditText) findViewById(R.id.resultView);

        //socket.emit("AndroidReqStart","Android says start camera");
        socket.on("SendingAdd", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {

                runOnUiThread(new Runnable() {
                    public void run() {
                        JSONArray dataReceive =(JSONArray) args[0];
                        try{
                            url= dataReceive.get(0).toString();
                        }
                        catch(Exception e)
                        {
                            Toast.makeText(getApplicationContext(),e.getMessage()
                                    , Toast.LENGTH_LONG).show();

                        }
                    }
                });
            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String result=BarcodeList.resultString();
                resultView.setText(result);
               BarcodeList2=BarcodeList.getBarcodeList();

                mAuth= FirebaseAuth.getInstance();
                FirebaseUser mUser = mAuth.getCurrentUser();
                String id = mUser.getUid();
                BarcodeList.sendToDB(socket,id);

                AddedList=BarcodeList.getAddedList();
                ErrorList=BarcodeList.getErrorList();

                for(int i=0;i<AddedList.size();i++) {
                    Log.i("AddedList:",""+AddedList.get(i));
                }
               final Intent myIntent = new Intent(getApplicationContext(), BarcodeResultActivity.class);
                //myIntent.putStringArrayListExtra("BarcodeList", BarcodeList2);
                new Thread(new Runnable()
                {

                    @Override
                    public void run()
                    {
                            try
                            {
                                if(AddedList.isEmpty())
                                {
                                    AddedList.add("Empty");
                                }
                                if(ErrorList.isEmpty())
                                {
                                    ErrorList.add("Empty");
                                }
                                myIntent.putExtra("AddedList", AddedList);
                                myIntent.putExtra("ErrorList", ErrorList);
                                startActivity(myIntent);
                                finish();
                            }
                            catch (Exception e)
                            {
                                // ooops
                            }
                    }
                }).start();


            }
        });
        cameraPreview = (SurfaceView) findViewById(R.id.camera_preview);
        createCameraSource();
    }



    private void ReadData()
    {/*
        for(int i=0; i<BarcodeList2.size(); i++)
        {
            final String barcode=BarcodeList2.get(i);

            socket.emit("GetProductData",barcode,id);
            socket.on("BarcodeRead", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {

                    runOnUiThread(new Runnable() {
                        public void run() {
                            JSONArray dataReceive =(JSONArray) args[0];
                            try{
                                barcodedata = dataReceive.get(0).toString();

                                if (barcodedata.matches("nodata"))
                                {
                                    ErrorList.add(barcode);
                                }

                                else
                                {
                                    AddedList.add(barcodedata);
                                }
                            }
                            catch(Exception e)
                            {
                                System.out.print(e.getMessage());

                            }
                        }
                    });
                }
            });


        }*/
    }

    private void createCameraSource() {
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this).build();

        final CameraSource cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1300,700)
                .build();

        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                if (ActivityCompat.checkSelfPermission(ScanBarcodeActivity.this,android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    ActivityCompat.requestPermissions(ScanBarcodeActivity.this, new String[]{android.Manifest.permission.CAMERA},
                            MY_PERMISSION_REQUEST_CAMERA);
                    return;
                }
                try {


                    cameraSource.start(cameraPreview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() > 0) {

                    Barcode thisCode = barcodes.valueAt(0);
                    String bar = thisCode.rawValue;
                   BarcodeList.AddBarcode(bar);
                    //textView.setText(thisCode.rawValue);
                    //socket.emit("BarcodeDetected", thisCode.rawValue);
                    ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_ALARM, 200);
                    toneGen1.startTone(ToneGenerator.TONE_DTMF_8, 100);
                    //intent intent= new Intent();
                    // intent.putExtra("barcode",barcodes.valueAt(0));
                    //setResult(CommonStatusCodes.SUCCESS,intent);
                    //finish();

                    try {

                        //sleep 1 seconds
                        Thread.sleep(1000);


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


    }



}