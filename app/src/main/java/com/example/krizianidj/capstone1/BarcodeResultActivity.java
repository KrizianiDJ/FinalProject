package com.example.krizianidj.capstone1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class BarcodeResultActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    Intent myIntent = getIntent(); // gets the previously created intent
    ListView AList;
    ListView EList;
    private Server server;
    private Socket socket;
    private ArrayList<String> AddedArrayList= new ArrayList<String>();
    private ArrayList<String> ErrorArrayList= new ArrayList<String>();
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
        setContentView(R.layout.activity_barcode_result);
        socket.connect();
        AList = (ListView) findViewById(R.id.AddedListView);
        EList = (ListView) findViewById(R.id.ErrorListView);mAuth= FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String id = mUser.getUid();






        //BarcodeList=myIntent.getStringArrayListExtra("BarcodeList");
        socket.emit("BarcodeDetected",id);
        socket.on("BarcodeLists", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {

                runOnUiThread(new Runnable() {
                    public void run() {
                        JSONArray AddedList = (JSONArray ) args[0];
                        JSONArray ErrorList =(JSONArray) args[1];
                        try {
                            for (int i=0;i<AddedList.length();i++)
                            {
                                JSONObject rec=AddedList.getJSONObject(i);
                                String addeditem = rec.getString("item_name");
                                AddedArrayList.add(addeditem);


                                Log.e("trying added list....", addeditem);

                            }
                            ArrayAdapter<String> arrayAdapter1=new ArrayAdapter<String>(
                                    getApplicationContext(), android.R.layout.simple_list_item_1,
                                    AddedArrayList);
                            AList.setAdapter(arrayAdapter1);




                            for (int i=0;i<ErrorList.length();i++)
                            {
                                String erroritem = ErrorList.get(i).toString();
                                ErrorArrayList.add(erroritem);


                                //Log.e("trying added list....", addeditem);

                            }
                            ArrayAdapter<String> arrayAdapter2=new ArrayAdapter<String>(
                                    getApplicationContext(), android.R.layout.simple_list_item_1,
                                    ErrorArrayList);
                            EList.setAdapter(arrayAdapter2);



                        } catch (Exception e) {

                            Log.e("ERROR:",e.getMessage());

                        }
                    }
                });
            }
        });




       /* AddedList=(ArrayList<String>)getIntent().getSerializableExtra("AddedList");
        ErrorList=(ArrayList<String>)getIntent().getSerializableExtra("ErrorList");



        ArrayAdapter<String> arrayAdapter1=new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                AddedList);

        AList.setAdapter(arrayAdapter1);
        ArrayAdapter<String> arrayAdapter2=new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                ErrorList);

        EList.setAdapter(arrayAdapter1);*/
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), ListMenuActivity.class);
        startActivity(intent);
        this.finish();
    }
}
