package com.example.krizianidj.capstone1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.net.URISyntaxException;

public class Camera extends AppCompatActivity {


    private Socket socket;

    /**************** code for camera fee*****************/
    ImageView imageView;
    String url="http://192.168.0.19:3000/";
    //uni
    //String url="http://10.76.1.43:3000/";
     /*end code for camera fee*/

    private Emitter.Listener onNewMessage=new Emitter.Listener(){
        @Override
        public void call (final Object... args)
        {
            runOnUiThread(new Runnable(){
                @Override
                public void run()
                {
                    String data= (String) args[0];
                    //TextView txt=(TextView) findViewById(R.id.textView);
                    //txt.setText(data);


                }
            });
        }
    };
    {
        try{
            //server url
            socket= IO.socket("http://192.168.0.13:4000");

            //uni
            //socket= IO.socket("http://10.3.72.128:4000");


        }
        catch(URISyntaxException e){}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        socket.connect();
        socket.emit("AndroidReqStart","Android says start camera");


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
                        Thread.sleep(50);
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
                socket.emit("GoUp","Android says move camera left");

            }});

        //creating click listener for right button
        ImageButton RightButton=(ImageButton) findViewById(R.id.RightBTN);
        RightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socket.emit("GoUp","Android says move camera right");

            }});

        //creating click listener for down button
        ImageButton DownButton=(ImageButton) findViewById(R.id.DownBTN);
        DownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socket.emit("GoUp","Android says move camera down");

            }});



    }

    private void loadImageFromURL(String url)
    {
        /*Picasso.with(this).load(url).fit().centerCrop()
                .placeholder(R.mipmap.placeholder)
                .error(R.mipmap.ic_launcher)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(imageView,new Callback(){

                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {

                            }
                        }


                );*/

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
