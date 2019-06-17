package com.example.samuel.pentrufacultate.adapters;

import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.samuel.pentrufacultate.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterForCreateProcedure extends ArrayAdapter<String> {
    private final static String TAG = "AdapterCreateProcedure";
    private int resourceLayout;
    private Context mContext;

    public AdapterForCreateProcedure(Context context, int resource, ArrayList<String> items) {
        super(context, resource, items);
        this.resourceLayout = resource;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "getView:"+position );
        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            v = vi.inflate(resourceLayout, null);
        }

        String p = getItem(position);

        if (p != null) {
            TextInputEditText textInputEditText = v.findViewById(R.id.procedure_edit_text_item);
            if (textInputEditText != null) {
                textInputEditText.setHint(p);
            }
            else
            {
                Log.e(TAG, "getView:textInput is NULL! " );
            }
        }

        return v;
    }
public void addInput(ListView listView,String value){}
}