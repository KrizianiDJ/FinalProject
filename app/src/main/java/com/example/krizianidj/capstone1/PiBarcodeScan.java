package com.example.krizianidj.capstone1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URISyntaxException;

public class PiBarcodeScan extends AppCompatActivity {

    ImageView imageView;
    private Server server;
    private Socket socket;
    private FirebaseAuth mAuth;
    private Button doneBtn;
    private EditText resultView;
    //TextView textView;
    String url;
    SparseArray<Barcode> barcodes, prevBarcode;
    BarcodeTempStorage BarcodeList;

    {
        try{
            //server url
            server=new Server();
            socket= IO.socket(server.getAddress());


        }
        catch(URISyntaxException e){}
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pi_barcode_scanning);
        BarcodeList=new BarcodeTempStorage();
       imageView=(ImageView) findViewById(R.id.camera_preview);
        //textView=(TextView)findViewById(R.id.barcode_rlt) ;
        socket.connect();
        mAuth= FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String id = mUser.getUid();
        socket.emit("Getaddres",id);
        doneBtn=(Button)findViewById(R.id.done_Btn);
        resultView=(EditText) findViewById(R.id.resultView);

        socket.emit("AndroidReqStart","Android says start camera");
        socket.on("SendingAdd", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {

                runOnUiThread(new Runnable() {
                    public void run() {
                        JSONArray dataReceive =(JSONArray) args[0];
                        try{
                            String item = dataReceive.get(0).toString();
                            url=item;
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

        new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                while (!Thread.interrupted())
                    try
                    {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable()
                        {

                            @Override
                            public void run()
                            {
                                loadImageFromURL(url);
                                scan();

                            }
                        });
                    }
                    catch (InterruptedException e)
                    {
                        // ooops
                    }
            }
        }).start();


        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String result=BarcodeList.resultString();
                resultView.setText(result);

            }
        });



    }


    private void scan(){

    }

    private void loadImageFromURL(String url)
    {

        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                imageView.setImageBitmap(bitmap);
                Drawable image = imageView.getDrawable();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] imageInByte = stream.toByteArray();
                ByteArrayInputStream bis = new ByteArrayInputStream(imageInByte);
                Bitmap bitmap2= BitmapFactory.decodeStream(bis);
                BarcodeDetector detector =
                        new BarcodeDetector.Builder(getApplicationContext()).build();
                if(!detector.isOperational()){
                    Toast.makeText(getApplicationContext(),  "Could not set up the detector!"
                            , Toast.LENGTH_SHORT).show();
                    return;
                }

                Frame frame = new Frame.Builder().setBitmap(bitmap2).build();
                barcodes = detector.detect(frame);

                if (barcodes.size()>0) {

                        Barcode thisCode = barcodes.valueAt(0);
                        //textView.setText(thisCode.rawValue);
                        socket.emit("BarcodeDetected", thisCode.rawValue);
                        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_ALARM, 200);
                        toneGen1.startTone(ToneGenerator.TONE_DTMF_8,100);
                        String bar=thisCode.rawValue;
                        BarcodeList.AddBarcode(bar);
                }
                else {
                    //textView.setText("no barcode detected");
                }


            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {}

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {}
        };


        Picasso.with(this).load(url).memoryPolicy(MemoryPolicy.NO_CACHE).into(target);
    }
    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), ListMenuActivity.class);
        startActivity(intent);
        socket.emit("AndroidReqStop","Android says stop camera");
        socket.close();
        this.finish();
    }

}
