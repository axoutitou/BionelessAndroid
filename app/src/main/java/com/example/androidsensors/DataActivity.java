package com.example.androidsensors;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;


public class DataActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private long startTime;
    private String activityType;
    private String fileName;
    private CSVWriter writer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        fileName = "MouvementData";
        File storageDir = getExternalFilesDir("DATA");

        String date =  new SimpleDateFormat("dd-MM-yyyy HH-mm-ss", Locale.getDefault()).format(new Date());
        String filename = storageDir + File.separator + fileName + "-" + date + ".csv";
        try {
            FileWriter fileWriter = new FileWriter(filename, true);
            writer = new CSVWriter(fileWriter);
            String[] headers = {"dX-acc", "dY-acc", "dZ-acc", "dX-gyr", "dY-gyr", "dZ-gyr", "time", "activityType"};
            writer.writeNext(headers);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        float dXacc = 0;
        float dYacc = 0;
        float dZacc = 0;

        float dXgyr = 0;
        float dYgyr = 0;
        float dZgyr = 0;

        TextView accX = findViewById(R.id.accX);
        TextView accY = findViewById(R.id.accY);
        TextView accZ = findViewById(R.id.accZ);

        TextView gyrX = findViewById(R.id.gyrX);
        TextView gyrY = findViewById(R.id.gyrY);
        TextView gyrZ = findViewById(R.id.gyrZ);

        TextView timerTextView = findViewById(R.id.timer);

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            dXacc = event.values[0];
            dYacc = event.values[1];
            dZacc = event.values[2];

            accX.setText(Float.toString(dXacc));
            accY.setText(Float.toString(dYacc));
            accZ.setText(Float.toString(dZacc));

            System.out.println("ACCELEROMETRE : Valeurs récupérés : x="+dXacc+" | y="+dYacc+" | z="+dZacc);
        }

        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            dXgyr = event.values[0];
            dYgyr = event.values[1];
            dZgyr = event.values[2];

            gyrX.setText(Float.toString(dXgyr));
            gyrY.setText(Float.toString(dYgyr));
            gyrZ.setText(Float.toString(dZgyr));

            System.out.println("GYROSCOPE : Valeurs récupérés : x="+dXgyr+" | y="+dYgyr+" | z="+dZgyr);
        }

        Long instant = System.currentTimeMillis()-startTime;
        float floatInstant = (float)instant;
        timerTextView.setText(String.format("%.1f", floatInstant/1000) );

        String[] values = {Float.toString(dXacc), Float.toString(dYacc), Float.toString(dZacc),
                Float.toString(dXgyr), Float.toString(dYgyr), Float.toString(dZgyr),
                Float.toString(floatInstant), activityType};
        writer.writeNext(values);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onStopClick(View view) throws IOException {
        writer.close();
        this.finish();
    }
}