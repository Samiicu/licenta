package com.example.samuel.pentrufacultate.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.samuel.pentrufacultate.R;
import com.example.samuel.pentrufacultate.fragments.AddShoppingList;
import com.example.samuel.pentrufacultate.managers.DataManager;
import com.example.samuel.pentrufacultate.models.ShoppingItem;
import com.example.samuel.pentrufacultate.models.ShoppingList;
import com.example.samuel.pentrufacultate.models.StringHelper;

import java.util.ArrayList;

public class AdapterForCreateShoppingList extends RecyclerView.Adapter<AdapterForCreateShoppingList.ViewHolder> {
    private static final String TAG = StringHelper.getTag(AddShoppingList.class, AdapterForCreateShoppingList.class);
    ArrayList<Boolean> flag = new ArrayList<>();
    private ShoppingList shoppingList;
    //    private List<String> mData;
    private LayoutInflater mInflater;
    private AdapterForCreateProcedure.ItemClickListener mClickListener;

    // data is passed into the constructor
    public AdapterForCreateShoppingList(Context context, ShoppingList shoppingList) {
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
        Log.e(TAG, "onBindViewHolder: " + shoppingList.getItemFromPosition(position).getName());
        ShoppingItem item = shoppingList.getItemFromPosition(position);
        holder.nameShoppingItem.setText(item.getName());

        holder.checkBoxShoppingItemItem.setChecked(item.isChecked());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return shoppingList.getSize();
    }

    public String getShoppingListTitle() {
        return shoppingList.getTitle();
    }
     class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameShoppingItem;

        CheckBox checkBoxShoppingItemItem;


        ViewHolder(View itemView) {
            super(itemView);
            nameShoppingItem = itemView.findViewById(R.id.text_shopping_item);
            checkBoxShoppingItemItem = itemView.findViewById(R.id.checkbox_shopping_list_item);
            checkBoxShoppingItemItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    DataManager.getInstance(null).notifyStatusChange();
                }
            });
        }


    }

}
