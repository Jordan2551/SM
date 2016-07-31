package com.example.shiftmate.Database.Tables.Currencies;

import com.example.shiftmate.MainActivity;
import com.example.shiftmate.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jorda_000 on 7/30/2016.
 */
public class Currencies {

    public static ArrayList<Currency> currencyList = new ArrayList<Currency>();//Contains all currency data from currenciesJSON in strings.xml

    public Currencies(){

        //Get the JSON formatted string containing all of the currency types
        String strJson = MainActivity.getContext().getResources().getString(R.string.currenciesJSON);

        String[] currencyArray = new String[0];

        try {

            //Get the root JSON object
            JSONObject rootObject = new JSONObject(strJson);
            //Get the currencies JSON array from the root object
            JSONArray jArray = rootObject.getJSONArray("currencies");

            //Loop through the entire currencies array and store the data in the currency list
            for (int i = 0; i < jArray.length() ; i++) {

                JSONObject subObj = jArray.getJSONObject(i);

                Currency currency = new Currency();

                currency.Id = subObj.getInt("Id");
                currency.currencyName = subObj.getString("currencyName");
                currency.currencySymbol = subObj.getString("currencySymbol");

                currencyList.add(currency);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
