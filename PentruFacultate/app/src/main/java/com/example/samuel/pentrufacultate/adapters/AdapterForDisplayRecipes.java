package com.example.samuel.pentrufacultate.adapters;

import android.content.Context;
import android.os.Bundle;

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
import com.example.samuel.pentrufacultate.managers.DataManager;
import com.example.samuel.pentrufacultate.models.RecipeModel;
import com.example.samuel.pentrufacultate.models.StringHelper;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class AdapterForDisplayRecipes extends RecyclerView.Adapter<AdapterForDisplayRecipes.ViewHolder> {
    private static final String TAG = StringHelper.getTag(AllProceduresDisplayFragment.class, AdapterForDisplayRecipes.class);
    private ArrayList<RecipeModel> mData;
    private RecipeModel mRecentlyDeletedItem;
    private int mRecentlyDeletedItemPosition;
    private LayoutInflater mInflater;
    private boolean deleteReverted = false;
    private MainActivity mMainActivity;

    public void deleteItem(int position) {
        mRecentlyDeletedItem = mData.get(position);
        mRecentlyDeletedItemPosition = position;
        mData.remove(position);
        notifyItemRemoved(position);
        showUndoSnackbar();
    }
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
                    bundle.putString("ProcedureToDisplayJSON", DataManager.getInstance(mMainActivity).getRecipeWithTitle((String) titleProcedure.getText()).toJson());
                    bundle.putString("userUid", DataManager.getInstance(mMainActivity).getCurrentUserUid());
//                    mMainActivity.hideMainFragmentIfNeeded();
                    oneProcedureDisplayFragment.setArguments(bundle);
                    MainActivity.mCurrentFragment = oneProcedureDisplayFragment;
                    mMainActivity.mFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, oneProcedureDisplayFragment)
                            .addToBackStack(StringHelper.TAG_DISPLAY_ONE_RECIPE_FRAGMENT)
                            .commit();
                }
            });
//            numberProcedure = itemView.findViewById(R.id.display_number_of_procedure);
            titleProcedure = itemView.findViewById(R.id.procedure_display_title);
            numberStepsProcedure = itemView.findViewById(R.id.procedure_display_number_of_steps);

        }


    }

    // data is passed into the constructor
    public AdapterForDisplayRecipes(Context context, ArrayList<RecipeModel> data) {
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
        holder.titleProcedure.setText(mData.get(position).getTitle());
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

    private void showUndoSnackbar() {
        Log.i(TAG, "showUndoSnackbar: ");
        deleteReverted = false;
        View view = mMainActivity.findViewById(R.id.drawer_layout);
        Snackbar snackbar = Snackbar.make(view, R.string.snack_bar_text,
                Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.snack_bar_undo, v -> undoDelete());

        Snackbar.Callback snackBarCallBack = new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                if (!deleteReverted) {
                    DataManager.getInstance(mMainActivity).removeRecipeFromDatabase(mRecentlyDeletedItem);
                }
            }
        };
        snackbar.addCallback(snackBarCallBack);

        snackbar.show();

    }

    private void undoDelete() {
        Log.i(TAG, "undoDelete: ");
        deleteReverted = true;
        mData.add(mRecentlyDeletedItemPosition,
                mRecentlyDeletedItem);
        notifyItemInserted(mRecentlyDeletedItemPosition);
    }
}