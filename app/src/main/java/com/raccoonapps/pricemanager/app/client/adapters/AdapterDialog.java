package com.raccoonapps.pricemanager.app.client.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.raccoonapps.pricemanager.app.R;
import com.raccoonapps.pricemanager.app.client.model.Tag;

import java.util.List;

/**
 * Adapter for lists in AlertDialog. This adapter fills list and provides functionality to select listElement
 * */
public class AdapterDialog extends SelectableAdapter<AdapterDialog.ViewHolder>{


    List<Tag> tags;
    Context context;

    public AdapterDialog(List<Tag> tags, Context context) {
        this.tags = tags;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_dialog, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Tag tag = tags.get(position);

        holder.id.setText(tag.getId());
        holder.text.setText(tag.getText());

        holder.linearLayout.setBackgroundColor(tag.isActive());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i<tags.size(); i++) {
                    tags.get(i).setIsActive(Color.parseColor("#ffffff"));
                    notifyItemChanged(i);
                }
                tag.setIsActive(Color.parseColor("#6816b5ff"));
                notifyItemChanged(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    public void setStringMap(List<Tag> tags) {
        this.tags = tags;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView id;
        TextView text;
        LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            id = (TextView) itemView.findViewById(R.id.id);
            text = (TextView) itemView.findViewById(R.id.text);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.item);
        }
    }
}