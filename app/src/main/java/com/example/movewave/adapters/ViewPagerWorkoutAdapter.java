package com.example.movewave.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.movewave.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class ViewPagerWorkoutAdapter extends RecyclerView.Adapter<ViewPagerWorkoutAdapter.ViewPagerHolder> {
    private List<String> imageUrls;
    private List<String> imageTexts;
    private ViewPager2 viewPager2;

    public ViewPagerWorkoutAdapter(List<String> imageUrls, List<String> imageTexts, ViewPager2 viewPager2) {
        this.imageUrls = imageUrls;
        this.imageTexts = imageTexts;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public ViewPagerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_pager_item_workout_type,
                parent, false);
        return new ViewPagerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPagerHolder holder, int position) {
        String imageUrl = imageUrls.get(position);
        String imageText = imageTexts.get(position);

        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .into(holder.roundedImageView);

        holder.tvImageSlider.setText(imageText);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public class ViewPagerHolder extends RecyclerView.ViewHolder {
        RoundedImageView roundedImageView;
        TextView tvImageSlider;
        public ViewPagerHolder(@NonNull View itemView) {
            super(itemView);
            roundedImageView = itemView.findViewById(R.id.sliderImageView);
            tvImageSlider = itemView.findViewById(R.id.sliderImageText);
        }
    }
}
