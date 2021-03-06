package com.vaibhav.vaibhavtraders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OrderActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    String itemTag, quantityTag;
    String date_n = new
            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());
    //String ordersString;
    //String sample = "data";
    private Spinner itemSpinner;
    private Spinner quantitySpinner;
    private TextView orderDisplay;
    private TextView displayStore;
    private TextView displayCurrentDate;
    //private TextView displayCurrentTime;
    private Button addItem;
    private Button test;
    FirebaseAuth firebaseAuth;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference orderRef = db.collection("orderdata");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        //setting up UI Views and setting ArrayAdapters for the spinners. (using the 'string-array' resource from strings.xml)
        itemSpinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemSpinner.setAdapter(adapter);
        itemSpinner.setOnItemSelectedListener(this);

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
        test = findViewById(R.id.btnTest);

        firebaseAuth = FirebaseAuth.getInstance();  //getting FirebaseAuth instance

        firebaseAuth = FirebaseAuth.getInstance();

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DisplayStoreModel displayStoreModel = snapshot.getValue(DisplayStoreModel.class);
                displayStore.setText("" + displayStoreModel.getUserStoreName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OrderActivity.this, "Cannot get user information. Try again later.", Toast.LENGTH_LONG).show();
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
                orderDisplay.append("-->  " + itemTag + " - " + quantityTag + " Pkt" + "\n");
            }
        });

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OrderActivity.this, TestActivity.class));
            }
        });

        displayCurrentDate.setText(date_n);
    }

    private void Logout(){
        firebaseAuth.signOut();
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
                Toast.makeText(OrderActivity.this, "Order Placed!", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.profileMenu:{
                startActivity(new Intent(OrderActivity.this, CustomerProfileActivity.class));
                break;
            }
            case R.id.orderHistoryMenu:{
                Toast.makeText(OrderActivity.this, "Order History menu under development", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.refreshMenu:{
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
                break;
            }
            case R.id.helpMenu:{
                startActivity(new Intent(OrderActivity.this, HelpActivity.class));
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