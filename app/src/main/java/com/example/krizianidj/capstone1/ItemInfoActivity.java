package com.example.krizianidj.capstone1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class ItemInfoActivity extends AppCompatActivity {

    EditText ExpDate_ET;
    TextView Record_TV, Name_TV,Time_TV,Barcode_TV;
    Button SaveBTN;


    private FirebaseAuth mAuth;
    private Server server;
    private Socket socket;
    private ProgressBar progressBar;
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
        setContentView(R.layout.activity_item_info);
        progressBar=(ProgressBar)findViewById(R.id.progressbar) ;
        ExpDate_ET=(EditText) findViewById(R.id.expET);
        Record_TV=(TextView) findViewById(R.id.recordTV);
        Name_TV=(TextView) findViewById(R.id.nameTV);
        Time_TV=(TextView) findViewById(R.id.timeinTV);
        Barcode_TV=(TextView) findViewById(R.id.barcodeTV);
        SaveBTN=(Button) findViewById(R.id.buttonSaveChanges);

        socket.connect();
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        final String id = mUser.getUid();
        socket.emit("GetItemRecord",id);
        socket.on("ItemRecord", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {

                runOnUiThread(new Runnable() {
                    public void run() {
                        JSONArray ItemRecord = (JSONArray ) args[0];
                        try {
                            for (int i=0;i<ItemRecord.length();i++) {
                                JSONObject rec = ItemRecord.getJSONObject(i);
                                String name = rec.getString("item_name");
                                String exp = rec.getString("exp_date");
                                String timein=rec.getString("timestamp_in");
                                String barcode=rec.getString("barcode");
                                final String record = rec.getString("itemrecord_id").toString();

                                if(barcode=="null")
                                {
                                    barcode="No barcode";
                                }

                                if(exp=="null")
                                {
                                    exp="No Exp.date has been added";
                                }

                                else {
                                    exp = exp.substring(0, 10);
                                }

                                Name_TV.setText(name);
                                ExpDate_ET.setHint(exp);
                                Time_TV.setText(timein);
                                Barcode_TV.setText(barcode);
                                Record_TV.setText(record);

                                SaveBTN.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {


                                        progressBar.setVisibility(View.VISIBLE);

                                        String expdata=ExpDate_ET.getText().toString().trim();
                                       if(!expdata.isEmpty()) {

                                           if (expdata.contains("-") || expdata.contains("/") || expdata.contains(" ") || expdata.contains(".")) {
                                               progressBar.setVisibility(View.GONE);
                                               ExpDate_ET.setError("Format Invalid: no special Characters");
                                               ExpDate_ET.requestFocus();


                                               return;
                                           } else {


                                               Log.e("Exp:", expdata);

                                               //valid data formar
                                               String year = expdata.substring(0, 4);
                                               Boolean Y = false, M = false, D = false;
                                               int yearnum = Integer.parseInt(year);
                                               //Log.e("year:", year);
                                               if (yearnum >= 1994 && yearnum <= 2040) {
                                                   Y = true;
                                               }

                                               Log.e("yearTF", Y.toString());
                                               String month = expdata.substring(4, 6);
                                               int monthnum = Integer.parseInt(month);
                                               if (monthnum >= 1 && monthnum <= 12) {
                                                   M = true;
                                               }
                                               // Log.e("Month", month);
                                               // Log.e("monthTF",M.toString());


                                               String day = expdata.substring(6, 8);
                                               int daynum = Integer.parseInt(day);


                                               //Log.e("day:", day);
                                               if ((monthnum == 1 || monthnum == 3 || monthnum == 5 || monthnum == 7 || monthnum == 8 || monthnum == 10 || monthnum == 12) && (daynum >= 1 && daynum <= 31)) {
                                                   D = true;
                                               }

                                               if ((monthnum == 4 || monthnum == 6 || monthnum == 9 || monthnum == 11) && (daynum >= 1 && daynum <= 30)) {
                                                   D = true;
                                               }

                                               if (monthnum == 2 && (daynum >= 1 && daynum <= 28)) {
                                                   D = true;
                                               }
                                               //  Log.e("dayTF",D.toString());
                                               final String dateformat = year + "-" + month + "-" + day;


                                               if (D && M && Y) {
                                                   //Toast.makeText(getApplicationContext(), "Perfect format", Toast.LENGTH_LONG).show();

                                                   socket.emit("UpdateExp", record, dateformat);
                                                   startActivity(new Intent(getApplicationContext(), InventoryActivity.class));
                                                   finish();


                                               } else {
                                                   progressBar.setVisibility(View.GONE);
                                                   ExpDate_ET.setError("Format Invalid");
                                                   ExpDate_ET.requestFocus();


                                                   return;

                                                  // Toast.makeText(getApplicationContext(), "Format Invalid", Toast.LENGTH_LONG).show();
                                               }
                                           }
                                       }

                                       else{
                                           startActivity(new Intent(getApplicationContext(), InventoryActivity.class));
                                           finish();

                                       }



                                    }
                                });






                            }

                        } catch (Exception e) {

                            Log.e("ERROR:",e.getMessage());

                        }
                    }
                });
            }
        });






    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), InventoryActivity.class);
        startActivity(intent);
        this.finish();
    }
}
