package com.vaibhav.vaibhavtraders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class OrderAdapter extends FirestoreRecyclerAdapter<OrderModel, OrderAdapter.OrderHolder>{

    public OrderAdapter(@NonNull FirestoreRecyclerOptions<OrderModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull OrderHolder holder, int position, @NonNull OrderModel model) {
        holder.textViewStoreCard.setText(model.getStore());
        holder.textViewDateTime.setText(model.getDate());
        holder.textViewOrdersCard.setText(model.getOrders());
    }

    @NonNull
    @Override
    public OrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item,
                parent, false);
        return new OrderHolder(v);
    }

    public void deleteCardItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class OrderHolder extends RecyclerView.ViewHolder{
        TextView textViewStoreCard;
        TextView textViewDateTime;
        TextView textViewOrdersCard;

        public OrderHolder(@NonNull View itemView) {
            super(itemView);
            textViewDateTime = itemView.findViewById(R.id.tvDateTime);
            textViewOrdersCard = itemView.findViewById(R.id.tvOrdersCard);
            textViewStoreCard = itemView.findViewById(R.id.tvStoreCard);
        }
    }
}
