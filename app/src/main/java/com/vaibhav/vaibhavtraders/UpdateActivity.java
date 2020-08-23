package com.vaibhav.vaibhavtraders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.w3c.dom.Text;

import es.dmoral.toasty.Toasty;

public class UpdateActivity extends AppCompatActivity {

    private EditText newStoreName;
    private EditText newCustomerName;
    private EditText newCustomerNumber;
    private TextView theFcm;
    private Button save;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    private static final String TAG = "UpdateActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        newStoreName = findViewById(R.id.etStoreNameUpdate);
        newCustomerName = findViewById(R.id.etCustomerNameUpdate);
        newCustomerNumber = findViewById(R.id.etCustomerNumberUpdate);
        theFcm = findViewById(R.id.tvFcm);
        save = findViewById(R.id.btnSave);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

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
                        //Toast.makeText(UpdateActivity.this, "Token generated", Toast.LENGTH_SHORT).show();
                        theFcm.setText(msg);
                    }
                });

        final DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserProfile userProfile = snapshot.getValue(UserProfile.class);
                newStoreName.setText(userProfile.getUserStoreName());
                newCustomerName.setText(userProfile.getUserName());
                newCustomerNumber.setText(userProfile.getUserNumber());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Toast.makeText(UpdateActivity.this, "Cannot get user information", Toast.LENGTH_SHORT).show();
                Toasty.error(UpdateActivity.this, "Cannot get user information", Toasty.LENGTH_SHORT).show();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = newCustomerName.getText().toString();
                String storename = newStoreName.getText().toString();
                String number = newCustomerNumber.getText().toString();
                String fcm = theFcm.getText().toString();

                UserProfile userProfile = new UserProfile(name, number, storename, fcm);

                databaseReference.setValue(userProfile);
                //Toast.makeText(UpdateActivity.this, "Profile Updated.", Toast.LENGTH_LONG).show();
                Toasty.success(UpdateActivity.this, "Profile Updated.", Toasty.LENGTH_LONG).show();
                finish();
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