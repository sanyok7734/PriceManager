package com.managerprice.racconapps.managerprice.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.managerprice.racconapps.managerprice.MainActivity;
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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Product product = products.get(position);

        holder.title.setText(product.getTitle());
        holder.price.setText(product.getPrice());

        holder.item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MainActivity.bus.post(product);
                holder.item.showContextMenu();
                return true;
            }
        });
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
        CardView item;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            price = (TextView) itemView.findViewById(R.id.price);
            item = (CardView) itemView.findViewById(R.id.item);
            item.setOnCreateContextMenuListener(onCreateContextMenuListener);
        }

        View.OnCreateContextMenuListener onCreateContextMenuListener = new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, 0, 0, "Change");
                menu.add(0, 1, 0, "Delete");
            }
        };
    }
}
