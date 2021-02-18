package com.example.androidsensors;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onStartClick(View view) {

        Intent intentData   = new Intent(getApplicationContext(), DataActivity.class);
        RadioGroup activityType = findViewById(R.id.activityType);
        RadioButton selectedActivity = findViewById(activityType.getCheckedRadioButtonId());
        intentData.putExtra("activityType", selectedActivity.getText());

        RadioGroup userId = findViewById(R.id.userId);
        RadioButton selectedButton = findViewById(userId.getCheckedRadioButtonId());
        String userIdString = selectedButton.getTag().toString();
        intentData.putExtra("userId", userIdString);

        startActivity(intentData);

    }
}