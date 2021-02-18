package com.example.androidsensors;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
    }


    public void startDataCollections(View view) {

        Intent intentData = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intentData);

    }

    public void startUsingApp(View view) {
        Intent intentData = new Intent(getApplicationContext(), DataPredictionActivity.class);
        startActivity(intentData);
    }
}
