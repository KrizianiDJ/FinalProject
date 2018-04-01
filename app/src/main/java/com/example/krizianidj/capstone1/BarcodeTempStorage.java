package com.example.krizianidj.capstone1;


import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by KrizianiDJ on 3/27/2018.
 */

public class BarcodeTempStorage extends Activity{
    private ArrayList<String> BarcodeList= new ArrayList<String>();
    private static ArrayList<String> AddedList= new ArrayList<String>();
    private static ArrayList<String> ErrorList= new ArrayList<String>();


    String barcodedata;

    public BarcodeTempStorage() {

    }

    public ArrayList<String> getBarcodeList() {
        return BarcodeList;
    }

    public void AddBarcode(String barcode)
    {
        BarcodeList.add(barcode);
    }

    public String resultString(){
        String result;

        if(BarcodeList.isEmpty())
        {
            result="No barcode detected";
        }

        else
        {
            result="Barcodes detected: ";
            for(int i=0; i<BarcodeList.size(); i++)
            {
                result+=BarcodeList.get(i);
                result+="  ";
            }
        }

        return result;
    }

    public void sendToDB(Socket socket,String id){

        for(int i=0; i<BarcodeList.size(); i++)
        {
            final String barcode=BarcodeList.get(i);
            final int j =i;
            socket.emit("GetProductData",barcode,id);

        }

    }


}
