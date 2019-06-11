package com.example.samuel.pentrufacultate.adapters;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.samuel.pentrufacultate.R;
import com.example.samuel.pentrufacultate.models.ProcedureModel;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class AdapterForDisplayProcedure extends RecyclerView.Adapter<AdapterForDisplayProcedure.ViewHolder> {

    private ArrayList<ProcedureModel> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public AdapterForDisplayProcedure(Context context, ArrayList<ProcedureModel> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ConstraintLayout view = (ConstraintLayout) mInflater.inflate(R.layout.display_procedure_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        Log.d(TAG, "onBindViewHolder: "+  holder.numberProcedure);
        Log.d(TAG, "onBindViewHolder: "+  holder.titleProcedure);
        Log.d(TAG, "onBindViewHolder: "+  holder.numberStepsProcedure);
        holder.numberProcedure.setText(String.valueOf(position + 1));
        holder.titleProcedure.setText( mData.get(position).getName());
        holder.numberStepsProcedure.setText(String.valueOf(mData.get(position).getSteps().size()));
//        holder.textInputLayout.setId((id*((int)Math.pow(10,idLength))+id));
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView numberProcedure, titleProcedure, numberStepsProcedure;
//        TextInputLayout textInputLayout;

        ViewHolder(View itemView) {
            super(itemView);

            numberProcedure = itemView.findViewById(R.id.display_number_of_procedure);
            titleProcedure = itemView.findViewById(R.id.procedure_display_title);
            numberStepsProcedure = itemView.findViewById(R.id.procedure_display_number_of_steps);

//            textInputLayout = itemView.findViewById(R.id.procedure_text_input_item);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}