package com.tech.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.tech.R;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder> {

    private Context mContext;
    String[] imageUrls;
    int startPosition;

    public ViewPagerAdapter(Context context, String[] imgUrls, int startPosition) {
        this.mContext = context;
        this.imageUrls = imgUrls;
        this.startPosition = startPosition;
    }

    @NonNull
    @Override
    public ViewPagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewPagerViewHolder(
                LayoutInflater.from(mContext).inflate((R.layout.view_pager_item), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPagerViewHolder holder, int position) {
        Glide.with(mContext).load(imageUrls[position]).into(holder.photoView);
    }

    @Override
    public int getItemCount() {
        return imageUrls.length;
    }

    public class ViewPagerViewHolder extends RecyclerView.ViewHolder {

        PhotoView photoView;

        public ViewPagerViewHolder(@NonNull View itemView) {
            super(itemView);
            photoView = itemView.findViewById(R.id.image_view);
            photoView.enable();
        }
    }
}
