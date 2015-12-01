package com.atease.at_ease;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.iconics.view.IconicsButton;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

/**
 * Created by Mark on 10/4/2015.
 */
public class WorkOrderInboxRecyclerViewAdapter extends RecyclerView.Adapter<WorkOrderInboxRecyclerViewAdapter.WorkOrderViewHolder> {
    private List<WorkOrder> workOrders;
    private Context mcontext;


    public class WorkOrderViewHolder extends RecyclerView.ViewHolder{
        public TextView subjectView;
        public IconicsButton btnView;
        public IconicsButton btnDelete;
        public IconicsButton btnDone;
        public Context context;


        public WorkOrderViewHolder (View v) {
            super(v);

            subjectView = (TextView) v.findViewById(R.id.work_order_inbox_subject);
            btnView = (IconicsButton) v.findViewById(R.id.work_order_inbox_view_btn);
            btnDelete= (IconicsButton) v.findViewById(R.id.work_order_inbox_delete_btn);
            btnDone = (IconicsButton) v.findViewById(R.id.work_order_inbox_done_btn);
            context = v.getContext();

        }
    }

    public WorkOrderInboxRecyclerViewAdapter(Context inContext, List<WorkOrder> inWorkOrders) {
        this.workOrders = inWorkOrders;
        this.mcontext = inContext;
    }

    @Override
    public WorkOrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_view_work_order_inbox, parent, false);

        return new WorkOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WorkOrderViewHolder holder, int position) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        final WorkOrder item = workOrders.get(position);
        final WorkOrderViewHolder workOrderViewHolder = (WorkOrderViewHolder) holder;
        workOrderViewHolder.subjectView.setText(item.getSubject());
        int txtColor = R.color.colorPrimaryDark;
        if (item.isManagerDone()){
            Log.d("AT-EASE", "Work Order is Manager Done");
            txtColor = R.color.colorAccent;
        }
        workOrderViewHolder.subjectView.setTextColor(((WorkOrderViewHolder) holder).context.getResources().getColor(txtColor));
        if (item.getManager() == currentUser) {
            workOrderViewHolder.btnDelete.setEnabled(false);
            if (item.isManagerDone()) {
                workOrderViewHolder.btnDone.setBackgroundColor(mcontext.getResources().getColor(R.color.secondary));
            }
        }
        if (item.getTenant() == currentUser) {
            if (item.isManagerDone()) {
                workOrderViewHolder.btnDone.setEnabled(true);
                workOrderViewHolder.btnDone.setBackgroundColor(mcontext.getResources().getColor(R.color.secondary));
            }
            else {
                workOrderViewHolder.btnDone.setEnabled(false);
            }
        }
//        workOrderViewHolder.data = item;
        holder.btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mcontext, ViewWorkOrderActivity.class);
                Log.d("At-Ease", ":" + item.getObjectId());
                intent.putExtra("workOrder", item.getObjectId());
                mcontext.startActivity(intent);
            }
        });
        holder.btnDelete.setTag(position);
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
//                final int pos = WorkOrderInboxActivity.removeWorkOrder(item, workOrders, WorkOrderInboxRecyclerViewAdapter.this);
                new MaterialDialog.Builder(mcontext)
                        .title("Confirm Deletion")
                        .content("Are you sure you want to cancel this Work Order? This action can not be undone!")
                        .positiveText("Delete")
                        .negativeText("Cancel")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                return;
                            }
                        })
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                ParseUser currentUser = ParseUser.getCurrentUser();
                                if(item.getTenant() == currentUser) {
                                    final int pos = (Integer) v.getTag();
                                    item.setDeleted(true);
                                    item.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e != null) {

                                                // There was some error.
                                                Log.d("At-Ease", "Work order confirmation not successful: " + e.getMessage());
//                                                displaySnackbar(inWorkOrder.getSubject() + " completion not successful", null, null);
                                                return;
                                            } else {
                                                workOrders.remove(pos);
                                                notifyItemRemoved(pos);
                                                Log.i("At-Ease", "Work order completed");

//                                                displaySnackbar(inWorkOrder.getSubject() + " completed", null, null);
                                            }
                                        }
                                    });
                                } else {
                                    Log.i("At-Ease", "Managers are not allowed to cancel a work order");
//                                    displaySnackbar("Managers are not allowed to cancel a work order", null, null);
                                }

                            }
                        })
                        .show();
            }
        });
        final IconicsButton btnDone = (IconicsButton) holder.btnDone;
        holder.btnDone.setTag(position);
        holder.btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //                final int pos = removeWorkOrder(inWorkOrder);
                new MaterialDialog.Builder(mcontext)
                        .title("Confirm Completion")
                        .content("Are you sure you want to designate this Work Order as completed? This action can not be undone!")
                        .positiveText("Confirm")
                        .negativeText("Cancel")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                return;
                            }
                        })
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                ParseUser currentUser = ParseUser.getCurrentUser();
                                if (item.getManager() == currentUser) {
                                    item.setManagerDone(true);
                                    btnDone.setBackgroundColor(mcontext.getResources().getColor(R.color.secondary));
                                    item.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e != null) {
                                                // There was some error.
                                                Log.d("At-Ease", "Work order confirmation not successful: " + e.getMessage());
//                                                displaySnackbar(inWorkOrder.getSubject() + " completion not successful", null, null);
                                                return;
                                            } else {
                                                Log.i("At-Ease", "Work order completed");
//                                                displaySnackbar(inWorkOrder.getSubject() + " completed", null, null);
                                            }
                                        }
                                    });
                                } else {
                                    item.setTenantDone(true);
                                    item.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e != null) {
                                                // There was some error.
                                                Log.d("At-Ease", "Work order confirmation not successful: " + e.getMessage());
//                                                displaySnackbar(inWorkOrder.getSubject() + " completion not successful", null, null);
                                                return;
                                            } else {
                                                Log.i("At-Ease", "Work order completed");
//                                                displaySnackbar(inWorkOrder.getSubject() + " completed", null, null);
                                            }
                                        }
                                    });
                                }
                                //only not sure part? double saves back to back
                                if (item.isManagerDone() && item.isTenantDone()) {
                                    final int pos = (Integer) v.getTag();
                                    item.setDeleted(true);
                                    item.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e != null) {
                                                // There was some error.
                                                Log.d("At-Ease", "Work order double confirmed not successful: " + e.getMessage());
                                                return;
                                            } else {
                                                workOrders.remove(pos);
                                                notifyItemRemoved(pos);
                                                Log.i("At-Ease", "Work order double confirmed");
                                            }
                                        }
                                    });
                                }
                            }
                        })
                        .show();
//                displaySnackbar(inWorkOrder.getSubject() + " removed", "Undo", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        addWorkOrder(pos, inWorkOrder);
//                    }
//                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return workOrders.size();
    }
}
