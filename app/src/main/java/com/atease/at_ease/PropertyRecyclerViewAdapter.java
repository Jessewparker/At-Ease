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

import java.util.List;

/**
 * Created by Jesse on 11/21/2015.
 */
public class PropertyRecyclerViewAdapter extends RecyclerView.Adapter<PropertyRecyclerViewAdapter.PropertyViewHolder>{

    private List<ParseObject> propertyList;
        ParseUser currentUser;
    private Context mContext;

public PropertyRecyclerViewAdapter(Context myContext, List<ParseObject> properties){
        this.mContext = myContext;
        this.propertyList = properties;
        this.currentUser = ParseUser.getCurrentUser();
        }


public class PropertyViewHolder extends RecyclerView.ViewHolder {
    public TextView tvNickname;
    public Button btnMessaging;
    public IconicsButton btnPaymentSettings;
    public Button btnWorkOrders;
    public IconicsButton btnPaymentHistory;
    public IconicsButton btnDelete;


    public PropertyViewHolder(View view) {
        super(view);

        tvNickname = (TextView) view.findViewById(R.id.tvNickname);
        btnMessaging = (Button) view.findViewById(R.id.btnMessaging);
        btnPaymentSettings = (IconicsButton) view.findViewById(R.id.btnPaymentSettings);
        btnWorkOrders = (Button) view.findViewById(R.id.btnWorkOrders);
        btnPaymentHistory = (IconicsButton) view.findViewById(R.id.btnPaymentHistory);
        btnDelete = (IconicsButton) view.findViewById(R.id.btnDelete);

    }
}

    @Override
    public PropertyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_view_property, parent, false);
        return new PropertyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PropertyViewHolder holder, final int pos){
        final ParseObject property = propertyList.get(pos);
        final String propId = property.getObjectId();
        holder.tvNickname.setText(property.getString("nickname"));
        holder.btnPaymentSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ManagerSettingsActivity.class);
                intent.putExtra("propId", propId);
                mContext.startActivity(intent);
            }
        });
        holder.btnMessaging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ListUsersActivity.class);
                intent.putExtra("propId", propId);
                mContext.startActivity(intent);
            }
        });
        holder.btnWorkOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ManagerSettingsActivity.class);
                intent.putExtra("propId", propId);
                mContext.startActivity(intent);
            }
        });
        holder.btnPaymentHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,PaymentHistoryActivity.class);
                intent.putExtra("propId", propId);
                mContext.startActivity(intent);
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(mContext)
                        .title("Confirm Property Deletion")
                        .content("Are you sure you want to delete this property? This action can not be undone!")
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
                                property.deleteInBackground(new DeleteCallback() {
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
        return propertyList.size();
    }

    public void removeAt(int position) {
        propertyList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, propertyList.size());
    }

}
