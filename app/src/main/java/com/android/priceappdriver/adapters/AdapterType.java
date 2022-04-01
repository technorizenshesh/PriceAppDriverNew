package com.android.priceappdriver.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.priceappdriver.R;
import com.android.priceappdriver.model.SuccessResVehicleType;

import java.util.ArrayList;

public class AdapterType extends BaseAdapter {
    Context context;
    ArrayList<SuccessResVehicleType.Result> categoryList;
    LayoutInflater inflater;


    public AdapterType(Context context, ArrayList<SuccessResVehicleType.Result> categoryList) {
        this.context = context;
        this.categoryList = categoryList;

        inflater = (LayoutInflater.from(context));

    }

    @Override
    public int getCount() {
        return categoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.item_spinner, null);
        TextView names = convertView.findViewById(R.id.item);
        RelativeLayout mainView = convertView.findViewById(R.id.mainView);
        names.setText(categoryList.get(position).getCarName());


      /*  mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickPositionListener.clickPos(position,status);
            }
        });*/

        return convertView;
    }
}
