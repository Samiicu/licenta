package com.example.samuel.pentrufacultate.adapters;

import android.content.Context;
import com.google.android.material.textfield.TextInputEditText;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.samuel.pentrufacultate.R;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterForCreateProcedure extends RecyclerView.Adapter<AdapterForCreateProcedure.ViewHolder> {
    private static final String TAG = "ADAPTERPRocedure";
    ArrayList<Boolean> flag = new ArrayList<>();
    private HashMap<String, String> dataInput;
    //    private List<String> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public AdapterForCreateProcedure(Context context, HashMap<String, String> dataInput) {
        this.mInflater = LayoutInflater.from(context);
        this.dataInput = dataInput;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = mInflater.inflate(R.layout.item_step, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        Log.e(TAG, "onBindViewHolder: " + dataInput.keySet().toString());
        int id = position + 1;
        final String hint = "Pasul " + id;


        holder.textInputEditText.setHint(hint);
        holder.textInputEditText.setText(dataInput.get(hint));
        holder.textInputEditText.setId(id);
        holder.textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    dataInput.put(hint, s.toString());
                    flag.add(position, true);
                }
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return dataInput.size();
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
//    String getItem(int id) {
//        return m.get(id);
//    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}