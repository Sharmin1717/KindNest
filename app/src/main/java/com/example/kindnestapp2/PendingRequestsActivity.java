package com.example.kindnestapp2;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class PendingRequestsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This line simply shows the layout file. It does nothing else.
        setContentView(R.layout.activity_pending_requests);
    }
}