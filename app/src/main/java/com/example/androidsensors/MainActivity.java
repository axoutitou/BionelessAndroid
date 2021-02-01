package com.example.androidsensors;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onStartClick(View view) {

        Intent intentData   = new Intent(getApplicationContext(), DataActivity.class);
        RadioGroup activityType = findViewById(R.id.activityType);
        RadioButton selectedButton = findViewById(activityType.getCheckedRadioButtonId());
        intentData.putExtra("activityType", selectedButton.getText());
        startActivity(intentData);
    }
}