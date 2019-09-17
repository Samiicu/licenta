package com.example.samuel.pentrufacultate.adapters;

import android.content.Context;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.samuel.pentrufacultate.activities.MainActivity;
import com.example.samuel.pentrufacultate.R;

import java.util.ArrayList;

public class AdapterForDisplaySteps extends RecyclerView.Adapter<AdapterForDisplaySteps.ViewHolder> {
    private static final String TAG = "MY_APP_Display_Fragment";
    private ArrayList<String> mSteps;
    private LayoutInflater mInflater;
    private MainActivity mMainActivity;
//    private final OnItemClickListener listener;


    // data is passed into the constructor
    public AdapterForDisplaySteps(Context context, ArrayList<String> mSteps) {
        mMainActivity = (MainActivity) context;
        this.mInflater = LayoutInflater.from(context);
        this.mSteps = mSteps;
        Log.d(TAG, "AdapterForDisplaySteps: "+mSteps);
//        this.listener = listener;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ConstraintLayout view = (ConstraintLayout) mInflater.inflate(R.layout.display_step_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.stepText.setText(mSteps.get(position));
        holder.stepText.setTag(position+1);
        holder.positionStep.setText(String.format("%s.", String.valueOf(position+1)));

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mSteps.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView stepText, positionStep;

        ViewHolder(final View itemView) {
            super(itemView);
            stepText = itemView.findViewById(R.id.step_text);
            positionStep = itemView.findViewById(R.id.step_number);

        }
    }
}