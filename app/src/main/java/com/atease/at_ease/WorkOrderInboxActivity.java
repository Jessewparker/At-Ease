package com.atease.at_ease;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.software.shell.fab.ActionButton;

/**
 * Created by Mark on 10/3/2015.
 */
public class WorkOrderInboxActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionButton actionButton = (ActionButton) findViewById(R.id.action_button);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WorkOrderInboxActivity.this, NewWorkOrderActivity.class);
                startActivity(intent);
            }
        });
    }
}


