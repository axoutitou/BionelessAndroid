package com.example.androidsensors;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
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
    private AccGyr currentmesure;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);

        currentmesure = new AccGyr();


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
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
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
            currentmesure.setdXacc(event.values[0]);
            currentmesure.setdYacc(event.values[1]);
            currentmesure.setdZacc(event.values[2]);

            accX.setText(Float.toString(currentmesure.getdXacc()));
            accY.setText(Float.toString(currentmesure.getdYacc()));
            accZ.setText(Float.toString(currentmesure.getdZacc()));

            System.out.println("ACCELEROMETRE : Valeurs récupérés : x="+currentmesure.getdXacc()+" | y="+currentmesure.getdYacc()+" | z="+currentmesure.getdZacc());
        }

        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            currentmesure.setdXgyr(event.values[0]);
            currentmesure.setdYgyr(event.values[1]);
            currentmesure.setdZgyr(event.values[2]);


            gyrX.setText(Float.toString(currentmesure.getdXgyr()));
            gyrY.setText(Float.toString(currentmesure.getdYgyr()));
            gyrZ.setText(Float.toString(currentmesure.getdZgyr()));

            System.out.println("GYROSCOPE : Valeurs récupérés : x="+currentmesure.getdXgyr()+" | y="+currentmesure.getdYgyr()+" | z="+currentmesure.getdZgyr());
        }


        Long instant = System.currentTimeMillis()-startTime;
        float floatInstant = (float)instant;
        timerTextView.setText(String.format("%.1f", floatInstant/1000) );

        String[] values = {Float.toString(currentmesure.getdXacc()), Float.toString(currentmesure.getdYacc()), Float.toString(currentmesure.getdZacc()),
                Float.toString(currentmesure.getdXgyr()), Float.toString(currentmesure.getdYgyr()), Float.toString(currentmesure.getdZgyr()),
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