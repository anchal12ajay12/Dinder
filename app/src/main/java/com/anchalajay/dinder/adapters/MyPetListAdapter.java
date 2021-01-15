package com.anchalajay.dinder.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.anchalajay.dinder.R;
import com.anchalajay.dinder.pojos.PojoMyDog;
import com.anchalajay.dinder.utility.DateTimeUtility;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.Collections;

public class MyPetListAdapter extends RecyclerView.Adapter<MyPetListAdapterViewHolder> {

    Context mContext;
    ArrayList<PojoMyDog> myDogsList;

    private final MySavedPetOnClickListener onClickListener;

    public interface MySavedPetOnClickListener{
        void onClick(long time_stamp);
    }

    public MyPetListAdapter(Context context, ArrayList<PojoMyDog> myDogsList, MySavedPetOnClickListener onClickListener){
        this.mContext = context;
        this.myDogsList = myDogsList;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public MyPetListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater li = (LayoutInflater) parent.
                getContext().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = li.inflate(R.layout.card_dog_added, parent, false);
        return new MyPetListAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyPetListAdapterViewHolder holder, int position) {
        PojoMyDog myDog = myDogsList.get(position);
        holder.progressBar.setVisibility(View.VISIBLE);
        holder.tvDogName.setText(myDog.getBreed_name());
        holder.tvPettedDate.setText(DateTimeUtility.getFormattedDate(myDog.getDate_my_petted()));
        Glide.with(mContext).
                load(myDog.getImage_url())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                }).
                into(holder.ivDogImage);
        holder.ivDelete.setOnClickListener(v ->
                onClickListener.onClick(
                        myDogsList.get(position).getDate_my_petted()
                ));
    }

    @Override
    public int getItemCount() {
        if(myDogsList == null) return 0;
        return myDogsList.size();
    }

    public void addMyDogsList(ArrayList<PojoMyDog> myDogsList){
        this.myDogsList = myDogsList;
        if(myDogsList!=null) {
            Collections.sort(myDogsList, (d1, d2) -> {
                if (d1.getDate_my_petted() < d2.getDate_my_petted()) return 1;
                return -1;
            });
        }
        notifyDataSetChanged();
    }
}
