package com.example.samuel.pentrufacultate.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.samuel.pentrufacultate.R;
import com.example.samuel.pentrufacultate.products.models.get_stores_for_products.Product;
import com.example.samuel.pentrufacultate.products.models.get_stores_for_products.RetailStore;
import com.google.zxing.common.StringUtils;

import java.util.ArrayList;

public class RetailStoreListAdapter extends RecyclerView.Adapter<RetailStoreListAdapter.RestaurantViewHolder> {
    private ArrayList<RetailStore> mRetailStores = new ArrayList<>();
    String NO_PRICE = "0";
    private Context mContext;

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

        ImageView mRestaurantImageView;
        TextView mNameTextView;
        TextView mCategoryTextView;
        TextView mDistanceView;
        TextView mFoundItems;
        TextView mPriceFoundItems;
        private Context mContext;

        public RestaurantViewHolder(View itemView) {
            super(itemView);
            mRestaurantImageView = itemView.findViewById(R.id.restaurantImageView);
            mNameTextView = itemView.findViewById(R.id.restaurantNameTextView);
            mCategoryTextView = itemView.findViewById(R.id.categoryTextView);
            mDistanceView = itemView.findViewById(R.id.distanceTextView);
            mFoundItems = itemView.findViewById(R.id.found_items);
            mPriceFoundItems = itemView.findViewById(R.id.price_products_found);
            mContext = itemView.getContext();
        }

        @SuppressLint("SetTextI18n")
        public void bindRestaurant(RetailStore retailStore) {
            mNameTextView.setText(retailStore.getName());
            mCategoryTextView.setText(retailStore.getType().getName());
            mDistanceView.setText("Distance: " + retailStore.getDistance() + " km");
//            mRestaurantImageView.setImageURI(Uri.parse(retailStore.getLogo().getLogouri()));
            String availableItems = getAvailableProducts(retailStore);
            mFoundItems.setText("Produse gasite : " + availableItems + " din " + retailStore.getProducts().getProductsList().size());
            mPriceFoundItems.setText("Pretul pentru produsele gasite este: " + retailStore.getBasketprice());

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