package com.quiz.mockchallenge;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ScoreActivity extends AppCompatActivity {

    private TextView score, total;
    private Button doneBtn;
    int scoreIntent, totalIntent;
    private TextView wonText;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        loadAds();

        score= findViewById(R.id.score);
        total= findViewById(R.id.total);
        doneBtn= findViewById(R.id.done_btn);
        wonText= findViewById(R.id.won_txt);

        scoreIntent =getIntent().getIntExtra("score", 0);
        totalIntent = getIntent().getIntExtra("total", 0);

        total.setText(String.valueOf("OUT OF "+totalIntent));

        if (scoreIntent == totalIntent){
            score.setText(String.valueOf(scoreIntent));
            myRef.child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("usercoins")
                    .setValue(String.valueOf(Integer.parseInt(HomeActivity.userCoins.getText().toString())+10));
            wonText.setText("You Won 10 coins");
        }

        else if (Integer.parseInt(HomeActivity.userLife.getText().toString()) > 0) {
            if (scoreIntent + 1 == totalIntent) {
                Toast.makeText(this, "Your life saved you from getting eliminated", Toast.LENGTH_SHORT).show();
                score.setText(String.valueOf(scoreIntent+1));
                myRef.child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("usercoins")
                        .setValue(String.valueOf(Integer.parseInt(HomeActivity.userCoins.getText().toString()) + 10));
                myRef.child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("userlife")
                        .setValue(String.valueOf(Integer.parseInt(HomeActivity.userLife.getText().toString()) - 1));
                wonText.setText("You Won 10 coins");

            }else {
                score.setText(String.valueOf(scoreIntent));
                wonText.setText("You Lost");
            }
        }
        else{
            score.setText(String.valueOf(scoreIntent));
            wonText.setText("You Lost");

        }


        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void loadAds()
    {

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
}
