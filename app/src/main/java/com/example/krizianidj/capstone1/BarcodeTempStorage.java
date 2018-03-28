package com.example.krizianidj.capstone1;

import java.util.ArrayList;

/**
 * Created by KrizianiDJ on 3/27/2018.
 */

public class BarcodeTempStorage {
    private ArrayList<String> BarcodeList= new ArrayList<String>();

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


}
