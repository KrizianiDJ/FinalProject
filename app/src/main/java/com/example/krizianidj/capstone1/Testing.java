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


public class Testing extends AppCompatActivity {

    private Server server;
    private Socket socket;
    private FirebaseAuth mAuth;
    private TextView result;
    String url;

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
        setContentView(R.layout.activity_testing);

        result=(TextView) findViewById(R.id.result);

        socket.connect();
        mAuth= FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String id = mUser.getUid();
        socket.emit("Getaddres",id);
        socket.on("SendingAdd", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {

                runOnUiThread(new Runnable() {
                    public void run() {
                        JSONArray dataReceive =(JSONArray) args[0];
                        try{
                            url = dataReceive.get(0).toString();


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

        result.setText("euuut "+url);


    }


}
