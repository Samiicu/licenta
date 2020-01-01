package com.example.samuel.pentrufacultate.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.samuel.pentrufacultate.R;
import com.example.samuel.pentrufacultate.activities.MainActivity;
import com.example.samuel.pentrufacultate.adapters.AdapterForCreateProcedure;
import com.example.samuel.pentrufacultate.adapters.AdapterForCreateShoppingList;
import com.example.samuel.pentrufacultate.managers.DataManager;
import com.example.samuel.pentrufacultate.models.RecipeModel;
import com.example.samuel.pentrufacultate.models.StringHelper;
import com.example.samuel.pentrufacultate.products.models.CatalogProduct;
import com.example.samuel.pentrufacultate.products.storage.DatabaseHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.example.samuel.pentrufacultate.models.StringHelper.USER_UID_EXTRA;

public class AddShoppingList extends Fragment {


    private static final String ACTION_SHOW_RECIPES = "show_recipes";
    private TextView nameText;
    private ImageButton btnAddNewItem;
    DatabaseHelper mDataBaseHelper;
    private ArrayList<CatalogProduct> shoppingList;
    TextInputEditText procedureStepText;
    AdapterForCreateShoppingList adapter;
    AutoCompleteTextView shoppingItemInput;


    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private final static String TAG = StringHelper.getTag(MainActivity.class, AddShoppingList.class);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_shopping_list, container, false);
        btnAddNewItem = view.findViewById(R.id.btn_add_to_shopping_list);
        shoppingItemInput = view.findViewById(R.id.shopping_item_input);

//        nameText = view.findViewById(R.id.display_recipe_title);

        //create the ArrayList from database
        mDataBaseHelper = DataManager.getInstance(getContext()).getLocalDB();

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
        btnAddNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputShoppingItem = shoppingItemInput.getText().toString();
                boolean isInShoppingList = false;
                for (CatalogProduct item : shoppingList
                ) {
                    if (item.getName().equals(inputShoppingItem)) {
                        isInShoppingList = true;
                    }
                }

                if (!isInShoppingList) {
                    CatalogProduct item = mDataBaseHelper.getProductByName(inputShoppingItem);
                    shoppingList.add(item);
                    adapter.notifyItemInserted(shoppingList.size() - 1);
                } else {
                    Log.i(TAG, "onClick: already exist in shopping list");
                }

//                openAddItemDialog(getContext());
            }
        });
        RecyclerView inputRecipesRecyclerView = view.findViewById(R.id.input_steps_recycler_view);
//        RecyclerView.ItemDecoration itemDecoration= new RecyclerView.ItemDecoration;itemDecoration.onDraw();
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
        shoppingList = new ArrayList<CatalogProduct>();
        adapter = new AdapterForCreateShoppingList(getContext(), shoppingList);
//        adapter.setClickListener(this);
        inputRecipesRecyclerView.setAdapter(adapter);
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

    public void openAddItemDialog(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.add_new_item_dialog_layout);
        dialog.show();
    }

}
