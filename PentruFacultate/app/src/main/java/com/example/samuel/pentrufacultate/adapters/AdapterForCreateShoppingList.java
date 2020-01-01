package com.example.samuel.pentrufacultate.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.samuel.pentrufacultate.R;
import com.example.samuel.pentrufacultate.fragments.AddShoppingList;
import com.example.samuel.pentrufacultate.models.StringHelper;
import com.example.samuel.pentrufacultate.products.models.CatalogProduct;
import com.google.android.material.textfield.TextInputEditText;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterForCreateShoppingList extends RecyclerView.Adapter<AdapterForCreateShoppingList.ViewHolder>   {
    private static final String TAG = StringHelper.getTag(AddShoppingList.class,AdapterForCreateShoppingList.class);
    ArrayList<Boolean> flag = new ArrayList<>();
    private  ArrayList<CatalogProduct> shoppingList;
    //    private List<String> mData;
    private LayoutInflater mInflater;
    private AdapterForCreateProcedure.ItemClickListener mClickListener;

    // data is passed into the constructor
    public AdapterForCreateShoppingList(Context context, ArrayList<CatalogProduct> shoppingList) {
        this.mInflater = LayoutInflater.from(context);
        this.shoppingList = shoppingList;

    }

    // inflates the row layout from xml when needed
    @Override
    public AdapterForCreateShoppingList.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = mInflater.inflate(R.layout.item_shopping_item, parent, false);
        return new AdapterForCreateShoppingList.ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(AdapterForCreateShoppingList.ViewHolder holder, final int position) {
        Log.e(TAG, "onBindViewHolder: " + shoppingList.get(position).getName());
        holder.shoppingItem.setText(shoppingList.get(position).getName());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return shoppingList.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView shoppingItem;
//        TextInputLayout textInputLayout;

        ViewHolder(View itemView) {
            super(itemView);
            shoppingItem = itemView.findViewById(R.id.text_shopping_item);
//            textInputLayout = itemView.findViewById(R.id.procedure_text_input_item);
        }


    }

}
