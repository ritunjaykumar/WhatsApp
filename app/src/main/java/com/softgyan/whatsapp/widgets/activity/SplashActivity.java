package com.softgyan.whatsapp.widgets.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.softgyan.whatsapp.R;
import com.softgyan.whatsapp.utils.UserDetails;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(() -> {
            Intent intent;
            if(FirebaseAuth.getInstance().getCurrentUser() == null){
                intent = new Intent(SplashActivity.this, WelcomeActivity.class);
            }else{
                if(!UserDetails.getInstance(SplashActivity.this).isUserNameSet()){
                    intent = new Intent(SplashActivity.this, SetUserInfoActivity.class);
                }else {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                }
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }, 1000);
    }

}