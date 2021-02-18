package com.example.androidsensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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

        setContentView(R.layout.prediction_activity);
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


        TextView accX = findViewById(R.id.accXa);
        TextView accY = findViewById(R.id.accYa);
        TextView accZ = findViewById(R.id.accZa);

        TextView gyrX = findViewById(R.id.gyrXa);
        TextView gyrY = findViewById(R.id.gyrYa);
        TextView gyrZ = findViewById(R.id.gyrZa);

        TextView timerTextView = findViewById(R.id.timer);

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            currentMesure.setdXacc(event.values[0]);
            currentMesure.setdYacc(event.values[1]);
            currentMesure.setdZacc(event.values[2]);

            accX.setText(Float.toString(currentMesure.getdXacc()));
            accY.setText(Float.toString(currentMesure.getdYacc()));
            accZ.setText(Float.toString(currentMesure.getdZacc()));

           // System.out.println("ACCELEROMETRE : Valeurs récupérés : x="+currentMesure.getdXacc()+" | y="+currentMesure.getdYacc()+" | z="+currentMesure.getdZacc());
        }

        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            currentMesure.setdXgyr(event.values[0]);
            currentMesure.setdYgyr(event.values[1]);
            currentMesure.setdZgyr(event.values[2]);


            gyrX.setText(Float.toString(currentMesure.getdXgyr()));
            gyrY.setText(Float.toString(currentMesure.getdYgyr()));
            gyrZ.setText(Float.toString(currentMesure.getdZgyr()));

         //   System.out.println("GYROSCOPE : Valeurs récupérés : x="+currentMesure.getdXgyr()+" | y="+currentMesure.getdYgyr()+" | z="+currentMesure.getdZgyr());
        }
        JSONObject jsonObject = new JSONObject();
        Long instant = System.currentTimeMillis()-startTime;
        float floatInstant = (float)instant;
        try {
            jsonObject.put("0", currentMesure.getdXacc());
            jsonObject.put("1", currentMesure.getdYacc());
            jsonObject.put("2", currentMesure.getdZacc());
            jsonObject.put("3", currentMesure.getdXgyr());
            jsonObject.put("4", currentMesure.getdYgyr());
            jsonObject.put("5", currentMesure.getdZgyr());
            jsonObject.put("6", floatInstant);
            jsonArray.put(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(floatInstant > 500){
            sendPost(jsonArray.toString());
            startTime = System.currentTimeMillis();
            jsonArray = new JSONArray();
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void sendPost(String jsonElement) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://8b95afc9-e4d1-4ae6-a2eb-1ec7dc3a8668.westeurope.azurecontainer.io/score");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                    os.writeBytes(jsonElement);
                    os.flush();
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    System.out.println("STATUS :" + String.valueOf(conn.getResponseCode()));

                    StringBuilder stringBuilder = new StringBuilder();
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStreamReader streamReader = new InputStreamReader(conn.getInputStream());
                        BufferedReader bufferedReader = new BufferedReader(streamReader);
                        String response = null;
                        while ((response = bufferedReader.readLine()) != null) {
                            stringBuilder.append(response + "\n");
                        }
                        bufferedReader.close();

                        Log.d("CONTENT", stringBuilder.toString());
                    }
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    public void onStopClick(View view) {
        this.finish();
    }
}
