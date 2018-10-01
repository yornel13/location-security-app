package com.icsseseguridad.locationsecurity.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.service.entity.Incidence;
import com.icsseseguridad.locationsecurity.service.entity.VehicleType;

import java.util.List;

public class VehicleTypeSpinnerAdapter extends ArrayAdapter<VehicleType> {

    private List<VehicleType> vehicles;
    private Context context;

    public VehicleTypeSpinnerAdapter(Context context, int textViewResourceId, List<VehicleType> vehicles) {
        super(context, textViewResourceId, vehicles);
        this.vehicles = vehicles;
        this.context = context;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(  Context.LAYOUT_INFLATER_SERVICE );
        View row = inflater.inflate(R.layout.spinner_item, parent, false);
        TextView label = row.findViewById(R.id.text);
        label.setText(vehicles.get(position).name);
        if (position == 0) {
            label.setTextColor(context.getResources().getColor(R.color.colorGrayAgate));
        } else {
            label.setTextColor(context.getResources().getColor(R.color.colorGray));
        }
        return row;
    }
}
