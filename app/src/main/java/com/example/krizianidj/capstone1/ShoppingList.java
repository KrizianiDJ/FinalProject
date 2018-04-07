package com.example.krizianidj.capstone1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class ShoppingList extends AppCompatActivity {
    private FirebaseAuth mAuth;
    Button Add,AddS;
    ListView ShopList;
    private Server server;
    private Socket socket;
    private ArrayList<String> Items=new ArrayList<>();
    private ArrayList<String> Records=new ArrayList<>();
    private ArrayList<String> Matches=new ArrayList<>();
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
        setContentView(R.layout.activity_shopping_list);
        socket.connect();
        ShopList = (ListView) findViewById(R.id.ShopListView);
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        final String id = mUser.getUid();
        Add=(Button) findViewById(R.id.addMBtn);
        AddS=(Button) findViewById(R.id.addScanBtn);

        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),AddItemShopping.class);
                startActivity(i);
                finish();

            }
        });

        AddS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),ShoppingListScan.class);
                startActivity(i);
                finish();

            }
        });

        socket.emit("GetShopList",id);
        socket.on("ShopList", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {

                runOnUiThread(new Runnable() {
                    public void run() {
                        JSONArray InventoryList = (JSONArray ) args[0];
                        try {
                            for (int i=0;i<InventoryList.length();i++)
                            {
                                JSONObject rec=InventoryList.getJSONObject(i);
                                String name = rec.getString("item_name");
                                String record=rec.getString("itemrecord_id").toString();
                                String barcode=rec.getString("barcode").toString();
                                String matched=rec.getString("match_tosp").toString();
                                Records.add(record);
                                Items.add(name);

                                if(matched=="null")
                                {
                                    matched="  ";
                                }
                                else{
                                    matched=" Matched via scan";
                                }

                                Matches.add(matched);





                                Log.e("trying added list....", name);
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                                    android.R.layout.simple_list_item_2, android.R.id.text1, Items) {
                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    View view = super.getView(position, convertView, parent);
                                    TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                                    TextView text2 = (TextView) view.findViewById(android.R.id.text2);


                                    text2.setText(Matches.get(position));
                                    text1.setText("Rec.# " + Records.get(position) + " " + Items.get(position));


                                    Log.e("..",Items.get(position) +"  "+Matches.get(position));

                                    return view;
                                }
                            };

                            ArrayAdapter<String> arrayAdapter1=new ArrayAdapter<String>(
                                    getApplicationContext(), android.R.layout.simple_list_item_1,
                                    Items);
                            ShopList.setAdapter(adapter);

                            ShopList.setOnItemClickListener(

                                    new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, final View view, int i, long l) {
                                            final int itemnum=i;

                                            String item=(String) adapterView.getItemAtPosition(i);
                                          /*Toast.makeText(getApplicationContext(),"Clicked item "+item+" "+
                                                          Records.get(i),
                                                  Toast.LENGTH_LONG).show();*/
                                            PopupMenu popupMenu=new PopupMenu(getApplicationContext(),view);
                                            popupMenu.getMenuInflater().inflate(R.menu.del_shopping_list_item,popupMenu.getMenu());

                                            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                                @Override
                                                public boolean onMenuItemClick(MenuItem menuItem) {
                                                    if(menuItem.getItemId()==R.id.yes)
                                                    {
                                                        socket.emit("DelItemShopping", Records.get(itemnum));
                                                        startActivity(new Intent(getApplicationContext(), ShoppingList.class));
                                                        finish();
                                                    }
                                                    if(menuItem.getItemId()==R.id.no)
                                                    {

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
