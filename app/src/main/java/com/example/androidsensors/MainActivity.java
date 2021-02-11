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
        RadioButton selectedButton = findViewById(activityType.getCheckedRadioButtonId());
        intentData.putExtra("activityType", selectedButton.getText());

        EditText id = (EditText) findViewById(R.id.userId);

        Integer userId = (null != id.getText().toString()
                &&  !id.getText().toString().isEmpty()) ?  Integer.parseInt(id.getText().toString()) : null;
        if(null == userId){
            ((TextView)findViewById(R.id.textView2)).setBackgroundColor(Color.RED);
        }else{
            intentData.putExtra("userId", userId.toString());
            startActivity(intentData);
        }

    }
}