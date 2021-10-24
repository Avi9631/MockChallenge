package com.quiz.mockchallenge;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {

    private Button startQuiz, getCoins, getSilver;
    public static TextView userName, userEmail, userMobile, userLife, userWallet, userCoins, userPlayed, userSilver;
    private static String name, email,mobile,life, wallet, coins, played, silver;
    private LinearLayout coin_lin, wallet_lin, silver_lin;

    Dialog dialog;
    private TextView title, amount, amounttext;
    private Button transferBtn;

    Dialog dialog1;
    private TextView title1, amount1, amounttext1;
    private Button withdrawl;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    private RewardedVideoAd mRewardedVideoAd;
    private RewardedVideoAd rewardedVideoAd;

    private Dialog loadingDialog;
    private ImageView editBtn;

    private ImageView quizImage;

    private Dialog infodialog;
    private TextView textdialog, titledialog;
    private ImageView closedialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        MobileAds.initialize(this);

        loadAds();
        //RewardedVideoAd
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);

        loadRewardedAds();

        loadRewardedAds1();


        if (getIntent().getIntExtra("dialog", -1) == 1) {
            //////////////////////////////pymentMethod dialog
            infodialog= new Dialog(HomeActivity.this);
            infodialog.setContentView(R.layout.dialog_setting);
            infodialog.setCancelable(true);
            infodialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            titledialog= infodialog.findViewById(R.id.dia_title);
            textdialog= infodialog.findViewById(R.id.txt);
            closedialog= infodialog.findViewById(R.id.close);
            titledialog.setText("How to play");
            textdialog.setText("The game is very simple and easy to play" + "\n" +
                    "Some Conversions:" + "\n" +
                    "1 coin = Rs. 0.40" + "\n" +
                    "Minimum of 300 coins are required to transfer it into wallet" + "\n" +
                    "You will get your money in your paytm account with registered mobile number." + "\n" + "\n" + "\n" +
                    "Play the quiz game to earn money." + "\n" +
                    "1. You need 5 silver to play the game." + "\n" +
                    "2. You can watch ads to gain 5 silver" + "\n" +
                    "3. You will get 1 life for 2 silver coin" + "\n" +
                    "4. If all the question are answered correctly 10 coins are rewarded");
            closedialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    infodialog.dismiss();
                }
            });
            infodialog.show();
        }


        /////////////////////////////////////////////////////////paymentmethod Dialog



        loadingDialog= new Dialog(HomeActivity.this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_corners));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);



        startQuiz= findViewById(R.id.startbtn);
        getCoins= findViewById(R.id.get_coins);
        getSilver = findViewById(R.id.get_silver);

        startQuiz.setEnabled(true);

        coin_lin= findViewById(R.id.lin_coins);
        wallet_lin= findViewById(R.id.lin_wallet);
        silver_lin = findViewById(R.id.lin_silver);

        userName= findViewById(R.id.user_name);
        userEmail= findViewById(R.id.user_email);
        userMobile= findViewById(R.id.user_mobile);
        userLife= findViewById(R.id.user_life);
        userWallet= findViewById(R.id.user_wallet);
        userCoins= findViewById(R.id.user_coins);
        userSilver = findViewById(R.id.user_silver);
        userPlayed = findViewById(R.id.user_played);
        editBtn= findViewById(R.id.setting);
        quizImage = findViewById(R.id.quiz_image);


        dialog= new Dialog(HomeActivity.this);
        dialog.setContentView(R.layout.dialog);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_borders_white));
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        title= dialog.findViewById(R.id.dialog_title);
        amount= dialog.findViewById(R.id.dialog_coins);
        amounttext= dialog.findViewById(R.id.dialog_coin_title);
        transferBtn= dialog.findViewById(R.id.coin_money);

        dialog1= new Dialog(HomeActivity.this);
        dialog1.setContentView(R.layout.dialog1);
        dialog1.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_borders_white));
        dialog1.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog1.setCancelable(true);
        title1= dialog1.findViewById(R.id.dialog_title1);
        amount1= dialog1.findViewById(R.id.dialog_coins1);
        amounttext1= dialog1.findViewById(R.id.dialog_coin_title1);
        withdrawl= dialog1.findViewById(R.id.withdraw);

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editintent = new Intent(HomeActivity.this, SettingActivity.class);
                startActivity(editintent);
            }
        });

        loadUserData();

        myRef.child("Quiz").child("1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Glide.with(HomeActivity.this).load(dataSnapshot.child("quizimage").getValue().toString()).into(quizImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        transferBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(userCoins.getText().toString()) >= 300){
                    transferBtn.setEnabled(false);
                    double val = Double.parseDouble(userCoins.getText().toString()) * 0.1;
                    myRef.child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("usercoins")
                            .setValue(String.valueOf((int) val));
                    myRef.child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("userwallet")
                            .setValue(String.valueOf(Double.parseDouble(userWallet.getText().toString())+(Integer.parseInt(userCoins.getText().toString())-val) * 0.40));
                    Toast.makeText(HomeActivity.this, "Amount transfered to wallet", Toast.LENGTH_SHORT).show();
                    loadUserData();
                }
                else {
                    Toast.makeText(HomeActivity.this, "Minimum transfer required is 300 coins", Toast.LENGTH_SHORT).show();
                }
            }
        });

        getSilver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRewardedAd1(v);
                loadRewardedAds1();
            }
        });

        getCoins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRewardedAd(v);
                loadRewardedAds();
            }
        });

        withdrawl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                withdrawl.setEnabled(false);
                if (Double.parseDouble(userWallet.getText().toString()) > 0) {
                    myRef.child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("userwallet")
                            .setValue(String.valueOf(0));
                    myRef.child("Withdrawal").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("prize")
                            .setValue(userWallet.getText().toString());
                    loadUserData();
                    dialog1.dismiss();
                    Toast.makeText(HomeActivity.this, "You will receive your amount in paytm account in 24 hrs \n Paytm account should be with registered mobile number.", Toast.LENGTH_LONG).show();

                }else{
                    Toast.makeText(HomeActivity.this, "Insufficient balance.", Toast.LENGTH_SHORT).show();
                    dialog1.dismiss();
                }

            }
        });

        coin_lin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setText("Coins");
                amount.setText(userCoins.getText().toString());
                amounttext.setText("You have "+userCoins.getText().toString()+" coins in your account");
                dialog.show();
            }
        });

        wallet_lin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title1.setText("Wallet");
                amount1.setText("Rs."+userWallet.getText().toString()+"/-");
                amounttext1.setText("You have Rs."+userWallet.getText().toString()+"/- in your wallet.");
                dialog1.show();
            }
        });

        startQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userPlayed.getText().toString().equals("falsed")) {
                    if (Integer.parseInt(userSilver.getText().toString()) >= 5) {
                        startQuiz.setEnabled(false);
                        Intent homeIntent = new Intent(HomeActivity.this, QuestionsActivity.class);
                        myRef.child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("usersilver")
                                .setValue(String.valueOf(Integer.parseInt(userSilver.getText().toString()) - 5));
//                        loadUserData();
                        myRef.child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("userplayed").setValue("trued");
                        startActivity(homeIntent);
                    }
                    else {
                        Toast.makeText(HomeActivity.this, "You do not have enough silver. \n Entry cost is 5 silver.", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(HomeActivity.this, "Already played", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void loadRewardedAds1() {
        rewardedVideoAd.loadAd("ca-app-pub-7700686769056733/8881908012", new AdRequest.Builder().build());
        rewardedVideoAd.setRewardedVideoAdListener(rewardedVideoAdListener1);
    }


    private void loadUserData() {
        loadingDialog.show();
        myRef.child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = String.valueOf(dataSnapshot.child("username").getValue());
                email = String.valueOf(dataSnapshot.child("useremail").getValue());
                mobile = String.valueOf(dataSnapshot.child("usermobile").getValue());
                life = String.valueOf(dataSnapshot.child("userlife").getValue());
                wallet = String.valueOf(dataSnapshot.child("userwallet").getValue());
                coins = String.valueOf(dataSnapshot.child("usercoins").getValue());
                silver = String.valueOf(dataSnapshot.child("usersilver").getValue());
                played = String.valueOf(dataSnapshot.child("userplayed").getValue());


                setData(name,email,mobile,life,wallet,coins,silver,played);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void setData(String name, String email, String mobile, String life, String wallet, String coins,String silver, String played) {

        userCoins.setText(coins);
        userMobile.setText(mobile);
        userWallet.setText(wallet);
        userLife.setText(life);
        userEmail.setText(email);
        userName.setText(name);
        userSilver.setText(silver);
        userPlayed.setText(played);
        loadingDialog.dismiss();
    }

    private void loadRewardedAds(){
        mRewardedVideoAd.loadAd("ca-app-pub-7700686769056733/8881908012", new AdRequest.Builder().build());
        mRewardedVideoAd.setRewardedVideoAdListener(rewardedVideoAdListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadingDialog.show();
        loadAds();
        //RewardedVideoAd
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        loadRewardedAds();
        loadRewardedAds1();
        ///////
        withdrawl.setEnabled(true);
        startQuiz.setEnabled(true);
        transferBtn.setEnabled(true);
        myRef.child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        name = String.valueOf(dataSnapshot.child("username").getValue());
                        email = String.valueOf(dataSnapshot.child("useremail").getValue());
                        mobile = String.valueOf(dataSnapshot.child("usermobile").getValue());
                        life = String.valueOf(dataSnapshot.child("userlife").getValue());
                        wallet = String.valueOf(dataSnapshot.child("userwallet").getValue());
                        coins = String.valueOf(dataSnapshot.child("usercoins").getValue());
                        silver = String.valueOf(dataSnapshot.child("usersilver").getValue());
                        played = String.valueOf(dataSnapshot.child("userplayed").getValue());

                        setData(name,email,mobile,life,wallet,coins,silver, played);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

    }

    public void showRewardedAd(View view) {
        if (mRewardedVideoAd != null && mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }
    }

    RewardedVideoAdListener rewardedVideoAdListener = new RewardedVideoAdListener() {
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
            if (Integer.parseInt(userSilver.getText().toString()) >= 10) {
                myRef.child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("usersilver")
                        .setValue(String.valueOf(Integer.parseInt(userSilver.getText().toString()) - 10));
                myRef.child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("userlife")
                        .setValue(String.valueOf(Integer.parseInt(HomeActivity.userLife.getText().toString()) + 1));
                loadUserData();
            }
            else {
                Toast.makeText(HomeActivity.this, "Minimum of 10 silver coins are required for 1 life", Toast.LENGTH_SHORT).show();
            }

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
            myRef.child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("usersilver")
                    .setValue(String.valueOf(Integer.parseInt(HomeActivity.userSilver.getText().toString())+5));
            loadUserData();
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


    @Override
    public void onResume() {
        mRewardedVideoAd.resume(this);
        rewardedVideoAd.resume(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        mRewardedVideoAd.pause(this);
        rewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mRewardedVideoAd.destroy(this);
        rewardedVideoAd.destroy(this);
        super.onDestroy();
    }

    private void loadAds()
    {

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

}
