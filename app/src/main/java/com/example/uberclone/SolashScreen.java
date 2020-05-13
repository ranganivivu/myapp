package com.example.uberclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SolashScreen extends AppCompatActivity {
    ImageView car,animlogo;
    Animation animFadein,logo,text;
    TextView t2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solash_screen);
        car=findViewById(R.id.car);
        animlogo=findViewById(R.id.logo);
        t2=findViewById(R.id.t2);

        animFadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.caranim);
        logo = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.logo);
        text = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.textanim);


        car.setAnimation(animFadein);
        animlogo.setAnimation(logo);
        t2.setAnimation(text);

        splashtimeout();
    }
    public void splashtimeout() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(SolashScreen.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 8000);
    }
}
