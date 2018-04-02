package com.example.krizianidj.capstone1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class AddItemInventory extends AppCompatActivity {
    private FirebaseAuth mAuth;
    Button Save;
    EditText ItemName1,ItemName2,ItemName3,ItemName4;
    private Server server;
    private Socket socket;

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
        setContentView(R.layout.activity_add_item_inventory);

        Save=(Button) findViewById(R.id.buttonSaveChanges);
        ItemName1=(EditText) findViewById(R.id.itemname1);
        ItemName2=(EditText) findViewById(R.id.itemname2);
        ItemName3=(EditText) findViewById(R.id.itemname3);
        ItemName4=(EditText) findViewById(R.id.itemname4);



        socket.connect();
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        final String id = mUser.getUid();

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> toAdd=new ArrayList<>();


                if(!ItemName1.getText().toString().trim().isEmpty())
                {
                    toAdd.add(ItemName1.getText().toString().trim());
                }
                if(!ItemName2.getText().toString().trim().isEmpty())
                {
                    toAdd.add(ItemName2.getText().toString().trim());
                }
                if(!ItemName3.getText().toString().trim().isEmpty())
                {
                    toAdd.add(ItemName3.getText().toString().trim());
                }
                if(!ItemName4.getText().toString().trim().isEmpty())
                {
                    toAdd.add(ItemName4.getText().toString().trim());
                }
                if(!toAdd.isEmpty())
                {for(int i=0; i< toAdd.size(); i++) {
                    socket.emit("AddtoInventory", id, toAdd.get(i));
                }
                }


                Intent i=new Intent(getApplicationContext(),InventoryActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}
