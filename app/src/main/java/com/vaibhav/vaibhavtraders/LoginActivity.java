package com.vaibhav.vaibhavtraders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.JsonParser;

public class LoginActivity extends AppCompatActivity {

    private Button adminLogin;
    private Button customerLogin;
    private TextView aboutApp;
    private TextView contactUs;
    //private EditText tokenwa;

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        updateCheck();

        adminLogin = findViewById(R.id.btnAdminLoginStart);
        customerLogin = findViewById(R.id.btnCustomerLoginStart);
        aboutApp = findViewById(R.id.tvAbout);
        contactUs = findViewById(R.id.tvContact);
        //tokenwa = findViewById(R.id.etToken);

        /*FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);
                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                        tokenwa.setText(msg);
                    }
                });*/

        adminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DO NOT FORGET TO CHANGE THIS !

                startActivity(new Intent(LoginActivity.this, AdminLoginActivity.class));
//                startActivity(new Intent(LoginActivity.this, OrderViewActivity.class));
            }
        });

        customerLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, CustomerLoginActivity.class));
            }
        });

        aboutApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, AboutActivity.class));
            }
        });

        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ContactActivity.class));
            }
        });
    }
    private void updateCheck(){
        new AppUpdater(this)
                .setUpdateFrom(UpdateFrom.JSON)
                .setUpdateJSON("https://raw.githubusercontent.com/vabbydante/vaibhavtraders/master/abc.json")
                .setDisplay(Display.DIALOG)
                .setContentOnUpdateAvailable("A new update is available to download. By downloading the latest update you will get the latest features, improvements and bug fixes. \nClick on the 'UPDATE' button below to start downloading the updated app.")
                .setCancelable(false)
                .setButtonDoNotShowAgain(null)
                .setButtonDismiss(null)
                //.showAppUpdated(true)
                .start();
    }
}