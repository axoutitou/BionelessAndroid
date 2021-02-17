package com.example.androidsensors;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.JsonWriter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.opencsv.CSVWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DataPredictionActivity  extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private long startTime;
    private AccGyr currentMesure;
    JSONArray jsonArray = new JSONArray();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        jsonArray = new JSONArray();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        startTime = System.currentTimeMillis();
        currentMesure = new AccGyr();

    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        TextView accX = findViewById(R.id.accX);
        TextView accY = findViewById(R.id.accY);
        TextView accZ = findViewById(R.id.accZ);

        TextView gyrX = findViewById(R.id.gyrX);
        TextView gyrY = findViewById(R.id.gyrY);
        TextView gyrZ = findViewById(R.id.gyrZ);

        TextView timerTextView = findViewById(R.id.timer);

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            currentMesure.setdXacc(event.values[0]);
            currentMesure.setdYacc(event.values[1]);
            currentMesure.setdZacc(event.values[2]);

            accX.setText(Float.toString(currentMesure.getdXacc()));
            accY.setText(Float.toString(currentMesure.getdYacc()));
            accZ.setText(Float.toString(currentMesure.getdZacc()));

            System.out.println("ACCELEROMETRE : Valeurs récupérés : x="+currentMesure.getdXacc()+" | y="+currentMesure.getdYacc()+" | z="+currentMesure.getdZacc());
        }

        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            currentMesure.setdXgyr(event.values[0]);
            currentMesure.setdYgyr(event.values[1]);
            currentMesure.setdZgyr(event.values[2]);


            gyrX.setText(Float.toString(currentMesure.getdXgyr()));
            gyrY.setText(Float.toString(currentMesure.getdYgyr()));
            gyrZ.setText(Float.toString(currentMesure.getdZgyr()));

            System.out.println("GYROSCOPE : Valeurs récupérés : x="+currentMesure.getdXgyr()+" | y="+currentMesure.getdYgyr()+" | z="+currentMesure.getdZgyr());
        }
        JSONObject jsonObject = new JSONObject();
        Long instant = System.currentTimeMillis()-startTime;
        float floatInstant = (float)instant;
        try {
            jsonObject.put("dX-acc", currentMesure.getdXacc());
            jsonObject.put("dY-acc", currentMesure.getdYacc());
            jsonObject.put("dZ-acc", currentMesure.getdZacc());
            jsonObject.put("dX-gyr", currentMesure.getdXgyr());
            jsonObject.put("dY-gyr", currentMesure.getdYgyr());
            jsonObject.put("dZ-gyr", currentMesure.getdZgyr());
            jsonObject.put("time", floatInstant);
            jsonArray.put(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(floatInstant > 500){
            //send data to predict class
            new SendDeviceDetails().execute("http://52.88.194.67:8080/IOTProjectServer/registerDevice", jsonArray.toString());
            // Clean array
            jsonArray = new JSONArray();
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
