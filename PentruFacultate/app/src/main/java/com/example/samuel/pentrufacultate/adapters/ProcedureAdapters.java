package com.example.samuel.pentrufacultate.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.samuel.pentrufacultate.R;
import com.example.samuel.pentrufacultate.models.ProcedureModel;

import java.util.ArrayList;

public class ProcedureAdapters extends ArrayAdapter<ProcedureModel> {
    private int resourceLayout;
    private Context contextLayout;
    private ArrayList<ProcedureModel> allProcedureModels;

    public ProcedureAdapters(Context context, int resource, ArrayList<ProcedureModel> objects) {
        super(context, resource, objects);
        setResourceLayout(resource);
        setContextLayout(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ProcedureModel mProcedureModel = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(contextLayout).inflate(R.layout.item_procedure_list, null);
        }
        final TextView mProcedureTitle = convertView.findViewById(R.id.item_procedure_title);
        final TextView mProcedureData = convertView.findViewById(R.id.item_procedure_date);
        final TextView mProcedureDescription = convertView.findViewById(R.id.item_procedure_description);
        mProcedureTitle.setText(mProcedureModel.getName());
        mProcedureData.setText(mProcedureModel.getData_creation().toString());

        return convertView;
    }

    public int getResourceLayout() {
        return resourceLayout;
    }

    private void setResourceLayout(int resourceLayout) {
        this.resourceLayout = resourceLayout;
    }

    public Context getContextLayout() {
        return contextLayout;
    }

    private void setContextLayout(Context contextLayout) {
        this.contextLayout = contextLayout;
    }
}
