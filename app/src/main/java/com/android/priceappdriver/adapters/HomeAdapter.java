package com.android.priceappdriver.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.android.priceappdriver.OrderCompletedAct;
import com.android.priceappdriver.R;
import com.android.priceappdriver.TripDetail;
import com.android.priceappdriver.TripPendingAct;
import com.android.priceappdriver.model.SuccessResGetRequests;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Ravindra Birla on 05,August,2021
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.SelectTimeViewHolder> {

    private Context context;
    private ArrayList<SuccessResGetRequests.Result> requestList;
    public HomeAdapter(Context context,ArrayList<SuccessResGetRequests.Result> requestList)
    {
        this.context = context;
        this.requestList = requestList;
    }
    @NonNull
    @Override
    public SelectTimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.home_item, parent, false);
        SelectTimeViewHolder viewHolder = new SelectTimeViewHolder(listItem);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull SelectTimeViewHolder holder, int position) {
        TextView tvTime,tvfromLocation,tvToLocation,tvPrice,tvCompleted,tvInProgress,tvNew;
        tvTime = holder.itemView.findViewById(R.id.tvTime);
        tvfromLocation = holder.itemView.findViewById(R.id.tvFromLocation);
        tvToLocation = holder.itemView.findViewById(R.id.tvtoLocation);
        tvPrice = holder.itemView.findViewById(R.id.tvPrice);
        tvCompleted = holder.itemView.findViewById(R.id.tvCompleted);
        tvInProgress = holder.itemView.findViewById(R.id.tvInProgress);
        tvNew = holder.itemView.findViewById(R.id.tvNew);
        RelativeLayout rlParent = holder.itemView.findViewById(R.id.rlParent);
        rlParent.setOnClickListener(v ->
                {
                    if(requestList.get(position).getStatus().equalsIgnoreCase("Pending"))
                    {
                        context.startActivity(new Intent(context, TripPendingAct.class).putExtra("data",new Gson().toJson(requestList.get(position))));
                    }else  if(requestList.get(position).getStatus().equalsIgnoreCase("Accepted"))
                    {
                        context.startActivity(new Intent(context, TripDetail.class).putExtra("data",new Gson().toJson(requestList.get(position))));
                    }else  if(requestList.get(position).getStatus().equalsIgnoreCase("Progress"))
                    {
                        context.startActivity(new Intent(context, TripDetail.class).putExtra("data",new Gson().toJson(requestList.get(position))));
                    }else  if(requestList.get(position).getStatus().equalsIgnoreCase(""))
                    {
                        context.startActivity(new Intent(context, TripPendingAct.class).putExtra("data",new Gson().toJson(requestList.get(position))));
                    }else  if(requestList.get(position).getStatus().equalsIgnoreCase("Cancel"))
                    {
                        context.startActivity(new Intent(context, TripPendingAct.class).putExtra("data",new Gson().toJson(requestList.get(position))));
                    }else  if(requestList.get(position).getStatus().equalsIgnoreCase("Completed"))
                    {
                        context.startActivity(new Intent(context, OrderCompletedAct.class).putExtra("data",new Gson().toJson(requestList.get(position))));
                    }
                }
                );
        if (requestList.get(position).getStatus().equalsIgnoreCase("Pending"))
        {
            tvCompleted.setVisibility(View.GONE);
            tvInProgress.setVisibility(View.GONE);
            tvNew.setVisibility(View.VISIBLE);
        }else if (requestList.get(position).getStatus().equalsIgnoreCase("Cancel"))
        {
            tvCompleted.setVisibility(View.GONE);
            tvInProgress.setVisibility(View.GONE);
            tvNew.setVisibility(View.VISIBLE);
        } else if(requestList.get(position).getStatus().equalsIgnoreCase("Accepted"))
        {
            tvCompleted.setVisibility(View.GONE);
            tvInProgress.setVisibility(View.VISIBLE);
            tvNew.setVisibility(View.GONE);
        } else if(requestList.get(position).getStatus().equalsIgnoreCase("Progress"))
        {
            tvCompleted.setVisibility(View.GONE);
            tvInProgress.setVisibility(View.VISIBLE);
            tvNew.setVisibility(View.GONE);
        }else if(requestList.get(position).getStatus().equalsIgnoreCase("Completed"))
        {
            tvCompleted.setVisibility(View.VISIBLE);
            tvInProgress.setVisibility(View.GONE);
            tvNew.setVisibility(View.GONE);
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date myDate = format.parse(requestList.get(position).getDateTime());
            String pattern = "EEE,MMM d, yyyy";
            DateFormat df = new SimpleDateFormat(pattern);
            String todayAsString = df.format(myDate);
            tvTime.setText(todayAsString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        tvPrice.setText("R"+requestList.get(position).getTotalAmount());
        tvfromLocation.setText(requestList.get(position).getCompanyLocation());
        tvToLocation.setText(requestList.get(position).getAddress());
    }
    @Override
    public int getItemCount() {
        return requestList.size();
    }
    public class SelectTimeViewHolder extends RecyclerView.ViewHolder {
        public SelectTimeViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
