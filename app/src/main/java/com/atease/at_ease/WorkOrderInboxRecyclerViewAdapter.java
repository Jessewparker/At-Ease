package com.atease.at_ease;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import co.dift.ui.SwipeToAction;

/**
 * Created by Mark on 10/4/2015.
 */
public class WorkOrderInboxRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<WorkOrder> workOrders;

    public class WorkOrderViewHolder extends SwipeToAction.ViewHolder<WorkOrder> {
        public TextView nameView;
        public TextView dateView;
        public TextView subjectView;

        public WorkOrderViewHolder (View v) {
            super(v);

            nameView = (TextView) v.findViewById(R.id.work_order_inbox_name);
            dateView = (TextView) v.findViewById(R.id.work_order_inbox_date);
            subjectView = (TextView) v.findViewById(R.id.work_order_inbox_subject);

        }
    }

    public WorkOrderInboxRecyclerViewAdapter(List<WorkOrder> inWorkOrders) {
        this.workOrders = inWorkOrders;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_view_work_order_inbox, parent, false);

        return new WorkOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        WorkOrder item = workOrders.get(position);
        WorkOrderViewHolder workOrderViewHolder = (WorkOrderViewHolder) holder;
//        workOrderViewHolder.nameView.setText(item.get_name());
//        workOrderViewHolder.dateView.setText(item.get_date());
        workOrderViewHolder.subjectView.setText(item.GetSubject());
        workOrderViewHolder.data = item;
    }

    @Override
    public int getItemCount() {
        return workOrders.size();
    }
}
