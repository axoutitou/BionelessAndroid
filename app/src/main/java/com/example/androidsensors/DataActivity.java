package com.example.androidsensors;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class DataActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private long startTime;
    private String activityType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        setContentView(R.layout.activity_data);
        startTime = System.currentTimeMillis();

        Intent intent = getIntent();
        if (intent.hasExtra("activityType")){
            activityType = intent.getStringExtra("activityType");
            TextView activityText = findViewById(R.id.activity);
            activityText.setText("Enregistrement en cours pour l'activité : "+activityType);
        }
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(this, accelerometer);
        sensorManager.unregisterListener(this, gyroscope);
        super.onPause();
    }

    @Override
    protected void onResume() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_UI);
        super.onResume();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = 0;
        float y = 0;
        float z = 0;

        TextView accX = findViewById(R.id.accX);
        TextView accY = findViewById(R.id.accY);
        TextView accZ = findViewById(R.id.accZ);

        TextView gyrX = findViewById(R.id.gyrX);
        TextView gyrY = findViewById(R.id.gyrY);
        TextView gyrZ = findViewById(R.id.gyrZ);

        TextView timerTextView = findViewById(R.id.timer);

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            x = event.values[0];
            y = event.values[1];
            z = event.values[2];

            accX.setText(Float.toString(x));
            accY.setText(Float.toString(y));
            accZ.setText(Float.toString(z));

            System.out.println("ACCELEROMETRE : Valeurs récupérés : x="+x+" | y="+y+" | z="+z);
        }

        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            x = event.values[0];
            y = event.values[1];
            z = event.values[2];

            gyrX.setText(Float.toString(x));
            gyrY.setText(Float.toString(y));
            gyrZ.setText(Float.toString(z));

            System.out.println("GYROSCOPE : Valeurs récupérés : x="+x+" | y="+y+" | z="+z);
        }
        Long instant = System.currentTimeMillis()-startTime;
        float floatInstant = (float)instant;
        timerTextView.setText(String.format("%.1f", floatInstant/1000) );
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onStopClick(View view) {
        this.finish();
    }
}