package com.example.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

public class Accelerometer extends AppCompatActivity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    static final float ALPHA = 0.25f;
    private float[] accelerometerReading = new float[3];
    private boolean shouldPling = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        lowPass(event.values, accelerometerReading);
        double x = Math.round(accelerometerReading[0]*100) / 100.0;
        double y = Math.round(accelerometerReading[1]*100) / 100.0;
        double z = Math.round(accelerometerReading[2]*100) / 100.0;
        TextView textView7 = findViewById(R.id.textView7);
        textView7.setText(String.valueOf(x));
        TextView textView8 = findViewById(R.id.textView8);
        textView8.setText(String.valueOf(y));
        TextView textView9 = findViewById(R.id.textView9);
        textView9.setText(String.valueOf(z));

        TextView leftright = findViewById(R.id.leftright);
        TextView backwardsforwards = findViewById(R.id.backwardsforwards);
        TextView downup = findViewById(R.id.downup);

        //shows which way the phone is leaning from the perspective of the phone lying down
        if(x > 0) {
            leftright.setText("Left");
            if(shouldPling) {
                try {
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                    r.play();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                shouldPling = false;
            }
        } else if (x < 0) {
            leftright.setText("Right");
            shouldPling = true;
        } else {
            leftright.setText("");
            shouldPling = true;
        }
        if (y > 0) {
            backwardsforwards.setText("Backwards");
        } else if (y < 0) {
            backwardsforwards.setText("Forwards");
        } else {
            backwardsforwards.setText("");
        }
        if (z > 0) {
            downup.setText("Down");
        } else if (z < 0) {
            downup.setText("Up");
        } else {
            downup.setText("");
        }
    }

    public void lowPass(float[] input, float[] output) {
        for ( int i=0; i < input.length; i++ ) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
    }
}