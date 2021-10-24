package com.quiz.mockchallenge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editName, editPhone;
    private TextView editEmail;
    private Button saveBtn;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    private RewardedVideoAd rewardedVideoAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        loadAds();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Profile");

        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);

        loadRewardedAds1();


        editEmail = findViewById(R.id.input_email);
        editName = findViewById(R.id.input_name);
        editPhone = findViewById(R.id.input_phone);
        saveBtn = findViewById(R.id.btn_save);

        editEmail.setText(HomeActivity.userEmail.getText().toString());
        editName.setText(HomeActivity.userName.getText().toString());
        editPhone.setText(HomeActivity.userMobile.getText().toString());



        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRewardedAd1(v);
                loadRewardedAds1();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void loadAds()
    {

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void loadRewardedAds1() {
        rewardedVideoAd.loadAd("ca-app-pub-7700686769056733/8881908012", new AdRequest.Builder().build());
        rewardedVideoAd.setRewardedVideoAdListener(rewardedVideoAdListener1);
    }

    public void showRewardedAd1(View view) {
        if (rewardedVideoAd != null && rewardedVideoAd.isLoaded()) {
            rewardedVideoAd.show();
        }
    }

    RewardedVideoAdListener rewardedVideoAdListener1 = new RewardedVideoAdListener() {
        @Override
        public void onRewardedVideoAdLoaded() {
        }

        @Override
        public void onRewardedVideoAdOpened() {
        }

        @Override
        public void onRewardedVideoStarted() {
        }

        @Override
        public void onRewardedVideoAdClosed() {
        }

        @Override
        public void onRewarded(RewardItem rewardItem) {
            myRef.child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("username").setValue(editName.getText().toString());
            myRef.child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("usermobile").setValue(editPhone.getText().toString());
            Toast.makeText(EditProfileActivity.this, "Changes Applied", Toast.LENGTH_SHORT).show();
            finish();
        }

        @Override
        public void onRewardedVideoAdLeftApplication() {
        }

        @Override
        public void onRewardedVideoAdFailedToLoad(int i) {
        }

        @Override
        public void onRewardedVideoCompleted() {

        }
    };


}
