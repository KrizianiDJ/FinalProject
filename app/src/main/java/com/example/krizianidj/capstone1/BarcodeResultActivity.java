package com.example.krizianidj.capstone1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class BarcodeResultActivity extends AppCompatActivity {

    Intent myIntent = getIntent(); // gets the previously created intent
    private ArrayList<String> BarcodeList= new ArrayList<String>();
    private ArrayList<String> AddedList= new ArrayList<String>();
    private ArrayList<String> ErrorList= new ArrayList<String>();
    ListView AList;
    ListView EList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_result);

       AList=(ListView)findViewById(R.id.AddedListView) ;
        EList=(ListView)findViewById(R.id.ErrorListView) ;
        //BarcodeList=myIntent.getStringArrayListExtra("BarcodeList");
        AddedList=(ArrayList<String>)getIntent().getSerializableExtra("AddedList");
        ErrorList=(ArrayList<String>)getIntent().getSerializableExtra("ErrorList");

        for(int i=0;i<AddedList.size();i++) {
            Log.i("AddedList:",""+AddedList.get(i));
        }
        for(int i=0;i<ErrorList.size();i++) {
            Log.i("Error:",""+ErrorList.get(i));
        }

       /* ArrayAdapter<String> arrayAdapter1=new ArrayAdapter<String>(
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
