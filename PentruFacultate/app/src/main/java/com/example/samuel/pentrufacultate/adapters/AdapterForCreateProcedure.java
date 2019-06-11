package com.example.samuel.pentrufacultate.adapters;

import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.samuel.pentrufacultate.R;

import java.util.List;

public class AdapterForCreateProcedure extends RecyclerView.Adapter<AdapterForCreateProcedure.ViewHolder> {

    private List<String> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public AdapterForCreateProcedure(Context context, List<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = mInflater.inflate(R.layout.create_procedure_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String hint = mData.get(position);



        int id=position+1;
        holder.textInputEditText.setHint(hint);
        holder.textInputEditText.setId(id);
        int idLength=String.valueOf(id).length();
//        holder.textInputLayout.setId((id*((int)Math.pow(10,idLength))+id));
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextInputEditText textInputEditText;
//        TextInputLayout textInputLayout;

        ViewHolder(View itemView) {
            super(itemView);
            textInputEditText = itemView.findViewById(R.id.procedure_edit_text_item);
//            textInputLayout = itemView.findViewById(R.id.procedure_text_input_item);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}