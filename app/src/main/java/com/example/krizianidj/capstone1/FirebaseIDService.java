package com.example.krizianidj.capstone1;

import android.util.Log;
import android.widget.ImageView;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.net.URISyntaxException;

public class FirebaseIDService extends FirebaseInstanceIdService{

    private static final String TAG="FirebaseIDService";
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

    @Override
    public void onTokenRefresh()
    {
        String token= FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "  "+token);
        sendRegistrationToServer(token);
    }
    private void sendRegistrationToServer(String token)
    {
        socket.connect();
        mAuth= FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String id = mUser.getUid();
        socket.emit("AddTokenToDB",id,token);

    }
}
