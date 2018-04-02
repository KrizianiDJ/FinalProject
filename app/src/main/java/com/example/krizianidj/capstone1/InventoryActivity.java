package com.example.krizianidj.capstone1;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.sql.Array;
import java.util.ArrayList;

public class InventoryActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    ListView InvList;
    private Server server;
    private Socket socket;
    private ArrayList<String> Inv= new ArrayList<String>();
    private ArrayList<String> Exp=new ArrayList<String>();
    private ArrayList<String> Records=new ArrayList<String>();
    Button Add;
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
        setContentView(R.layout.activity_inventory);
        socket.connect();
        InvList = (ListView) findViewById(R.id.InvListView);
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        final String id = mUser.getUid();
        Add=(Button) findViewById(R.id.addBtn);

        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),AddItemInventory.class);
                startActivity(i);
                finish();

            }
        });

        socket.emit("GiveInvList",id);
        socket.on("InvList", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {

                runOnUiThread(new Runnable() {
                    public void run() {
                        JSONArray InventoryList = (JSONArray ) args[0];
                        try {
                            for (int i=0;i<InventoryList.length();i++)
                            {
                                JSONObject rec=InventoryList.getJSONObject(i);
                                String addeditem = rec.getString("item_name");
                                String exp=rec.getString("exp_date");
                                String record=rec.getString("itemrecord_id").toString();
                                Inv.add(addeditem);
                                Records.add(record);




                                if(exp=="null")
                                {
                                    exp="No Exp.date has been added";
                                }

                                else {
                                    exp = exp.substring(0, 10);
                                }
                                Exp.add(exp);





                                //Log.e("trying added list....", addeditem);
                            }

                            ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),
                                    android.R.layout.simple_list_item_2, android.R.id.text1, Inv) {
                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    View view = super.getView(position, convertView, parent);
                                    TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                                    TextView text2 = (TextView) view.findViewById(android.R.id.text2);


                                    text1.setText("Rec.# "+Records.get(position)+" "+Inv.get(position));
                                    text2.setText(Exp.get(position));
                                    return view;
                                }
                            };

                            /*ArrayAdapter<String> arrayAdapter1=new ArrayAdapter<String>(
                                    getApplicationContext(), android.R.layout.simple_list_item_2,
                                    Inv);*/
                          InvList.setAdapter(adapter);


                          InvList.setOnItemClickListener(

                                  new AdapterView.OnItemClickListener() {
                                      @Override
                                      public void onItemClick(AdapterView<?> adapterView, final View view, int i, long l) {
                                          final int itemnum=i;

                                          String item=(String) adapterView.getItemAtPosition(i);
                                          /*Toast.makeText(getApplicationContext(),"Clicked item "+item+" "+
                                                          Records.get(i),
                                                  Toast.LENGTH_LONG).show();*/
                                          PopupMenu popupMenu=new PopupMenu(getApplicationContext(),view);
                                          popupMenu.getMenuInflater().inflate(R.menu.popup_menu,popupMenu.getMenu());

                                          popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                              @Override
                                              public boolean onMenuItemClick(MenuItem menuItem) {
                                                  if(menuItem.getItemId()==R.id.EditInfo)
                                                  {
                                                      socket.emit("ItemInfo",id, Records.get(itemnum));
                                                      startActivity(new Intent(getApplicationContext(), ItemInfoActivity.class));
                                                      finish();
                                                  }
                                                  if(menuItem.getItemId()==R.id.DelItem)
                                                  {
                                                      final PopupMenu popupMenu2=new PopupMenu(getApplicationContext(),view);
                                                      popupMenu2.getMenuInflater().inflate(R.menu.del_popup,popupMenu2.getMenu());

                                                      popupMenu2.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                                          @Override
                                                          public boolean onMenuItemClick(MenuItem menuItem) {
                                                              if(menuItem.getItemId()==R.id.yesA)
                                                              {
                                                                  socket.emit("DelItemA",Records.get(itemnum));
                                                                  startActivity(new Intent(getApplicationContext(), InventoryActivity.class));
                                                                  finish();
                                                              }
                                                              if(menuItem.getItemId()==R.id.yesNoadd)
                                                              {
                                                                  socket.emit("DelItemN",Records.get(itemnum));
                                                                  startActivity(new Intent(getApplicationContext(), InventoryActivity.class));
                                                                  finish();
                                                              }
                                                              if(menuItem.getItemId()==R.id.no)
                                                              {

                                                              }
                                                              return true;

                                                          }

                                                      });
                                                      popupMenu2.show();
                                                  }
                                                  return true;
                                              }
                                          });

                                          popupMenu.show();





                                      }
                                  }
                          );



                        } catch (Exception e) {

                            Log.e("ERROR:",e.getMessage());

                        }
                    }
                });
            }
        });
    }
}
