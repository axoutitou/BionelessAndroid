package com.example.androidsensors;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.opencsv.CSVWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DataPredictionActivity  extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private long startTime;
    private AccGyr currentMesure;
    JSONArray jsonArray = new JSONArray();

    private static Socket s;
    private static PrintWriter printWriter;

    String message ="ON TESTE FORT";
    private static String ip = "172.20.10.3";

    private String fullFileName;
    private String uniqueFileName;
    private String filePath;
    private String fileName;
    private CSVWriter writer;

    public static final String storageContainer = "azureml-blobstore-95bf6b49-8218-4ad1-9b11-f83ea245d4fe";
    public static final String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=XXXXX;AccountKey=XXXX;EndpointSuffix=core.windows.net";



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

        // Partie enregistrement CSV
        fileName = "MouvementData";
        File storageDir = getExternalFilesDir("DATA");
        String date =  new SimpleDateFormat("dd-MM-yyyy HH-mm-ss", Locale.getDefault()).format(new Date());
        String filename = storageDir + File.separator + fileName + "-" + date + ".csv";
        fullFileName = filename;
        uniqueFileName = fileName + "-" + date + ".csv";
        filePath = storageDir + File.separator;
        try {
            FileWriter fileWriter = new FileWriter(filename, true);
            writer = new CSVWriter(fileWriter);
            String[] headers = {"userId", "dX-acc", "dY-acc", "dZ-acc", "dX-gyr", "dY-gyr", "dZ-gyr", "time", "activityType"};
            writer.writeNext(headers);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
            jsonObject.put("0", Float.toString(currentMesure.getdXacc()));
            jsonObject.put("1", Float.toString(currentMesure.getdYacc()));
            jsonObject.put("2", Float.toString(currentMesure.getdZacc()));
            jsonObject.put("3", Float.toString(currentMesure.getdXgyr()));
            jsonObject.put("4", Float.toString(currentMesure.getdYgyr()));
            jsonObject.put("5", Float.toString(currentMesure.getdZgyr()));
            jsonObject.put("6", floatInstant);
            jsonArray.put(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        timerTextView.setText(String.format("%.1f", floatInstant/1000) );

        String[] values = {"1", Float.toString(currentMesure.getdXacc()), Float.toString(currentMesure.getdYacc()), Float.toString(currentMesure.getdZacc()),
                Float.toString(currentMesure.getdXgyr()), Float.toString(currentMesure.getdYgyr()), Float.toString(currentMesure.getdZgyr()),
                Float.toString(floatInstant), "Immobile"};
        writer.writeNext(values);

        if(floatInstant > 10000){
            System.out.println(jsonArray.toString());
            sendPost(jsonArray.toString());
            jsonArray = new JSONArray();
            //getMaxOccurenceActivity(jsonArray.toString());
            startTime = System.currentTimeMillis();
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

                        getMaxOccurenceActivity(stringBuilder.toString());

                        //Log.d("CONTENT", stringBuilder.toString());


                    }
                    conn.disconnect();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    public void onStopClick(View view) throws IOException {
        sendPost(jsonArray.toString());
        writer.close();
        this.finish();
    }

    public String getMaxOccurenceActivity(final String data){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //data = "[\"Marcher\", \"Marcher\", \"Marcher\", \"Marcher\", \"Marcher\", \"Marcher\", \"Marcher\", \"Marcher\", \"Immobile\", \"Immobile\"]";
                String tmpData = data.replace("\"","").replace("[","").replace("]","").replace(" ","").replace("\n","").replace("\\","");
                List<String> myList = new ArrayList<String>(Arrays.asList(tmpData.split(",")));

                int fqDescendre = Collections.frequency(myList,"Descendre");
                int fqMonter = Collections.frequency(myList,"Monter");
                int fqCourir = Collections.frequency(myList,"Courir");
                int fqMarcher = Collections.frequency(myList,"Marcher");
                int fqImmobile = Collections.frequency(myList,"Immobile");

                ArrayList<Integer> listFq = new ArrayList<Integer>();
                listFq.add(fqDescendre);
                listFq.add(fqMonter);
                listFq.add(fqCourir);
                listFq.add(fqMarcher);
                listFq.add(fqImmobile);

                HashMap<String,Integer> mapFq = new HashMap<String,Integer>();
                mapFq.put("Descendre",fqDescendre);
                mapFq.put("Monter",fqMonter);
                mapFq.put("Courir",fqCourir);
                mapFq.put("Marcher",fqMarcher);
                mapFq.put("Immobile",fqImmobile);

                int max = Collections.max(listFq);

                @SuppressLint({"NewApi", "LocalSuppress"}) String maxMouvement = Collections.max(mapFq.entrySet(), Map.Entry.comparingByValue()).getKey().toString();
                Log.d("maxMouvement", maxMouvement);
                Log.d("MAAAAAAXXX", String.valueOf(max));

                try {
                    s = new Socket(ip, 5000);
                    printWriter = new PrintWriter(s.getOutputStream());
                    printWriter.write(maxMouvement);
                    printWriter.flush();
                    printWriter.close();
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        thread.start();


        return null;

    }


}
