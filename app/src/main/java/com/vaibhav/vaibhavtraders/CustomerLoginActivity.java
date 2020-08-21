package com.vaibhav.vaibhavtraders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class CustomerLoginActivity extends AppCompatActivity {

    private Button customerLogin;
    private Button otpButton;
    private EditText customerNumber;
    private EditText otpFill;
    private TextView registerText;
    Button customerRegister;
    private String verificationId;
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);
        //code to hide the action bar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
        //defining the views to the elements / initialize fields..
        customerLogin = findViewById(R.id.btnCustomerLogin);
        otpButton = findViewById(R.id.btnGetOtp);
        customerNumber = findViewById(R.id.etCustomerNumber);
        otpFill = findViewById(R.id.etOtpFill);
        customerRegister = findViewById(R.id.btnCustomerRegister);
        progressDialog = new ProgressDialog(this);
        registerText = findViewById(R.id.textView8);
        //getting instances of currentUser and firebaseAuth..

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseAuth = FirebaseAuth.getInstance();

        //check if the user is logged in or not. if logged in, direct them to OrderActivity
        if(currentUser != null){
            Intent intent = new Intent(CustomerLoginActivity.this, OrderActivity.class);
            startActivity(intent);
            finish();
        }else{
            otpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //getting the customerNumber from the EditText field, setting it to mobileNo and converting it to string and trimming for any spaces
                    final String mobileNo = customerNumber.getText().toString().trim();
                    //necessary if else statements..
                    if(mobileNo.isEmpty()){
                        customerNumber.setError("Enter a valid mobile number");
                        customerNumber.requestFocus();
                    }else if(mobileNo.length() < 10) {
                        customerNumber.setError("Enter a valid mobile number");
                        customerNumber.requestFocus();
                    }

                    //sending the verification code to the number
                    progressDialog.setMessage("Requesting OTP from server Please Wait");
                    progressDialog.show();
                    sendVerificationCode(mobileNo);
                }
            });
        }
        //if the automatic sms detection did not work, user can also enter the code manually
        //so adding a click listener to the button
        customerLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = otpFill.getText().toString().trim();
                if (code.isEmpty() || code.length() < 6) {
                    otpFill.setError("Enter valid code");
                    otpFill.requestFocus();
                    return;
                }

                //verifying the code entered manually
                verifyVerificationCode(code);
            }
        });

        customerRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerLoginActivity.this, CustomerRegistrationActivity.class));
                finish();
            }
        });

        registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerLoginActivity.this, CustomerRegistrationActivity.class));
                finish();
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
                        otpFill.setText(code);
                        //verifying the code
                        verifyVerificationCode(code);
                    }
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    progressDialog.dismiss();
                    Toast.makeText(CustomerLoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
                .addOnCompleteListener(CustomerLoginActivity.this,
                        new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    //verification successful we will start the order activity
                                    progressDialog.dismiss();
                                    Toast.makeText(CustomerLoginActivity.this, "You're now logged in", Toast.LENGTH_LONG).show();
                                    finish();
                                    startActivity(new Intent(CustomerLoginActivity.this, OrderActivity.class));
                                } else {
                                    //verification unsuccessful.. display an error message
                                    progressDialog.dismiss();
                                    String message = "Something is wrong, we will fix it soon...";

                                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                        progressDialog.dismiss();
                                        message = "Invalid code entered...";
                                    }
                                    Toast.makeText(CustomerLoginActivity.this,message,Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
    }
}