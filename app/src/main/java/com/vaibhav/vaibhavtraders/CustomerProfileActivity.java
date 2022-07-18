package com.vaibhav.vaibhavtraders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import es.dmoral.toasty.Toasty;

public class CustomerProfileActivity extends AppCompatActivity {

    private TextView storeName;
    private TextView customerName;
    private TextView customerNumber;
    private EditText profileToken;
    private Button editProfile;
    private Button refreshProfile;
    FirebaseAuth firebaseAuth;

    private static final String TAG = "CustomerProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        storeName = findViewById(R.id.tvProfileStoreName);
        customerName = findViewById(R.id.tvProfileCustomerName);
        customerNumber = findViewById(R.id.tvProfileCustomerNumber);
        editProfile = findViewById(R.id.btnEditProfile);
        refreshProfile = findViewById(R.id.btnProfileRefresh);
        profileToken = findViewById(R.id.etToken2);

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
                        //Toast.makeText(CustomerProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
                        profileToken.setText(msg);
                    }
                });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserProfile userProfile = snapshot.getValue(UserProfile.class);
                storeName.setText("" + userProfile.getUserStoreName());
                customerName.setText("Retailer : " + userProfile.getUserName());
                customerNumber.setText("Mobile : " + userProfile.getUserNumber());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Toast.makeText(CustomerProfileActivity.this, "Cannot get user information. Try again later.", Toast.LENGTH_LONG).show();
                Toasty.warning(CustomerProfileActivity.this, "Cannot get user information. Try again later.", Toasty.LENGTH_LONG).show();
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerProfileActivity.this, UpdateActivity.class));
            }
        });

        refreshProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
