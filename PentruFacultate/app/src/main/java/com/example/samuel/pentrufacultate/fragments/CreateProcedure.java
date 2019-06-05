package com.example.samuel.pentrufacultate.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.samuel.pentrufacultate.MainActivity;
import com.example.samuel.pentrufacultate.R;

public class CreateProcedure extends Fragment {
    LinearLayout linearLayout;
    RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    int createdView = 0;
    private final static String TAG = "CREATE_PROCDURE";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_create_procedure, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = getActivity().findViewById(R.id.input_steps_recycler_view);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLinearLayoutManager);



        createInput(1);


    }

    private void createInput(final int pas) {

        LayoutInflater inflater = LayoutInflater.from(getActivity()); // or (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextInputEditText textInputEditText = (TextInputEditText) inflater.inflate(R.layout.input_step_a, null);
        textInputEditText.setId(pas);
        textInputEditText.setHint("Pasul " + pas);
        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && createdView < pas) {
                    ++createdView;
                    createInput(pas + 1);
                }
            }
        });
        TextInputLayout textInputLayout = (TextInputLayout) inflater.inflate(R.layout.input_step_b, null);
        textInputLayout.addView(textInputEditText);
        linearLayout = getActivity().findViewById(R.id.input_steps_linear_layout);


        linearLayout.addView(textInputLayout);
        Log.d(TAG, "createInput: DONE");
    }
}
