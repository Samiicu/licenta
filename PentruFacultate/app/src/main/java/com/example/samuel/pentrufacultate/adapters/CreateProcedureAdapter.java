//package com.example.samuel.pentrufacultate.adapters;
//
//import android.app.Activity;
//import android.support.annotation.NonNull;
//import android.support.design.widget.TextInputEditText;
//import android.support.design.widget.TextInputLayout;
//import android.support.v7.widget.RecyclerView;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.view.LayoutInflater;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import com.example.samuel.pentrufacultate.MainActivity;
//import com.example.samuel.pentrufacultate.R;
//
//public class CreateProcedureAdapter extends RecyclerView.Adapter<CreateProcedureAdapter.MyViewHolder> {
//    private String[] mDataset;
//    private MainActivity currentActivity;
//    private int createdView = 0;
//
//    // Provide a reference to the views for each data item
//    // Complex data items may need more than one view per item, and
//    // you provide access to all the views for a data item in a view holder
//    public static class MyViewHolder extends RecyclerView.ViewHolder {
//        // each data item is just a string in this case
//        public TextView textView;
//        public MyViewHolder(TextView v) {
//            super(v);
//            textView = v;
//        }
//    }
//
//    // Provide a suitable constructor (depends on the kind of dataset)
//    public CreateProcedureAdapter(String[] myDataset, MainActivity activity) {
//        mDataset = myDataset;
//        currentActivity = activity;
//    }
//
//    // Create new views (invoked by the layout manager)
//    @Override
//    public CreateProcedureAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
//                                                                  int viewType) {
//
//        // create a new view
//        TextView v = (TextView) LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.my_text_view, parent, false);
////        ...
//        MyViewHolder vh = new MyViewHolder(v);
//        return vh;
//    }
//
//
//
//    // Replace the contents of a view (invoked by the layout manager)
//    @Override
//    public void onBindViewHolder(MyViewHolder holder, int position) {
//        // - get element from your dataset at this position
//        // - replace the contents of the view with that element
//        holder.textView.setText(mDataset[position]);
//
//    }
//
//    // Return the size of your dataset (invoked by the layout manager)
//    @Override
//    public int getItemCount() {
//        return mDataset.length;
//    }
//
//    private TextInputLayout getTextInputLayout(int pas){
//        LayoutInflater inflater = LayoutInflater.from(currentActivity);
//        TextInputEditText textInputEditText = (TextInputEditText) inflater.inflate(R.layout.input_step_a, null);
//        textInputEditText.setId(pas);
//        textInputEditText.setHint("Pasul " + pas);
//        textInputEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (s.length() > 0 && createdView < pas) {
//                    ++createdView;
//                    createInput(pas + 1);
//                }
//            }
//        });
//        TextInputLayout textInputLayout = (TextInputLayout) inflater.inflate(R.layout.input_step_b, null);
//        textInputLayout.addView(textInputEditText);
//    }
//}