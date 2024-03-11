package com.example.weatherapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RC_RecyclerViewAdapter extends RecyclerView.Adapter<RC_RecyclerViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<WeatherModel> weatherModels;
    SelectListener linstener;
    public RC_RecyclerViewAdapter(Context context, ArrayList<WeatherModel> weatherModels, SelectListener linstener){
        this.context = context;
        this.weatherModels = weatherModels;
        this.linstener = linstener;
    }
    @NonNull
    @Override
    public RC_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.cities_weather_row,parent, false);
        return new RC_RecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RC_RecyclerViewAdapter.MyViewHolder holder, int position) {

        holder.img.setImageResource(weatherModels.get(position).getImg());
        holder.country_city.setText(weatherModels.get(position).getCity()+", "+weatherModels.get(position).getCountry());
        holder.temp.setText(String.valueOf(weatherModels.get(position).getTemperature())+"°C");
        holder.date_info.setText(weatherModels.get(position).getDate_info());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linstener.onItemClicked(weatherModels.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return weatherModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView img ;
        TextView country_city, date_info, temp;

        CardView cardView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.weatherimg);
            country_city = itemView.findViewById(R.id.city_country);
            date_info= itemView.findViewById(R.id.date);
            temp = itemView.findViewById(R.id.temperature);

            cardView = itemView.findViewById(R.id.card_view);
        }
    }
}
