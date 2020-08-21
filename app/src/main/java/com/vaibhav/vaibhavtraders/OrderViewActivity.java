package com.vaibhav.vaibhavtraders;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class OrderViewActivity extends AppCompatActivity {

    private TextView viewOrders;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("orderdata");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_view);

        viewOrders = findViewById(R.id.orderView);

        notebookRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                String dataorder = "";

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    OrderModel orderModel = documentSnapshot.toObject(OrderModel.class);

                    String date = orderModel.getDate();
                    String store = orderModel.getStore();
                    String orders = orderModel.getOrders();

                    dataorder += "Date/Time : " + date + "\nStore : " + store + "\nOrders : " + orders + "\n\n";
                }
                viewOrders.setText(dataorder);
            }
        });
    }
}