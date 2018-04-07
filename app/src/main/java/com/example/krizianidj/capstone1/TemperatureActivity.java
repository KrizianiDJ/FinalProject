package com.example.krizianidj.capstone1;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.vision.text.Line;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TemperatureActivity extends AppCompatActivity {
    private Server server;
    private Socket socket;
    private FirebaseAuth mAuth;
    private ArrayList<Entry> TemperatureValuesList=new ArrayList<>();
    private ArrayList<Entry>HumidityValuesList=new ArrayList<>();
   LineGraphSeries<DataPoint> TemperatureList;
    LineGraphSeries<DataPoint> HumidityList;


    private ArrayList<String>Hum_times=new ArrayList<>();
    private ArrayList<String>Temp_times=new ArrayList<>();
    GraphView graph;
    GraphView graph1;
    LineChart lineChart;

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
        //ineChart=(LineChart) findViewById(R.id.lineChart);
        //lineChart.setNoDataText("Loading Data...");
        graph =(GraphView) findViewById(R.id.graph);
        graph1 =(GraphView) findViewById(R.id.graph1);

        //lineChart=(LineChart) findViewById(R.id.lineChart);

        socket.emit("GetSensorData",id);
        socket.on("GiveSensorData", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {

                runOnUiThread(new Runnable() {
                    public void run() {
                        JSONArray tempReceive =(JSONArray) args[0];
                        JSONArray humReceive =(JSONArray) args[1];

                        try{
                            //Log.e("tempREeive:", tempReceive.toString());
                            //Log.e("humREeive:", humReceive.toString());
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


       socket.emit("GetHistoricData",id);
        socket.on("GiveHistoricData", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {

                runOnUiThread(new Runnable() {
                    public void run() {
                        JSONArray TempList= (JSONArray ) args[0];
                        JSONArray HumList = (JSONArray ) args[1];

                        try{
                                TemperatureList= new LineGraphSeries<DataPoint>();
                                HumidityList= new LineGraphSeries<DataPoint>();

                            for (int i=0;i<TempList.length();i++) {
                                JSONObject rec = TempList.getJSONObject(i);
                                Double value=Double.parseDouble(rec.getString("data_value"));
                                String time=rec.getString("timestamp");
                                time=time.substring(0,10);
                                Date date=new SimpleDateFormat("yyyy-mm-dd").parse(time);
                                //TemperatureValuesList.add(new Entry(value,i));
                                Temp_times.add(time);
                                Log.e("checking temperature:","temp: "+value+" time:"+time);

                                TemperatureList.appendData(new DataPoint(i,value),true,500);

                            }
                            for (int i=0;i<HumList.length();i++) {
                                JSONObject rec = HumList.getJSONObject(i);
                                Float value=Float.parseFloat(rec.getString("data_value"));
                                String time=rec.getString("timestamp");
                                time=time.substring(0,10);
                                //HumidityValuesList.add(new Entry(value,i));
                                HumidityList.appendData(new DataPoint(i,value),true,500);
                                Hum_times.add(time);
                                Log.e("checking humidity:","hum: "+value+" time:"+time);
                            }



                            graph.addSeries(TemperatureList);
                            graph1.addSeries(HumidityList);





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
