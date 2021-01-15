package com.anchalajay.dinder.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anchalajay.dinder.R;

public class MyPetListAdapterViewHolder extends RecyclerView.ViewHolder {
    ImageView ivDogImage,ivDelete;
    TextView tvDogName, tvPettedDate;
    ProgressBar progressBar;
    public MyPetListAdapterViewHolder(@NonNull View itemView) {
        super(itemView);

        ivDogImage = itemView.findViewById(R.id.iv_dog_image);
        tvDogName = itemView.findViewById(R.id.tv_dog_name);
        tvPettedDate = itemView.findViewById(R.id.tv_petted_on);
        ivDelete = itemView.findViewById(R.id.iv_delete);
        progressBar = itemView.findViewById(R.id.progressbar);
    }
}
