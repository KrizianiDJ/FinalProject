package com.example.krizianidj.capstone1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.w3c.dom.Text;

import java.net.URISyntaxException;

public class TemperatureActivity extends AppCompatActivity {
    private Server server;
    private Socket socket;
    private FirebaseAuth mAuth;


    {
        try{
            //server url
            server=new Server();
            socket= IO.socket(server.getAddress());


        }
        catch(URISyntaxException e){}
    }


    private TextView Temp, Humidity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);
        Temp=(TextView) findViewById(R.id.Temp_TV);
        Humidity=(TextView) findViewById(R.id.Humidity_TV);
        socket.connect();
        mAuth= FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String id = mUser.getUid();

        socket.emit("GetSensorData",id);
        socket.on("GiveSensorData", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {

                runOnUiThread(new Runnable() {
                    public void run() {
                        JSONArray tempReceive =(JSONArray) args[0];
                        JSONArray humReceive =(JSONArray) args[1];

                        try{
                            Log.e("tempREeive:", tempReceive.toString());
                            Log.e("humREeive:", humReceive.toString());
                            String temp = tempReceive.get(0).toString();
                            String hum = humReceive.get(0).toString();


                            Temp.setText(temp+" °F");
                            Humidity.setText(hum+" %");

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
    }
}
