package com.icsseseguridad.locationsecurity.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.model.Incidence;

import java.util.List;

public class IncidenceSpinnerAdapter extends ArrayAdapter<Incidence> {

    private List<Incidence> incidences;
    private Context context;

    public IncidenceSpinnerAdapter(Context context, int textViewResourceId, List<Incidence> incidences) {
        super(context, textViewResourceId, incidences);
        this.incidences = incidences;
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
        label.setText(incidences.get(position).name);
        if (incidences.get(position).level > 1) {
            label.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        } else {
            label.setTextColor(context.getResources().getColor(R.color.black));
        }

        return row;
    }
}
