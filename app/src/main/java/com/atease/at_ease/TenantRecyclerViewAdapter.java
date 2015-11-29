package com.atease.at_ease;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.atease.at_ease.models.Payment;
import com.mikepenz.iconics.Iconics;
import com.mikepenz.iconics.view.IconicsButton;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

/**
 * Created by Jesse on 11/21/2015.
 */
public class TenantRecyclerViewAdapter extends RecyclerView.Adapter<TenantRecyclerViewAdapter.TenantViewHolder>{

    private List<ParseUser> tenantList;
    ParseUser currentUser;
    private Context mContext;

    public TenantRecyclerViewAdapter(Context myContext, List<ParseUser> properties){
        this.mContext = myContext;
        this.tenantList = properties;
        this.currentUser = ParseUser.getCurrentUser();
    }


    public class TenantViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public Button btnDelete;


        public TenantViewHolder(View view) {
            super(view);

            tvName = (TextView) view.findViewById(R.id.item_view_tenant_name);
            btnDelete = (IconicsButton) view.findViewById(R.id.item_view_tenant_delete_btn);

        }
    }

    @Override
    public TenantViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_view_tenant, parent, false);
        return new TenantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TenantViewHolder holder, final int pos){
        final ParseUser tenant = tenantList.get(pos);
        final String tenId = tenant.getObjectId();
        holder.tvName.setText(tenant.getString("firstName") + " " + tenant.getString("lastName"));
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(mContext)
                        .title("Confirm Tenant Deletion")
                        .content("Are you sure you want to delete this tenant? This action can not be undone!")
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
                                tenant.put("liveAt", "");
                                tenant.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Log.i("AT-EASE", "Property deletion successful");
                                            removeAt(pos);
                                        } else {
                                            Log.d("AT-EASE", "Property deletion unsuccessful. Error: " + e.toString());
                                        }
                                    }
                                });
                            }
                        })
                        .show();
            }
        });





    }


    @Override
    public int getItemCount() {
        return tenantList.size();
    }

    public void removeAt(int position) {
        tenantList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, tenantList.size());
    }

}
