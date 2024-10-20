package com.example.news.adapters;// NavbarAdapter.java
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.news.R;
import com.example.news.fragments.HomeFragment;

import java.util.ArrayList;
import java.util.List;

public class NavbarAdapter extends RecyclerView.Adapter<NavbarAdapter.NavbarViewHolder> {

    ArrayList<String> navItems;
    Context context;
    int selectedPosition = 0;
    private OnCategoryClickListener categoryClickListener;

    public NavbarAdapter(ArrayList<String> navItems, Context context) {
        this.navItems = navItems;
        this.context = context;
    }

    @NonNull
    @Override
    public NavbarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.nav_items_layout, parent, false);
        return new NavbarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NavbarViewHolder holder, int position) {
        String item = navItems.get(position);
        holder.navItem.setText(item);

        // Set default or selected state
        if (selectedPosition == position) {
            holder.navItem.setTextColor(context.getResources().getColor(R.color.themeColor2));
            holder.navUnderline.setVisibility(View.VISIBLE);
        } else {
            holder.navItem.setTextColor(context.getResources().getColor(R.color.navColor));
            holder.navUnderline.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            int previousSelectedPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousSelectedPosition);
            notifyItemChanged(selectedPosition);



            if (categoryClickListener != null) {
                categoryClickListener.onCategoryClicked(holder.navItem.getText().toString().toLowerCase());
            }
        });
    }

    @Override
    public int getItemCount() {
        return navItems.size();
    }

    public static class NavbarViewHolder extends RecyclerView.ViewHolder {

        TextView navItem;
        View navUnderline;

        public NavbarViewHolder(@NonNull View itemView) {
            super(itemView);
            navItem = itemView.findViewById(R.id.navItem);
            navUnderline = itemView.findViewById(R.id.navUnderline);
        }

//        public void bind(String item, OnItemClickListener listener) {
//            navItem.setText(item);
//            itemView.setOnClickListener(v -> {
//                if (listener != null) {
//                    listener.onItemClicked(getAdapterPosition());
//                }
//            });
//        }
    }

    public interface OnCategoryClickListener {
        void onCategoryClicked(String category);
    }

    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.categoryClickListener = listener;
    }
}
