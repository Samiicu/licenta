package com.example.samuel.pentrufacultate.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.samuel.pentrufacultate.R;
import com.example.samuel.pentrufacultate.products.models.get_stores_for_products.Product;
import com.example.samuel.pentrufacultate.products.models.get_stores_for_products.RetailStore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RetailStoreListAdapter extends RecyclerView.Adapter<RetailStoreListAdapter.RestaurantViewHolder> {
    private ArrayList<RetailStore> mRetailStores = new ArrayList<>();
    String NO_PRICE = "0";
    Context mContext;

    public RetailStoreListAdapter(Context context, ArrayList<RetailStore> restaurants) {
        mContext = context;
        mRetailStores = restaurants;

    }

    @Override
    public RetailStoreListAdapter.RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_result_search_item, parent, false);
        RestaurantViewHolder viewHolder = new RestaurantViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RetailStoreListAdapter.RestaurantViewHolder holder, int position) {
        holder.bindRestaurant(mRetailStores.get(position));
    }

    @Override
    public int getItemCount() {
        return mRetailStores.size();
    }

    public class RestaurantViewHolder extends RecyclerView.ViewHolder {

        ImageView mStoreImageView;
        ImageButton moreAbout;
        TextView mNameTextView;
        TextView mCategoryTextView;
        TextView mDistanceView;
        TextView mFoundItems;
        TextView mPriceFoundItems;
        private Context mContext;

        public RestaurantViewHolder(View itemView) {
            super(itemView);
            mStoreImageView = itemView.findViewById(R.id.storeImageView);
            mNameTextView = itemView.findViewById(R.id.restaurantNameTextView);
            mCategoryTextView = itemView.findViewById(R.id.categoryTextView);
            mDistanceView = itemView.findViewById(R.id.distanceTextView);
            mFoundItems = itemView.findViewById(R.id.found_items);
            mPriceFoundItems = itemView.findViewById(R.id.price_products_found);
            moreAbout = itemView.findViewById(R.id.more_about);
            mContext = itemView.getContext();
        }

        @SuppressLint("SetTextI18n")
        public void bindRestaurant(RetailStore retailStore) {

            mNameTextView.setText(retailStore.getName());
            mCategoryTextView.setText(retailStore.getType().getName());
            mDistanceView.setText("Distance: " + retailStore.getDistance() + " km");
            String availableItems = getAvailableProducts(retailStore);
            mFoundItems.setText("Produse gasite : " + availableItems + " din " + retailStore.getProducts().getProductsList().size());
            mPriceFoundItems.setText("Pretul pentru produsele gasite este: " + retailStore.getBasketprice());
            moreAbout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    ViewGroup viewGroup = v.findViewById(android.R.id.content);
                    View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.customview, viewGroup, false);
                    SearchResponseAdapter searchResponseAdapter = new SearchResponseAdapter(mContext, retailStore.getProducts().getProductsList());
                    TextView textView= (TextView) ((LinearLayout) dialogView).getChildAt(1);
                    textView.setText(retailStore.getName());
                    RecyclerView foundItemsView = (RecyclerView) ((LinearLayout) dialogView).getChildAt(3);
                    foundItemsView.setAdapter(searchResponseAdapter);
                    RecyclerView.LayoutManager layoutManager =
                            new LinearLayoutManager(mContext);
                    foundItemsView.setLayoutManager(layoutManager);
                    foundItemsView.setHasFixedSize(true);
                    foundItemsView.addItemDecoration(new DividerItemDecoration(mContext,
                            DividerItemDecoration.VERTICAL));
                    builder.setView(dialogView);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });
            Picasso.get().load(retailStore.getLogo().getLogouri()).into(mStoreImageView);

        }
    }

    private String getAvailableProducts(RetailStore retailStore) {
        int count = 0;
        for (Product product : retailStore.getProducts().getProductsList()) {
            if (product.getPrice() != null && !NO_PRICE.equals(product.getPrice())) {
                count++;
            }
        }
        return String.valueOf(count);
    }
}