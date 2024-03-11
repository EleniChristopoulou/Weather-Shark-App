package com.example.weatherapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity2 extends AppCompatActivity implements SelectListener{

    ArrayList<WeatherModel> weatherModels = new ArrayList<>();

    SearchView searchView;
    String APIkey = "18eef754aa63a31562233b418524d875";
    String url = "http://api.openweathermap.org/data/2.5/weather";
    String filename = "citiesList.txt";
    FileInputStream inputStream = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        try {
            inputStream = openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }
            displayOnWatchList(sb.toString());
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

        RecyclerView recyclerView = findViewById(R.id.weather_view);
        RC_RecyclerViewAdapter adapter = new RC_RecyclerViewAdapter(this, weatherModels, this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchView = findViewById(R.id.search);

        /*  Expanding searchView so data can be normally displayed,
                    (due to a bug i encountered)*/

        searchView.setIconified(false);
        searchView.requestFocus();
        /*in between is how i fixed it*/

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String city) {
                checkCity(city);
                searchView.setQuery("", true);
                return true;
            }

            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    public void displayOnWatchList(String urls) {
        String[] url = urls.split("\n");
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        for (int i=0; i<url.length; i++) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url[i], new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                        int temp = jsonObjectMain.getInt("temp") - 273;        //from Kelvin to Celcius
                        Date date = new Date(jsonResponse.getLong("dt") * 1000L);
                        String date_info = DateFormat.getDateInstance(DateFormat.FULL).format(date);

                        Log.d("response", response);

                        String city = jsonResponse.getString("name");
                        String country = jsonResponse.getJSONObject("sys").getString("country");
                        int icon = getImg(jsonResponse.getJSONArray("weather").getJSONObject(0).getString("icon"));

                        weatherModels.add(new WeatherModel(city, country, date_info, temp,icon));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            requestQueue.add(stringRequest);
        }

    }


    void checkCity(String city) {
        city = city.toLowerCase().trim();
        String tempUrl = url + "?q=" + city + "&appid=" + APIkey;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, tempUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Intent intent = new Intent(MainActivity2.this, MainActivity3.class);
                intent.putExtra("tempUrl", tempUrl);
                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (isNetworkAvailable()) { // Check if device is connected to Internet
                    Toast.makeText(getApplicationContext(), "City's name is incorrect.", Toast.LENGTH_SHORT).show();
                } else {
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

    private int getImg(String icon) {

        switch (icon) {
            case "01d":     /*clear sky*/
            case "01n":
                return R.drawable.sunnyday;
            case "02d":     /*clouds*/
            case "03d":
            case "04d":
            case "50d":
            case "02n":
            case "03n":
            case "04n":
            case "50n":
                return R.drawable.cloudyday;
            case "09d":     /*rain*/
            case "10d":
            case "11d":
            case "09n":
            case "10n":
            case "11n":
                return R.drawable.rainyday;
            case "13d":     /*snow*/
            case "13n":
                return R.drawable.snowyday;
            default:
                return R.drawable.sunnyday;
        }
    }

    @Override
    public void onItemClicked(WeatherModel weatherModel) {
        String tempUrl = url + "?q=" + weatherModel.getCity().toLowerCase(Locale.ROOT).trim()+ "&appid=" + APIkey;
        Intent intent = new Intent(MainActivity2.this, MainActivity3.class);
        intent.putExtra("tempUrl", tempUrl);
        startActivity(intent);
    }
}