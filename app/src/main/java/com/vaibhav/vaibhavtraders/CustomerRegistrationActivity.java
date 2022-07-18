package com.vaibhav.vaibhavtraders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;

public class CustomerRegistrationActivity extends AppCompatActivity {

    private EditText regContact;
    private EditText regOtp;
    private Button otpButton;
    private Button registerButton;
    private TextView already;
    private String verificationId;
    FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_registration);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        regContact = findViewById(R.id.etRegContactNo);
        regOtp = findViewById(R.id.etRegOtp);
        otpButton = findViewById(R.id.btnRegOtp);
        registerButton = findViewById(R.id.btnRegister);
        already = findViewById(R.id.tvAlready);
        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

        already.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerRegistrationActivity.this, CustomerLoginActivity.class));
                finish();
            }
        });

        otpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getting the customerNumber from the EditText field, setting it to mobileNo and converting it to string and trimming for any spaces
                final String mobileNo = regContact.getText().toString().trim();
                //necessary if else statements..
                if(mobileNo.isEmpty()){
                    regContact.setError("Enter a valid mobile number");
                    regContact.requestFocus();
                }else if(mobileNo.length() < 10) {
                    regContact.setError("Enter a valid mobile number");
                    regContact.requestFocus();
                }

                //sending the verification code to the number
                progressDialog.setMessage("Requesting OTP from server Please Wait");
                progressDialog.show();
                sendVerificationCode(mobileNo);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = regOtp.getText().toString().trim();
                if (code.isEmpty() || code.length() < 6) {
                    regOtp.setError("Enter valid code");
                    regOtp.requestFocus();
                    return;
                }

                //verifying the code entered manually
                verifyVerificationCode(code);
            }
        });
    }
    //the method is sending verification code
    //the country id is concatenated
    //you can take the country id as user input as well
    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mobile,                  //phoneNo that is given by user
                60,                              //Timeout Duration
                TimeUnit.SECONDS,                   //Unit of Timeout
                TaskExecutors.MAIN_THREAD,          //Work done on main Thread
                mCallbacks);                        //OnVerificationStateChangedCallbacks
    }

    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                    //Getting the code sent by SMS
                    String code = phoneAuthCredential.getSmsCode();

                    //sometime the code is not detected automatically
                    //in this case the code will be null
                    //so user has to manually enter the code
                    if (code != null) {
                        regOtp.setText(code);
                        //verifying the code
                        verifyVerificationCode(code);
                    }
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    progressDialog.dismiss();
                    //Toast.makeText(CustomerRegistrationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    Toasty.error(CustomerRegistrationActivity.this, e.getMessage(), Toasty.LENGTH_LONG).show();
                    Log.e("TAG", Objects.requireNonNull(e.getMessage()));
                }

                //when the code is generated then this method will receive the code.
                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//                super.onCodeSent(s, forceResendingToken);

                    //storing the verification id that is sent to the user
                    verificationId = s;
                }
            };

    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
        progressDialog.dismiss();
    }
    //used for signing the user
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(CustomerRegistrationActivity.this,
                        new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    //verification successful we will start the profile activity
                                    progressDialog.dismiss();
                                    //Toast.makeText(CustomerRegistrationActivity.this, "Successfully Registered. Enter details here to complete registration", Toast.LENGTH_LONG).show();
                                    Toasty.success(CustomerRegistrationActivity.this, "Successfully Registered. Enter details here to complete registration", Toasty.LENGTH_LONG).show();
                                    startActivity(new Intent(CustomerRegistrationActivity.this, NextRegistrationActivity.class));
                                    finish();
                                } else {
                                    //verification unsuccessful.. display an error message
                                    progressDialog.dismiss();
                                    String message = "Something is wrong, we will fix it soon...";

                                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                        progressDialog.dismiss();
                                        message = "Invalid code entered...";
                                    }
                                    Toast.makeText(CustomerRegistrationActivity.this,message,Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
    }
}