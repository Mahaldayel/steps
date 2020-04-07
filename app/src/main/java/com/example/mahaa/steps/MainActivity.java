package com.example.mahaa.steps;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.content.ContentValues.TAG;


public class MainActivity extends Activity implements SensorEventListener{
    private SensorManager sensorManager;
    private TextView count;
    private TextView ff,vv;
    private TextView Calories;
    boolean activityRunning;
public int f=0;
public int v=0;
public int r=0;
    private int[] values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      //count.getText().clear();
        count = (TextView) findViewById(R.id.count);
        ff = (TextView) findViewById(R.id.f);
        vv = (TextView) findViewById(R.id.v);
        count.setText(" ");
        Calories = (TextView) findViewById(R.id.Calories);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
       // registermessage();
        //connecting
        String clientId = MqttClient.generateClientId();
        MqttAndroidClient client =
                new MqttAndroidClient(this.getApplicationContext(), "tcp://broker.hivemq.com:1883",
                        clientId);

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "onSuccess");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        ////////////publish
        String topic = "foo/bar";
        String payload = "the payload";
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }//////
    }
    public void registermessage(View view) {

        //count.setText(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //count.setText(String.valueOf("0"));

        activityRunning = true;
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if(countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Count sensor not available!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityRunning = false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[]values=event.values;
        Calendar c= Calendar.getInstance();
        Calendar m= Calendar.getInstance();
        Calendar s= Calendar.getInstance();
        DateFormat df=new SimpleDateFormat("HH:mm:ss");
        String time=df.format(Calendar.getInstance().getTime());
        if(activityRunning) {
            //count.setText(String.valueOf("0"));
             f = (int)values[0];

         //  if((f>0) && (v==0)&&)
        if(c.get(Calendar.HOUR)==0 && m.get(Calendar.MINUTE)==0 && s.get(Calendar.SECOND)==0)   {//to restart the counter everyday
              // v=f;
            v= (int)values[0];

        } else
               r=f-v;//since the value cannot be changed subtract the whole from the day value
         //  r=f-v;


           count.setText(String.valueOf(r));
            vv.setText(String.valueOf(v));
            ff.setText(String.valueOf(f));
            // count.setText(String.valueOf("0"));
           Calories.setText(String.valueOf("0"));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

