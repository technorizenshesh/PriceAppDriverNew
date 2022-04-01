package com.android.priceappdriver.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.priceappdriver.R;
import com.android.priceappdriver.utility.CancelClick;

import java.util.ArrayList;

/**
 * Created by Ravindra Birla on 05,August,2021
 */
public class CancelAdapter extends RecyclerView.Adapter<CancelAdapter.SelectTimeViewHolder> {

    private Context context;

    private ArrayList<String> requestList;

    private int selectedPosition = -1;

    private CancelClick cancelClick;

    public CancelAdapter(Context context, ArrayList<String> requestList, CancelClick cancelClick)
    {
        this.context = context;
        this.requestList = requestList;
        this.cancelClick = cancelClick;
    }

    @NonNull
    @Override
    public SelectTimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.cancel_item, parent, false);
        SelectTimeViewHolder viewHolder = new SelectTimeViewHolder(listItem);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull SelectTimeViewHolder holder, int position) {
        TextView tvCancel;
        CheckBox radioButton;
        radioButton = holder.itemView.findViewById(R.id.checkbox_discount);
        tvCancel = holder.itemView.findViewById(R.id.tvCancelContent);
        tvCancel.setText(requestList.get(position));

        if(selectedPosition == position)
        {
            radioButton.setChecked(true);
        }
        else
        {
            radioButton.setChecked(false);
        }

        radioButton.setOnClickListener(v ->
                {
                    selectedPosition = position;
                    cancelClick.cancelReason(position);
                    notifyDataSetChanged();
                }
                );

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
