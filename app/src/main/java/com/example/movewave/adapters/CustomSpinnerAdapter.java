package com.example.movewave.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.movewave.R;

import java.util.List;

public class CustomSpinnerAdapter extends ArrayAdapter {
    TextView tvItem;
    private Context context;
    private int tvResourceId;
    private List<String> objects;
    private LayoutInflater layoutInflater;

    public CustomSpinnerAdapter(@NonNull Context context, int textViewResourceId, @NonNull List objects,
                                LayoutInflater layoutInflater) {
        super(context, textViewResourceId, objects);
        this.context=context;
        this.tvResourceId=textViewResourceId;
        this.objects=objects;
        this.layoutInflater=layoutInflater;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        tvItem = view.findViewById(R.id.text1);

        String item = objects.get(position);
        tvItem.setText(item);

        return view;
    }
}
