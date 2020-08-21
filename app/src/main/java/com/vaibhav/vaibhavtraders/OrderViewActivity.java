package com.vaibhav.vaibhavtraders;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Locale;

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

                    sendNotification();

                    String date = orderModel.getDate();
                    String store = orderModel.getStore();
                    String orders = orderModel.getOrders();

                    dataorder += "Date/Time : " + date + "\nStore : " + store + "\nOrders : " + orders + "\n\n";
                }
                viewOrders.setText(dataorder);
            }
        });
    }

    private void sendNotification() {
        long[] v = {100, 420, 150, 420};
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent intent = new Intent(this, OrderViewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.logo)
                .setContentTitle("You have new orders!")
                .setContentText("Tap to view incoming orders")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                //.setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setSound(uri)
                .setVibrate(v)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(001, builder.build());
    }
}