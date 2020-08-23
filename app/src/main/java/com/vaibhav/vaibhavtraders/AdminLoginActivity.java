package com.vaibhav.vaibhavtraders;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

public class AdminLoginActivity extends AppCompatActivity {

    private EditText Email;
    private EditText Password;
    private TextView Info;
    private Button Login;
    private int counter = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        Email = findViewById(R.id.etAdminEmail);
        Password = findViewById(R.id.etAdminPassword);
        Info = findViewById(R.id.tvAdminInfo);
        Login = findViewById(R.id.btnAdminLogin);

        Info.setText("No of attempts remaining : 5");

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate(Email.getText().toString(), Password.getText().toString());
            }
        });
    }

    private void validate(String userEmail, String userPassword){
        if((userEmail.equals("vaibhavtradersgupta@gmail.com"))&&(userPassword.equals("rngupta2162"))){
            startActivity(new Intent(AdminLoginActivity.this, OrderViewActivity.class));
        }else{
            counter--;
            //Toast.makeText(AdminLoginActivity.this, "Email/Phone No or Password is wrong.", Toast.LENGTH_SHORT).show();
            Toasty.warning(AdminLoginActivity.this, "Email/Phone No or Password is wrong.", Toasty.LENGTH_SHORT).show();
            Info.setText("No of attempts remaining : " + counter);
            if(counter == 0){
                //Toast.makeText(AdminLoginActivity.this, "Admin login button disabled for security", Toast.LENGTH_LONG).show();
                Toasty.error(AdminLoginActivity.this, "Admin login button disabled for security", Toasty.LENGTH_LONG).show();
                Login.setEnabled(false);
            }
        }
    }
}