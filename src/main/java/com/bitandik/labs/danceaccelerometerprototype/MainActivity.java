package com.bitandik.labs.danceaccelerometerprototype;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity
                          implements SensorEventListener {

    private DatabaseReference databaseReference;
    private FirebaseDatabase database;

    private HashMap<String, Float> mAccelerometerValues;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    @BindView(R.id.txtStatus) TextView txtStatus;
    @BindView(R.id.btnStart) Button btnStart;
    @BindView(R.id.btnStop) Button btnStop;

    private final static String X_VALUE = "x";
    private final static String Y_VALUE = "y";
    private final static String Z_VALUE = "z";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        btnStop.setEnabled(false);

        database = FirebaseDatabase.getInstance();
        mAccelerometerValues = new HashMap<String, Float>();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @OnClick(R.id.btnStart)
    public void starSession(){
        btnStart.setEnabled(false);
        btnStop.setEnabled(true);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        databaseReference = database.getReference().push();
    }

    @OnClick(R.id.btnStop)
    public void stopSession(){
        btnStart.setEnabled(true);
        btnStop.setEnabled(false);
        mSensorManager.unregisterListener(this);
        databaseReference = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        DatabaseReference connectedRef = database.getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    txtStatus.setText(R.string.main_message_online);
                    txtStatus.setTextColor(Color.GREEN);
                } else {
                    txtStatus.setText(R.string.main_message_offline);
                    txtStatus.setTextColor(Color.RED);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        boolean newValue = false;
        if (mAccelerometerValues.get(X_VALUE) != null) {
            float currentValue = mAccelerometerValues.get(X_VALUE);
            if (currentValue != sensorEvent.values[0]) {
                mAccelerometerValues.put(X_VALUE, sensorEvent.values[0]);
                newValue = true;
            }
        } else {
            mAccelerometerValues.put(X_VALUE, sensorEvent.values[0]);
            newValue = true;
        }

        if (mAccelerometerValues.get(Y_VALUE) != null) {
            float currentValue = mAccelerometerValues.get(Y_VALUE);
            if (currentValue != sensorEvent.values[1]) {
                mAccelerometerValues.put(Y_VALUE, sensorEvent.values[1]);
                newValue = true;
            }
        } else {
            mAccelerometerValues.put(Y_VALUE, sensorEvent.values[1]);
            newValue = true;
        }

        if (mAccelerometerValues.get(Z_VALUE) != null) {
            float currentValue = mAccelerometerValues.get(Z_VALUE);
            if (currentValue != sensorEvent.values[2]) {
                mAccelerometerValues.put(Z_VALUE, sensorEvent.values[2]);
                newValue = true;
            }
        } else {
            mAccelerometerValues.put(Z_VALUE, sensorEvent.values[2]);
            newValue = true;
        }

        if (databaseReference != null && newValue) {
            databaseReference.push().setValue(mAccelerometerValues);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
