package com.vaibhav.vaibhavtraders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import es.dmoral.toasty.Toasty;

public class NextRegistrationActivity extends AppCompatActivity {

    private EditText regCustomerName;
    private EditText regStoreName;
    private EditText regCustomerNumber;
    private TextView regTokenDisplay;
    private Button regButton;
    String name, storename, number, fcm;
    private FirebaseAuth firebaseAuth;

    private static final String TAG = "NextRegistrationActivit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_registration);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        regCustomerName = findViewById(R.id.etRegCustomerName);
        regStoreName = findViewById(R.id.etRegStoreName);
        regCustomerNumber = findViewById(R.id.etRegContactNo);
        regButton = findViewById(R.id.btnFinalRegister);
        regTokenDisplay = findViewById(R.id.tvToken1);

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseInstanceId.getInstance().getInstanceId()
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
                        //Toast.makeText(NextRegistrationActivity.this, "Token generated", Toast.LENGTH_SHORT).show();
                        Toasty.info(NextRegistrationActivity.this, "Token generated", Toasty.LENGTH_SHORT).show();
                        regTokenDisplay.setText(msg);
                    }
                });

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validate()) {

                    final String mobileNo = regCustomerNumber.getText().toString().trim();

                    if (mobileNo.isEmpty()) {
                        regCustomerNumber.setError("Enter a valid mobile number");
                        regCustomerNumber.requestFocus();
                    } else if (mobileNo.length() < 10) {
                        regCustomerNumber.setError("Enter a valid mobile number");
                        regCustomerNumber.requestFocus();
                    }
                    sendUserData();
                    //Toast.makeText(NextRegistrationActivity.this, "Successfully Registered. Start ordering.", Toast.LENGTH_LONG).show();
                    Toasty.success(NextRegistrationActivity.this, "Successfully Registered. Start ordering.", Toasty.LENGTH_LONG).show();
                    startActivity(new Intent(NextRegistrationActivity.this, OrderActivity.class));
                    finish();
                }
            }
        });
    }
    private Boolean validate() {
        Boolean result = false;

        number = regCustomerNumber.getText().toString();
        storename = regStoreName.getText().toString();
        name = regCustomerName.getText().toString();
        fcm = regTokenDisplay.getText().toString();

        if (number.isEmpty() || storename.isEmpty() || name.isEmpty()) {
            //Toast.makeText(NextRegistrationActivity.this, "Please enter all the details", Toast.LENGTH_LONG).show();
            Toasty.warning(NextRegistrationActivity.this, "Please enter all the details", Toasty.LENGTH_LONG).show();
        } else {
            result = true;
        }
        return result;
    }

    private void sendUserData() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference(firebaseAuth.getUid());
        UserProfile userProfile = new UserProfile(name, number, storename, fcm);
        myRef.setValue(userProfile);
    }
}