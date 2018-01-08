package com.example.krizianidj.capstone1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class Camera extends AppCompatActivity {

    private Socket socket;
    private Emitter.Listener onNewMessage=new Emitter
    {
        try{
            socket= IO.socket("http://192.168.0.15:4000");
        }
        catch(URISyntaxException e){}
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        socket.connect();
        socket.emit("appReq","Hello is the android app");

       // socket.on('appReq',response(data));
    }


}
