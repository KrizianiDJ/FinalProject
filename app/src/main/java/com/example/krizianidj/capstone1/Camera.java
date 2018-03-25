package com.example.krizianidj.capstone1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;

import java.net.URISyntaxException;

public class Camera extends AppCompatActivity {



    private Server server;
    private Socket socket;
    private FirebaseAuth mAuth;
    String url;

    /**************** code for camera fee*****************/
    ImageView imageView;


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
        setContentView(R.layout.activity_camera);
        socket.connect();
        mAuth= FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String id = mUser.getUid();
        socket.emit("Getaddres",id);

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


        // socket.on("appResp",onNewMessage);

        /****** code of camera stream****/
        imageView=(ImageView)findViewById(R.id.image);
        new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                while (!Thread.interrupted())
                    try
                    {
                        Thread.sleep(5);
                        runOnUiThread(new Runnable()
                        {

                            @Override
                            public void run()
                            {
                                loadImageFromURL(url);
                            }
                        });
                    }
                    catch (InterruptedException e)
                    {
                        // ooops
                    }
            }
        }).start();


        //creating click listener for up button
        ImageButton UpButton=(ImageButton) findViewById(R.id.UpBTN);
        UpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socket.emit("GoUp","Android says move camera up");

            }});

        //creating click listener for left button
        ImageButton LeftButton=(ImageButton) findViewById(R.id.LeftBTN);
        LeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socket.emit("GoLeft","Android says move camera left");

            }});

        //creating click listener for right button
        ImageButton RightButton=(ImageButton) findViewById(R.id.RightBTN);
        RightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socket.emit("GoRight","Android says move camera right");

            }});

        //creating click listener for down button
        ImageButton DownButton=(ImageButton) findViewById(R.id.DownBTN);
        DownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socket.emit("GoDown","Android says move camera down");

            }});

        socket.on("limitUpR", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Up limit reached.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        socket.on("limitDownR", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Down limit reached.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        socket.on("limitLeftR", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Left limit reached.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        socket.on("limitRightR", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Right limit reached.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        //socket.on("LimUp",limup());



    }

    private void loadImageFromURL(String url)
    {

        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                imageView.setImageBitmap(bitmap);
                Drawable image = imageView.getDrawable();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {}

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {}
        };

        Picasso.with(this).load(url).memoryPolicy(MemoryPolicy.NO_CACHE).into(target);



        /*Picasso.with(this)
                .load(url)
                .noFade()
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(imageView);*/

    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainMenu.class);
        startActivity(intent);
        socket.emit("AndroidReqStop","Android says stop camera");
        socket.close();
        this.finish();
    }


}
