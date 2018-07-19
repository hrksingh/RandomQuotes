package com.example.ashok.randomquotes;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TextView txtquote, txtauthor;
    Button btnnext;
    CardView c1;
    ArrayList<Suitcase> arrquote = new ArrayList<>();
    int count = 0, countclose = 0;
    Dialog dialog, loading;
    CountDownTimer timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtquote = findViewById(R.id.txtquote);
        txtauthor = findViewById(R.id.txtauthor);
        btnnext = findViewById(R.id.btnnext);
        c1 = findViewById(R.id.c1);


        dailogNoNetwork();


    }

    public void quoteLoader() {
        c1.setVisibility(View.VISIBLE);


        txtquote.setText("\"" + arrquote.get(count).quote + "\"");
        txtauthor.setText("~" + arrquote.get(count).author);
        btnnext.setVisibility(View.VISIBLE);
        count++;
    }


    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    public void getsetData() {

        loading = new Dialog(MainActivity.this);
        loading.setContentView(R.layout.customloading);
        loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading.setCancelable(false);
        LottieAnimationView lottieNoNetwork = loading.findViewById(R.id.lottieloading);
        lottieNoNetwork.setAnimation(R.raw.ripple);
        lottieNoNetwork.playAnimation();
        loading.show();


        AndroidNetworking.initialize(getApplicationContext());
        AndroidNetworking.get("https://andruxnet-random-famous-quotes.p.mashape.com/?cat=famous&count=10")
                .addHeaders("X-Mashape-Key", "220o2QaK5Ymsh1fFYCO8O2QHSVbXp1LtVDNjsnSTsXOpIsKTMo")
                .addHeaders("Accept", "application/json")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        loading.dismiss();

                        for (int i = 0; i < response.length(); i++) {

                            try {
                                JSONObject object = response.getJSONObject(i);
                                Suitcase suitcase = new Suitcase();
                                suitcase.quote = object.getString("quote");
                                suitcase.author = object.getString("author");
                                arrquote.add(suitcase);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }

                        quoteLoader();

                        btnnext.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (count < 9) {
                                    quoteLoader();
                                } else if (count == 9) {

                                    quoteLoader();

                                    StateListDrawable drawable = (StateListDrawable) btnnext.getBackground();
                                    DrawableContainer.DrawableContainerState dcs = (DrawableContainer.DrawableContainerState) drawable.getConstantState();
                                    Drawable[] drawableItems = dcs.getChildren();
                                    GradientDrawable gradientDrawable = (GradientDrawable) drawableItems[0];
                                    gradientDrawable.setStroke(6, getResources().getColor(R.color.red));
                                    /*gradientDrawable.setStroke((int) (1.5f*getResources().getDisplayMetrics().density + 0.5f), getResources().getColor(R.color.red));*/

                                    btnnext.setTextColor(getResources().getColor(R.color.red));
                                    btnnext.setText("EXIT");
                                    btnnext.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (countclose < 1) {
                                                Toast.makeText(MainActivity.this, "Press again If you want to exit!!", Toast.LENGTH_SHORT).show();
                                                countclose++;
                                            } else {
                                                finish();
                                            }

                                        }
                                    });


                                }

                            }
                        });


                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });


    }

    public void dailogNoNetwork() {

        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.customdailog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);


        if (isOnline()) {
            getsetData();
        } else {
            LottieAnimationView lottieNoNetwork = dialog.findViewById(R.id.lottieNoNetwork);
            lottieNoNetwork.setAnimation(R.raw.network_lost);
            lottieNoNetwork.playAnimation();
            dialog.show();

            timer = new CountDownTimer(Long.MAX_VALUE, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    if (isOnline()) {
                        dialog.dismiss();
                        getsetData();
                        timer.cancel();
                    }
                }

                @Override
                public void onFinish() {

                }
            };

            timer.start();
        }


    }


}
