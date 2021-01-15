package com.anchalajay.dinder.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anchalajay.dinder.R;
import com.anchalajay.dinder.adapters.MyPetListAdapter;
import com.anchalajay.dinder.pojos.PojoMyDog;
import com.anchalajay.dinder.utility.JsonParser;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements MyPetListAdapter.MySavedPetOnClickListener {

    ImageView ivRandomDogImage;
    TextView tvRandomDogName;
    ImageView ivLike, ivDislike;
    ProgressBar progressBar;
    RecyclerView rvMyPetList;
    MyPetListAdapter myPetListAdapter;

    FirebaseDatabase mDb;
    DatabaseReference mDbReference;

    ArrayList<PojoMyDog> myDogsList;

    ArrayList<PojoMyDog> randomDogsList;

    int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvMyPetList = findViewById(R.id.rvMyPetList);
        ivRandomDogImage = findViewById(R.id.iv_dog_image);
        tvRandomDogName = findViewById(R.id.tv_dog_name);
        ivLike = findViewById(R.id.iv_like);
        ivDislike = findViewById(R.id.iv_dislike);
        progressBar = findViewById(R.id.progress_bar);

        LinearLayoutManager lm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvMyPetList.setLayoutManager(lm);

        myPetListAdapter = new MyPetListAdapter(this, null, this);
        rvMyPetList.setAdapter(myPetListAdapter);

        mDb = FirebaseDatabase.getInstance();
        mDbReference = mDb.getReference();

        makeApiCall();
        fetchMyPetList();
        setOnClickListenersToButtons();
    }

    private void fetchMyPetList(){
        @SuppressLint("HardwareIds")
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        mDbReference.child("my_pet_list").child(deviceId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    myDogsList = new ArrayList<>();
                    for(DataSnapshot myPetSnapshot : dataSnapshot.getChildren()){
                        PojoMyDog myDog = myPetSnapshot.getValue(PojoMyDog.class);
                        myDogsList.add(myDog);
                    }
                    myPetListAdapter.addMyDogsList(myDogsList);
                }
                else{
                    //no pet list handle here
                    myPetListAdapter.addMyDogsList(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(
                        MainActivity.this,
                        "Error while fetching your pets list.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void makeApiCall(){
        String url = "https://dog.ceo/api/breeds/image/random/10";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request
                .Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(() -> Toast.makeText(
                        MainActivity.this,
                        "Error while fetching data.",
                        Toast.LENGTH_SHORT
                ).show());
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String rawJson = response.body().string();
                if(!TextUtils.isEmpty(rawJson)){
                    runOnUiThread(()->{
                        randomDogsList = JsonParser.parseRawJsonData(rawJson);

                        currentIndex = 0;
                        populateRandomDogData();
                    });
                }
                else{
                    runOnUiThread(()-> Toast.makeText(
                            MainActivity.this,
                            "Invalid response encountered.",
                            Toast.LENGTH_SHORT
                    ).show());
                }
            }
        });
    }

    private void setOnClickListenersToButtons(){
        ivLike.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            if(currentIndex == 10){
                makeApiCall();
            }
            else {
                addLikedPetToDataBase();
                currentIndex += 1;
                if(currentIndex == 10){
                    makeApiCall();
                }
                else {
                    populateRandomDogData();
                }
            }
        });

        ivDislike.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            currentIndex += 1;
            if(currentIndex == 10){
                makeApiCall();
            }
            else {
                populateRandomDogData();
            }
        });
    }

    private void populateRandomDogData(){
        Glide.with(MainActivity.this)
                .load(randomDogsList.get(currentIndex).getImage_url())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(ivRandomDogImage);
        tvRandomDogName.setText(randomDogsList.get(currentIndex).getBreed_name());
    }

    private void addLikedPetToDataBase(){
        @SuppressLint("HardwareIds")
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        PojoMyDog dogToBeSaved = randomDogsList.get(currentIndex);
        dogToBeSaved.setDate_my_petted(System.currentTimeMillis());
        mDbReference.child("my_pet_list").child(deviceId).child(String.valueOf(dogToBeSaved.getDate_my_petted())).setValue(dogToBeSaved);
    }

    @Override
    public void onClick(long time_stamp) {
        @SuppressLint("HardwareIds")
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        mDbReference.child("my_pet_list").child(deviceId).child(String.valueOf(time_stamp)).setValue(null);
        myPetListAdapter.notifyDataSetChanged();
    }
}