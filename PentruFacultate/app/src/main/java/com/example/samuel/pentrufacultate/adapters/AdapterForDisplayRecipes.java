package com.example.samuel.pentrufacultate.adapters;

import android.content.Context;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.samuel.pentrufacultate.activities.MainActivity;
import com.example.samuel.pentrufacultate.R;
import com.example.samuel.pentrufacultate.fragments.AllProceduresDisplayFragment;
import com.example.samuel.pentrufacultate.fragments.OneProcedureDisplayFragment;
import com.example.samuel.pentrufacultate.models.ProcedureModel;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;

public class AdapterForDisplayRecipes extends RecyclerView.Adapter<AdapterForDisplayRecipes.ViewHolder> {
    private static final String TAG = AdapterForDisplayRecipes.class.getSimpleName();
    private ArrayList<ProcedureModel> mData;
    private LayoutInflater mInflater;
    private MainActivity mMainActivity;
//    private final OnItemClickListener listener;


    // stores and recycles views as they are scrolled off screen
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleProcedure, numberStepsProcedure;

        ViewHolder(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: " + titleProcedure.getText());
                    OneProcedureDisplayFragment oneProcedureDisplayFragment = new OneProcedureDisplayFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("ProcedureToDisplayJSON", mData.get((Integer) titleProcedure.getTag()).toJson());
                    bundle.putString("userUid", MainActivity.getUserUid());
//                    mMainActivity.hideMainFragmentIfNeeded();
                    oneProcedureDisplayFragment.setArguments(bundle);
                    MainActivity.mCurrentFragment = oneProcedureDisplayFragment;
                    mMainActivity.mFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, oneProcedureDisplayFragment)
                            .addToBackStack("one_recipe")
                            .commit();
                }
            });
//            numberProcedure = itemView.findViewById(R.id.display_number_of_procedure);
            titleProcedure = itemView.findViewById(R.id.procedure_display_title);
            numberStepsProcedure = itemView.findViewById(R.id.procedure_display_number_of_steps);

        }


    }

    // data is passed into the constructor
    public AdapterForDisplayRecipes(Context context, ArrayList<ProcedureModel> data) {
        mMainActivity = (MainActivity) context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
//        this.listener = listener;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        FlexboxLayout view = (FlexboxLayout) inflater.inflate(R.layout.display_procedure_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


//        holder.numberProcedure.setText(String.valueOf(position + 1));
        holder.titleProcedure.setText(mData.get(position).getName());
        holder.titleProcedure.setTag(position);
        String numarPasi = String.valueOf(mData.get(position).getSteps().size());
        String textNumarPasi;
        textNumarPasi = "contine " + numarPasi + " pasi";

        holder.numberStepsProcedure.setText(textNumarPasi);

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


}