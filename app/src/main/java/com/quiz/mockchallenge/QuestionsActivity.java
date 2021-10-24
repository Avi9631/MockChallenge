package com.quiz.mockchallenge;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class QuestionsActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    private Dialog loadingDialog;
    private CountDownTimer t;

    private TextView question, noIndicator;
    private TextView bookmarksBtn;
    private LinearLayout optionsContainer;
    private int count = 0;
    List<QuestionModel> list = new ArrayList<>();
    private int position = 0;
    private int score=0;

    private int counter;

    private ProgressBar progressBar;

    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        loadAds();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        question = findViewById(R.id.question);
        noIndicator = findViewById(R.id.no_indicator);
        bookmarksBtn = findViewById(R.id.floatingActionButton);
        optionsContainer = findViewById(R.id.options_container);
        progressBar= findViewById(R.id.progressBar);
        progressBar.setMax(100);
        progressBar.setProgress(counter);

        loadingDialog= new Dialog(QuestionsActivity.this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_corners));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);

        t=new CountDownTimer(11000,1000){

            @Override
            public void onTick(long millisUntilFinished) {
                bookmarksBtn.setText(String.valueOf(counter));
                counter++;
                progressBar.setProgress((int)counter*100/ (11000/1000));

            }

            @Override
            public void onFinish() {

                progressBar.setProgress(100);
                nextques();
            }
        };


        list= new ArrayList<>();

        loadingDialog.show();
        myRef.child("Quiz").child("1").child("Questions").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
//                            String id = dataSnapshot1.getKey();
                            String question = String.valueOf(dataSnapshot1.child("question").getValue());
                            String a = String.valueOf(dataSnapshot1.child("optionA").getValue());
                            String b = String.valueOf(dataSnapshot1.child("optionB").getValue());
                            String c = String.valueOf(dataSnapshot1.child("optionC").getValue());
                            String d = String.valueOf(dataSnapshot1.child("optionD").getValue());
                            String correctANS = String.valueOf(dataSnapshot1.child("correctANS").getValue());

                            list.add(new QuestionModel(question, a,b,c,d,correctANS));

                        }
                        if (list.size() > 0)
                        {

                            for (int i = 0; i < 4; i++) {
                                optionsContainer.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        checkAnswer((Button) v);
                                    }
                                });

                            }

                            playAnim(question,0, list.get(position).getQuestion());

                        }else {
                            Toast.makeText(QuestionsActivity.this, "no question ", Toast.LENGTH_SHORT).show();
                        }
                        loadingDialog.dismiss();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(QuestionsActivity.this, "database erropr ", Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    }
                });

    }



    private void playAnim(final View view, final int value, final String data) {
        t.cancel();
        counter =0;
        t.start();

        for (int i = 0; i < 4; i++) {
            optionsContainer.getChildAt(i).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0099CC")));

        }

        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500).setStartDelay(100)
                .setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (value == 0 && count < 4) {
                    String option = "";
                    if (count == 0) {
                        option = list.get(position).getA();
                    } else if (count == 1) {
                        option = list.get(position).getB();

                    } else if (count == 2) {
                        option = list.get(position).getC();

                    } else if (count == 3) {
                        option = list.get(position).getD();

                    }

                    playAnim(optionsContainer.getChildAt(count), 0, option);
                    count++;
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {


                if (value == 0) {
                    try{
                        ((TextView) view).setText(data);
                        noIndicator.setText(position+1+"/"+list.size());

                    }catch (ClassCastException ex)
                    {
                        ((Button) view).setText(data);

                    }
                    view.setTag(data);
                    playAnim(view, 1, data);
                }else {
                    enableOption(true);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }

    private void nextques(){

                position++;
                if (position == list.size())
                {
                    if (interstitialAd.isLoaded()){
                        interstitialAd.show();
                        return;
                    }

                    //score activity
                    Intent scoreIntent = new Intent(QuestionsActivity.this, ScoreActivity.class);
                    scoreIntent.putExtra("score", score);
                    scoreIntent.putExtra("total", list.size());
                    startActivity(scoreIntent);
                    finish();
                    return;
                }
                count = 0;
                t.start();
                playAnim(question, 0, list.get(position).getQuestion());
    }

    private void checkAnswer(Button selectedOption) {
            enableOption(false);

            if (selectedOption.getText().toString().equals(list.get(position).getAnswer())){
                //correct
                score++;
                selectedOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
            }else {
                //incorrect
                selectedOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#CC0000")));

                Button correctoption = (Button) optionsContainer.findViewWithTag(list.get(position).getAnswer());

                correctoption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));



            }
    }

    private void enableOption(Boolean enable) {
        for (int i = 0; i < 4; i++) {
                optionsContainer.getChildAt(i).setEnabled(enable);
                if (enable){
                    optionsContainer.getChildAt(i).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0099CC")));

                }
        }

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

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_id));
        interstitialAd.loadAd(new AdRequest.Builder().build());

        interstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Intent scoreIntent = new Intent(QuestionsActivity.this, ScoreActivity.class);
                scoreIntent.putExtra("score", score);
                scoreIntent.putExtra("total", list.size());
                startActivity(scoreIntent);
                finish();
                return;
            }
        });
    }

}
