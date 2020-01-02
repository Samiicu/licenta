package com.example.samuel.pentrufacultate.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.samuel.pentrufacultate.R;
import com.example.samuel.pentrufacultate.activities.MainActivity;
import com.example.samuel.pentrufacultate.adapters.AdapterForCreateShoppingList;
import com.example.samuel.pentrufacultate.managers.DataManager;
import com.example.samuel.pentrufacultate.models.ShoppingItem;
import com.example.samuel.pentrufacultate.models.ShoppingList;
import com.example.samuel.pentrufacultate.models.StringHelper;
import com.example.samuel.pentrufacultate.products.storage.DatabaseHelper;

import java.util.Objects;

public class AddShoppingList extends Fragment {

    private ImageButton addNewItemButton;
    private Button saveShoppingListButton, checkShoppingList;
    private DatabaseHelper mDataBaseHelper;
    private AdapterForCreateShoppingList adapter;
    private AutoCompleteTextView shoppingItemInput;
    private DataManager dataManager;

    private final static String TAG = StringHelper.getTag(MainActivity.class, AddShoppingList.class);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_shopping_list, container, false);
        addNewItemButton = view.findViewById(R.id.btn_add_to_shopping_list);
        shoppingItemInput = view.findViewById(R.id.shopping_item_input);
        saveShoppingListButton = view.findViewById(R.id.save_shopping_list_btn);
        checkShoppingList = view.findViewById(R.id.check_the_shopping_list_btn);
        dataManager = DataManager.getInstance(getContext());


        //create the ArrayList from database
        mDataBaseHelper = dataManager.getLocalDB();

        final String[] myData;
        myData = mDataBaseHelper.getAllProducts().toArray(new String[0]);

        //Finally Set the adapter to AutoCompleteTextView like this,
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Objects.requireNonNull(this.getContext()), R.layout.search_shopping_list_item, myData);
        //populate the list to the AutoCompleteTextView controls
        shoppingItemInput.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        nameText.setText(DataManager.getInstance(getContext()).getSelectedRecipeTitle());

        ////////////////////////

        //////////////////////////
        addNewItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputShoppingItem = shoppingItemInput.getText().toString();
                if (!inputShoppingItem.equals("")) {
                    boolean isInShoppingList = false;
                    for (ShoppingItem item : dataManager.getCurrentShoppingList().getShoppingItems()
                    ) {
                        if (item.getName().equals(inputShoppingItem)) {
                            isInShoppingList = true;
                        }
                    }

                    if (!isInShoppingList) {
                        ShoppingItem item = new ShoppingItem(mDataBaseHelper.getProductByName(inputShoppingItem).getName());
                        dataManager.addItemToShoppingList(item);
                        dataManager.notifyShoppingListItemInserted(dataManager.getCurrentShoppingList().getSize() - 1);
                    } else {
                        Log.w(TAG, "onClick: already exist in shopping list");
                    }
                } else {
                    Log.w(TAG, "onClick: input field is empty");
                }
            }
        });
        RecyclerView inputRecipesRecyclerView = view.findViewById(R.id.input_steps_recycler_view);
        inputRecipesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public void onItemsAdded(@NonNull RecyclerView recyclerView, int positionStart, int itemCount) {
                super.onItemsAdded(recyclerView, positionStart, itemCount);
                inputRecipesRecyclerView.scrollToPosition(positionStart);
                inputRecipesRecyclerView.requestChildFocus(recyclerView.getChildAt(positionStart), recyclerView.getFocusedChild());
            }
        });
        inputRecipesRecyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);
        ////temporally will be empty at start-//todo synch with cloud db



        inputRecipesRecyclerView.setAdapter(dataManager.getCurrentShoppingListAdapter());


        ///////////////////
        saveShoppingListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DataManager dataManager = DataManager.getInstance(getContext());
                ShoppingList shoppingList = new ShoppingList();
                shoppingList.setTitle(dataManager.getSelectedRecipeTitle());

                int itemsCount = inputRecipesRecyclerView.getChildCount();
                for (int i = 0; i < itemsCount; i++) {
                    ShoppingItem newShoppingItem = new ShoppingItem();
                    View view = inputRecipesRecyclerView.getChildAt(i);
                    TextView itemName = view.findViewById(R.id.text_shopping_item);
                    TextView quantityItem = view.findViewById(R.id.quantity_shopping_list_item);
                    TextView measureItem = view.findViewById(R.id.measure_item_shopping_list);
                    CheckBox checkBox = view.findViewById(R.id.checkbox_shopping_list_item);

                    newShoppingItem.setName(itemName.getText().toString());
                    newShoppingItem.setQuantity(quantityItem.getText().toString());
                    newShoppingItem.setMeasure(measureItem.getText().toString());
                    newShoppingItem.setChecked(checkBox != null && checkBox.isChecked());
                    shoppingList.addItemToShoppingList(newShoppingItem);

                }
                dataManager.saveShoppingList(shoppingList);


            }
        });

    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause: ");
        super.onPause();
    }
}
