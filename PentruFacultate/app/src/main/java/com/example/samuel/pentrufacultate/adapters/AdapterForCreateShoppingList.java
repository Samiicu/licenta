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
import com.example.samuel.pentrufacultate.fragments.AddShoppingListFragment;
import com.example.samuel.pentrufacultate.managers.DataManager;
import com.example.samuel.pentrufacultate.models.ShoppingItem;
import com.example.samuel.pentrufacultate.models.ShoppingList;
import com.example.samuel.pentrufacultate.models.StringHelper;

public class AdapterForCreateShoppingList extends RecyclerView.Adapter<AdapterForCreateShoppingList.ViewHolder> {

    private static final String TAG = StringHelper.getTag(AddShoppingListFragment.class, AdapterForCreateShoppingList.class);

    private ShoppingList shoppingList;
    private LayoutInflater mInflater;

    public AdapterForCreateShoppingList(Context context, ShoppingList shoppingList) {
        this.mInflater = LayoutInflater.from(context);
        this.shoppingList = shoppingList;

    }

    @Override
    public AdapterForCreateShoppingList.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_shopping_item, parent, false);
        return new AdapterForCreateShoppingList.ViewHolder(view);
    }

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
                    DataManager.getInstance().notifyStatusChange();
                }
            });
        }


    }

}
