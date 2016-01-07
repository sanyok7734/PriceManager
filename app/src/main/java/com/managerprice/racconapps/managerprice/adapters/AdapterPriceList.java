package com.managerprice.racconapps.managerprice.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.managerprice.racconapps.managerprice.R;
import com.managerprice.racconapps.managerprice.model.Product;

import java.util.List;

/**
 * Created by sanyok on 03.01.16.
 */
public class AdapterPriceList extends RecyclerView.Adapter<AdapterPriceList.ViewHolder> {


    List<Product> products;
    Context context;

    public AdapterPriceList(List<Product> products, Context context) {
        this.products = products;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Product product = products.get(position);

        holder.title.setText(product.getTitle());
        holder.price.setText(product.getPrice());
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void addItem(Product product) {
        products.add(product);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView price;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            price = (TextView) itemView.findViewById(R.id.price);
        }
    }
}
