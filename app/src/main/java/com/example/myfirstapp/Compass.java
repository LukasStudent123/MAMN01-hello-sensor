package com.example.myfirstapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class Compass extends AppCompatActivity implements SensorEventListener {
    private SensorManager SensorManage;
    private ImageView compassimage;
    private float DegreeStart = 0f;
    private TextView DegreeTV;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private Vibrator v;
    private TextView north_text;
    private View background;
    float[] accelerometerReading = new float[3];
    float[] magnetometerReading = new float[3];
    float[] rotationMatrix = new float[9];
    float[] orientationAngles = new float[3];
    static final float ALPHA = 0.25f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        compassimage = (ImageView) findViewById(R.id.compass_image);
        DegreeTV = (TextView) findViewById(R.id.DegreeTV);
        SensorManage = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = SensorManage.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = SensorManage.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        north_text = (TextView) findViewById(R.id.north_text);
        View view = (View) findViewById(R.id.north_text);
        background = view.getRootView();
    }
    @Override
    protected void onPause() {
        super.onPause();
        // to stop the listener and save battery
        SensorManage.unregisterListener(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        SensorManage.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        SensorManage.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event == null) {
            return;
        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            lowPass(event.values, accelerometerReading);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            lowPass(event.values, magnetometerReading);
        }

        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading);
        float[] orientation = SensorManager.getOrientation(rotationMatrix, orientationAngles);
        float degree = (float) ((Math.toDegrees(orientation[0]) + 360.0) % 360.0);
        long angle = Math.round(degree * 100) / 100;

        if(angle > 345 || angle < 15) {
            // Vibrate for 500 milliseconds
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            background.setBackgroundColor(Color.parseColor("#00ff33"));
            north_text.setText("North!");
        } else {
            background.setBackgroundColor(Color.parseColor("#ffffff"));
            north_text.setText("");
        }

        DegreeTV.setText("Heading: " + Float.toString(angle) + " degrees");
        RotateAnimation ra = new RotateAnimation(
                DegreeStart,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setFillAfter(true);
        ra.setDuration(210);
        compassimage.startAnimation(ra);
        DegreeStart = -degree;
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void lowPass(float[] input, float[] output) {
        for ( int i=0; i < input.length; i++ ) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
    }
}