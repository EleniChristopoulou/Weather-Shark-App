package com.example.weatherapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity3 extends AppCompatActivity {
    String tempUrl;
    String APIkey = "18eef754aa63a31562233b418524d875";
    String url = "http://api.openweathermap.org/data/2.5/weather";

    ArrayList<String> savedUrls = new ArrayList<>();

    int[] rIds = {
            R.id.temperature,
            R.id.feelsLike,
            R.id.pressure,
            R.id.humidity,
            R.id.clouds,
            R.id.deg,
            R.id.windspeed,
            R.id.description,
            R.id.date
    };

    String unitsOfMeasurement[] ={
            "°C",
            "°C",
            " hPa",
            "%",
            "%",
            "°",
            " m/s",
            "",
            ""
    };

    boolean isOn=false;
    DecimalFormat df = new DecimalFormat("#.#");
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        String url = getIntent().getStringExtra("tempUrl");
        tempUrl = url;

        getDetails(url);
        readSavedUrls();

        if(savedUrls.contains(tempUrl)){        //already saved
            changeColorOfStar(R.id.star,isOn);
            isOn = !isOn;
        }

        searchView = findViewById(R.id.searchact3);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String city) {
                checkCity(city);
                return true;
            }
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    private void readSavedUrls() {
        String filename = "citiesList.txt";
        FileInputStream inputStream = null;
        StringBuilder sb = new StringBuilder();

        try {
            inputStream = openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);
            String text;

            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }

        String[] url = sb.toString().split("\n");

        for(int i=0; i<url.length; i++){
            if(!savedUrls.contains(url[i])) {
                savedUrls.add(url[i]);
            }
        }
    }

    void getDetails(String tempUrl) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, tempUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String[] info = new String[9];

                try {
                    JSONObject jsonResponse = new JSONObject(response);

                    JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                    info[0]= df.format(jsonObjectMain.getDouble("temp") - 273.15);        //from Kelvin to Celcius
                    info[1] = df.format(jsonObjectMain.getDouble("feels_like") - 273.15);
                    info[2] = df.format(jsonObjectMain.getDouble("pressure"));
                    info[3] = df.format(jsonObjectMain.getDouble("humidity"));
                    info[4] = df.format(jsonResponse.getJSONObject("clouds").getInt("all"));
                    info[5] = df.format(jsonResponse.getJSONObject("wind").getInt("deg"));

                    info[6] = df.format(jsonResponse.getJSONObject("wind").getDouble("speed"));

                    JSONArray weatherArray = jsonResponse.getJSONArray("weather");
                    JSONObject weatherObject = weatherArray.getJSONObject(0);
                    info[7] = capitalizeEachWord(weatherObject.getString("description"));
                    Date date = new Date(jsonResponse.getLong("dt") * 1000L);
                    info[8] = DateFormat.getDateInstance(DateFormat.FULL).format(date);

                    updateUI(info);

                    String location= jsonResponse.getString("name")+","+ jsonResponse.getJSONObject("sys").getString("country");
                    androidx.appcompat.widget.SearchView  s = (androidx.appcompat.widget.SearchView) findViewById(R.id.searchact3);
                    s.setQueryHint(location);

                    String icon = jsonResponse.getJSONArray("weather").getJSONObject(0).getString("icon");
                    displayImg(icon);
                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Intent intent = new Intent(MainActivity3.this, MainActivity2.class);
                startActivity(intent);
                finish();
                Toast.makeText(getApplicationContext(), "City's name is incorrect.", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void displayImg(String icon) {
        ImageView img = (ImageView) findViewById(R.id.img);

        switch (icon){
            case "01d":     /*clear sky*/
            case "01n":
                img.setBackgroundResource(R.drawable.sunnyday);
                break;
            case "02d":     /*clouds*/
            case "03d":
            case "04d":
            case "50d":
            case "02n":
            case "03n":
            case "04n":
            case "50n":
                img.setBackgroundResource(R.drawable.cloudyday);
                break;
            case "09d":     /*rain*/
            case "10d":
            case "11d":
            case "09n":
            case "10n":
            case "11n":
                img.setBackgroundResource(R.drawable.rainyday);
                break;
            case "13d":     /*snow*/
            case "13n":
                img.setBackgroundResource(R.drawable.snowyday);
                break;
            default:
                img.setBackgroundResource(R.drawable.sunnyday);
                break;
        }
    }

    private void updateUI(String[] info){
        TextView textInfo;

        for(int i=0; i<info.length; i++){
            textInfo = (TextView) findViewById(rIds[i]);
            textInfo.setText(info[i]+unitsOfMeasurement[i]);
        }
    }

    public void addRemove(View view) {

        String filename = "citiesList.txt";
        String fileContents = new String();
        FileOutputStream outputStream = null;

        changeColorOfStar(R.id.star,isOn);
        isOn = !isOn;

        if(isOn){  /*saving new url*/
            fileContents = tempUrl + "\n";

            try {
                outputStream = openFileOutput(filename, Context.MODE_APPEND);
                outputStream.write(fileContents.getBytes());
                Toast.makeText(getApplicationContext(), "City Added onWatch.", Toast.LENGTH_SHORT).show();
                savedUrls.add(tempUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            savedUrls.remove(tempUrl);
            Toast.makeText(getApplicationContext(), "City Removed from onWatch.", Toast.LENGTH_SHORT).show();

            /* remove from text*/
            fileContents = null;

            for(int i=0; i<savedUrls.size(); i++){
                fileContents = savedUrls.get(i) + "\n" + fileContents;
            }

            try {
                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);  /*Overwrite*/
                outputStream.write(fileContents.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }

        Intent intent = new Intent(MainActivity3.this, MainActivity2.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void changeColorOfStar(int id, boolean isOn){
        ImageButton star = (ImageButton) findViewById(id);

        if(isOn){
            star.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFEB3B")));
        }else {
            star.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FA7740")));
        }
    }

    public static String capitalizeEachWord(String str) {
        StringBuilder result = new StringBuilder();

        String[] words = str.split("\\s");
        for (String word : words) {
            String capitalizedWord = word.substring(0, 1).toUpperCase() + word.substring(1);
            result.append(capitalizedWord).append(" ");
        }
        return result.toString().trim();
    }

    void checkCity(String city) {
        city = city.toLowerCase().trim();
        String tempUrl = url + "?q=" + city + "&appid=" + APIkey;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, tempUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Intent intent = new Intent(MainActivity3.this, MainActivity3.class);
                intent.putExtra("tempUrl", tempUrl);
                startActivity(intent);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (isNetworkAvailable()) {
                    Toast.makeText(getApplicationContext(), "City's name is incorrect.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Device is not connected to Internet.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }
}