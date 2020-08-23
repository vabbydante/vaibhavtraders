package com.vaibhav.vaibhavtraders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.Duration;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class OrderActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    String itemTag, quantityTag;
    String date_n = new
            SimpleDateFormat("dd.MM.yyyy hh:mm:ss a", Locale.getDefault()).format(new Date());
    private Spinner quantitySpinner;
    private Spinner itemSpinner;
    private TextView orderDisplay;
    private TextView displayStore;
    private TextView displayCurrentDate;
    private Button addItem;
    //private Button test;
    FirebaseAuth firebaseAuth;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference orderRef = db.collection("orderdata");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        //setting up UI Views and setting ArrayAdapters for the spinners. (using the 'string-array' resource from strings.xml)
        itemSpinner = findViewById(R.id.spinner);

        //this is a previous method in which i parsed the string from strings.xml to the itemSpinner.
        /*ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemSpinner.setAdapter(adapter);
        itemSpinner.setOnItemSelectedListener(this);*/

        firebaseAuth = FirebaseAuth.getInstance();

        DatabaseReference itemReference = FirebaseDatabase.getInstance().getReference("spinneritems");

        itemReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                final List<String> items = new ArrayList<>();

                for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                    String itemName = areaSnapshot.child("itemName").getValue(String.class);
                    items.add(itemName);
                }

                //Spinner itemSpinner = findViewById(R.id.spinner);
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(OrderActivity.this, android.R.layout.simple_spinner_item, items);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                itemSpinner.setAdapter(dataAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        quantitySpinner = findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.Quantities, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        quantitySpinner.setAdapter(adapter1);
        quantitySpinner.setOnItemSelectedListener(this);

        orderDisplay = findViewById(R.id.tvOrderDisplay);
        displayStore = findViewById(R.id.tvDisplayStore);
        displayCurrentDate = findViewById(R.id.tvCurrentDate);
        //displayCurrentTime = findViewById(R.id.tvCurrentTime);
        addItem = findViewById(R.id.btnAddItem);
        //test = findViewById(R.id.btnTest);

        firebaseAuth = FirebaseAuth.getInstance();  //getting FirebaseAuth instance

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    DisplayStoreModel displayStoreModel = snapshot.getValue(DisplayStoreModel.class);
                    if(displayStoreModel != null) {
                        displayStore.setText("" + displayStoreModel.getUserStoreName());
                    }else{
                        //displayStore.setText("New users have to REGISTER FIRST!/nOr the app won't work!");
                        Toasty.error(OrderActivity.this, "Please REGISTER here first!", Toasty.LENGTH_LONG).show();
                        startActivity(new Intent(OrderActivity.this, NextRegistrationActivity.class));
                    }
                }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Toast.makeText(OrderActivity.this, "Cannot get user information. Try again later.", Toast.LENGTH_LONG).show();
                Toasty.error(OrderActivity.this, "Cannot get user information. Try again later.", Toasty.LENGTH_LONG).show();
            }
        });

        itemSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                itemTag = itemSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        quantitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                quantityTag = quantitySpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderDisplay.append("-->  " + itemTag + " - " + quantityTag + "\n");
            }
        });

        /*test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OrderActivity.this, TestActivity.class));
            }
        });*/

        displayCurrentDate.setText(date_n);
    }

    private void Logout(){
        firebaseAuth.signOut();
        Toasty.success(OrderActivity.this, "You are now successfully logged out. Login again to continue.", Toasty.LENGTH_LONG).show();
        finish();
        startActivity(new Intent(OrderActivity.this, CustomerLoginActivity.class));
    }

    private void order(){
        String date = displayCurrentDate.getText().toString();
        String store = displayStore.getText().toString();
        String orders = orderDisplay.getText().toString();

        OrderModel ord = new OrderModel(date, store, orders);

        orderRef.add(ord);
    }

    private void updateCheckSnackbar(){
        new AppUpdater(this)
                .setUpdateFrom(UpdateFrom.JSON)
                .setUpdateJSON("https://raw.githubusercontent.com/vabbydante/vaibhavtraders/master/abc.json")
                .setDisplay(Display.SNACKBAR)
                //.setContentOnUpdateNotAvailable("You've the latest version available!")
                .setDuration(Duration.NORMAL)
                .start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.orderMenu:{
                order();
                //Toast.makeText(OrderActivity.this, "Order Placed!", Toast.LENGTH_LONG).show();
                Toasty.success(OrderActivity.this, "Order Placed!", Toasty.LENGTH_LONG).show();
                break;
            }
            case R.id.profileMenu:{
                startActivity(new Intent(OrderActivity.this, CustomerProfileActivity.class));
                break;
            }
            case R.id.orderHistoryMenu:{
                //Toast.makeText(OrderActivity.this, "Order History menu under development", Toast.LENGTH_SHORT).show();
                Toasty.info(OrderActivity.this, "Order History menu under development", Toasty.LENGTH_SHORT).show();
                break;
            }
            case R.id.refreshMenu:{
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
                Toasty.success(OrderActivity.this, "Orderlist refreshed", Toasty.LENGTH_SHORT).show();
                break;
            }
            case R.id.helpMenu:{
                //startActivity(new Intent(OrderActivity.this, HelpActivity.class));
                //Toast.makeText(this, "Help section will be added in upcoming update soon", Toast.LENGTH_LONG).show();
                Toasty.info(this, "Help section will be added in upcoming update soon", Toasty.LENGTH_LONG).show();
                break;
            }
            case R.id.contactMenu:{
                startActivity(new Intent(OrderActivity.this, ContactActivity.class));
                break;
            }
            case R.id.aboutMenu:{
                startActivity(new Intent(OrderActivity.this, AboutActivity.class));
                break;
            }
            case R.id.updateMenu:{
                //Development stage here!
                //updateCheckSnackbar();
                //Toast.makeText(this, "Feature under development!", Toast.LENGTH_SHORT).show();
                Toasty.info(this, "Feature under development!", Toasty.LENGTH_SHORT).show();
                break;
            }
            case R.id.logoutMenu:{
                Logout();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}