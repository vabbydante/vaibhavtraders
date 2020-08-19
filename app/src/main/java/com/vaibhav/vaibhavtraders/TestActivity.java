package com.vaibhav.vaibhavtraders;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    //private Spinner dataSpinner;
    FirebaseAuth firebaseAuth;
    //FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        //dataSpinner = findViewById(R.id.spinner4);

        firebaseAuth = FirebaseAuth.getInstance();
        //firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("spinneritems");

        databaseReference.addValueEventListener(new ValueEventListener() {
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

                Spinner dataSpinner = findViewById(R.id.spinner4);
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(TestActivity.this, android.R.layout.simple_spinner_item, items);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dataSpinner.setAdapter(dataAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}