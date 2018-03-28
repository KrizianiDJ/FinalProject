package com.example.krizianidj.capstone1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.net.URISyntaxException;

public class MainMenu extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private  Button camButton;
    private Button tempButton;
    private Button listButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        camButton=(Button) findViewById(R.id.camButton);
        tempButton=(Button) findViewById(R.id.tempButton);
        listButton=(Button) findViewById(R.id.listButton);

        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();


        if (mUser==null)
        {
            startActivity(new Intent(this, SignIn.class));
            finish();
            return;
        }

        camButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent i=new Intent(getApplicationContext(),Camera.class);
                startActivity(i);

            }
        });


        tempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),  mUser.getUid()
                        , Toast.LENGTH_SHORT).show();

            }
        });

        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),ListMenuActivity.class);
                startActivity(i);

            }
        });



    }



    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.btnsignOut)
        {
            mAuth.signOut();
            startActivity(new Intent(this, SignIn.class));
            finish();
        }
        if(item.getItemId()==R.id.btnAddProduct)
        {
            startActivity(new Intent(this, AddingBarcodes.class));
            finish();
        }
        return super.onOptionsItemSelected(item);



    }


}
