package com.atease.at_ease;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.atease.at_ease.models.Payment;
import com.parse.ParseUser;

import java.util.List;



/**
 * Created by Jesse on 10/23/2015.
 */
public class PaymentHistoryRecyclerViewAdapter extends RecyclerView.Adapter<PaymentHistoryRecyclerViewAdapter.PaymentHistoryViewHolder>{

    private List<Payment> paymentsList;
    ParseUser currentUser;

    public PaymentHistoryRecyclerViewAdapter(List<Payment> payments){
        this.paymentsList = payments;
        this.currentUser = ParseUser.getCurrentUser();
    }


    public class PaymentHistoryViewHolder extends RecyclerView.ViewHolder {
        public TextView tvAmount;
        public TextView tvDate;
        public TextView tvUserName;


        public PaymentHistoryViewHolder(View view) {
            super(view);

            tvAmount = (TextView) view.findViewById(R.id.tvAmount);
            tvDate = (TextView) view.findViewById(R.id.tvDate);
            tvUserName = (TextView) view.findViewById(R.id.tvUserName);

        }
    }

    @Override
    public PaymentHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_view_payment_history, parent, false);
        return new PaymentHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PaymentHistoryViewHolder holder, int pos){
        Payment payment = paymentsList.get(pos);
        holder.tvDate.setText(payment.getDate());
        if(currentUser.equals(payment.getTenant())){
            holder.tvAmount.setText("Paid $" + payment.getAmount());
            holder.tvUserName.setText(payment.getManagerName());
        }
        else{
            holder.tvAmount.setText("Received $" + payment.getAmount());
            holder.tvUserName.setText(payment.getTenantName());
        }




    }

    @Override
    public int getItemCount() {
        return paymentsList.size();
    }



}
