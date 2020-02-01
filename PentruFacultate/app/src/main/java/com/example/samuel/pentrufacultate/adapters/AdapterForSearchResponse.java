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

import java.util.ArrayList;
import java.util.List;

public class AdapterForSearchResponse extends RecyclerView.Adapter<AdapterForSearchResponse.RestaurantViewHolder> {
    private List<Product> mStoreItems = new ArrayList<>();
    private String NO_PRICE = "0";
    private Context mContext;

    public AdapterForSearchResponse(Context context, List<Product> products) {
        mContext = context;
        mStoreItems = products;

    }

    @Override
    public AdapterForSearchResponse.RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_detalied_result_search_item, parent, false);
        RestaurantViewHolder viewHolder = new RestaurantViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AdapterForSearchResponse.RestaurantViewHolder holder, int position) {
        holder.bindRestaurant(mStoreItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mStoreItems.size();
    }

    public class RestaurantViewHolder extends RecyclerView.ViewHolder {

        ImageView mProductStatus;

        TextView mProductTitle;
        TextView mProductCategory;


        TextView mProductPrice;


        public RestaurantViewHolder(View itemView) {
            super(itemView);
            mProductTitle = itemView.findViewById(R.id.product_title);
            mProductStatus = itemView.findViewById(R.id.product_state_img);
            mProductCategory = itemView.findViewById(R.id.product_category);
            mProductPrice = itemView.findViewById(R.id.product_price);

            mContext = itemView.getContext();
        }

        @SuppressLint("SetTextI18n")
        public void bindRestaurant(Product product) {
            String myString=product.getName()!=null?product.getName():product.getCatprod().getName();
            String upperString = myString.substring(0,1).toUpperCase() + myString.substring(1).toLowerCase();
            mProductTitle.setText(upperString);

            if (NO_PRICE.equals(product.getPrice()) || product.getPrice() == null) {
                mProductStatus.setImageResource(R.drawable.ic_cancel_red_24dp);

            } else {
                mProductStatus.setImageResource(R.drawable.
                        ic_done_green_24dp);
                mProductPrice.setText("Pret:" + product.getPrice());
            }
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