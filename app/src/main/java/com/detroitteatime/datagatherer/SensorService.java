package com.detroitteatime.datagatherer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SensorService extends Service implements LocationListener, SensorEventListener {

    private LocationManager manager;
    private Criteria criteria;
    private String provider;
    private Location location;
    private int freq = 200000;

    private double lattitude;
    private double longitude;
    private double speedGps;

    private String timeStamp;

    private SensorManager sensorManager;
    private Sensor sensorACC;
    private Sensor sensorGrav;
    private Sensor sensorLinear;
    private Sensor sensorMagnetic;
    private Sensor sensorGyro;
    private Sensor sensorOrient;

    private PowerManager.WakeLock mWakeLock;
    private PowerManager pm;
    private int wakeLockType = PowerManager.PARTIAL_WAKE_LOCK;
    private PowerManager.WakeLock fWakelock;

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    private SimpleDateFormat s;


    private final IBinder mBinder = new LocalBinder();

    DataSet data;

    public class LocalBinder extends Binder {
        SensorService getService() {
            return SensorService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        data = new DataSet();

        s = new SimpleDateFormat(DATE_FORMAT);
        Log.i("My Code", "Service created");
        Toast.makeText(this, "Service created ...", Toast.LENGTH_LONG).show();

        //manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        //criteria = new Criteria();

//        criteria.setAccuracy(Criteria.ACCURACY_FINE);
//        criteria.setAltitudeRequired(false);
//        criteria.setBearingRequired(false);
//        criteria.setCostAllowed(false);
//
//        provider = manager.getBestProvider(criteria, false);
//        location = manager.getLastKnownLocation(provider);

        // Initialize the location fields
        if (location != null) {
            onLocationChanged(location);
        }

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        setSensors();

    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stubdata.setSpeedGPS(speedGps);
        data.setTime(timeStamp);
        data.setLat(lattitude);
        data.setLng(longitude);
        super.onDestroy();

            if (manager != null) {
                manager.removeUpdates(this);
            }

            disableSensor();

            if (mWakeLock != null && mWakeLock.isHeld())
                mWakeLock.release();

            if (fWakelock != null && fWakelock.isHeld())
                fWakelock.release();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        manager.requestLocationUpdates(provider, 500, 1, this);
        enableSensor();
        Log.i("My Code", "Service started");


        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

        mWakeLock = pm
                .newWakeLock(
                        (PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP),
                        "bbb");

        mWakeLock.acquire();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor source = event.sensor;

        if (source.equals(sensorACC)) {

            data.setAccelX(event.values[0]);
            data.setAccelY(event.values[1]);
            data.setAccelZ(event.values[2]);
            Log.i("My Code", "Value accelx: " + event.values[0]);

        } else if (source.equals(sensorGrav)) {
            data.setGravX(event.values[0]);
            data.setGravY(event.values[1]);
            data.setGravZ(event.values[2]);


        } else if (source.equals(sensorLinear)) {
            data.setLinX(event.values[0]);
            data.setLinY(event.values[1]);
            data.setLinZ(event.values[2]);


        } else if (source.equals(sensorMagnetic)) {
            data.setMagX(event.values[0]);
            data.setMagY(event.values[1]);
            data.setMagZ(event.values[2]);

        } else if (source.equals(sensorGyro)) {
            data.setGyroX(event.values[0]);
            data.setGyroY(event.values[1]);
            data.setGyroZ(event.values[2]);

        } else if (source.equals(sensorOrient)) {
            data.setOrientX(event.values[0]);
            data.setOrientY(event.values[1]);
            data.setOrientZ(event.values[2]);

        }

        //Log.i("My Code", "Value positive: " + data.isPositive());
        timeStamp = s.format(new Date());

        data.setSpeedGPS(speedGps);
        data.setTime(timeStamp);
        data.setLat(lattitude);
        data.setLng(longitude);

        Intent localIntent = new Intent(Constants.BROADCAST_SENSOR_DATA).putExtra(Constants.DATA, data);
        LocalBroadcastManager.getInstance(SensorService.this).sendBroadcast(localIntent);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("My Code", "Location changed ");
        if (location != null) {

            lattitude = location.getLatitude();
            longitude = location.getLongitude();


            if (location.hasSpeed())
                speedGps = location.getSpeed();
            else
                speedGps = -1000;

        }

        timeStamp = s.format(new Date());

    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }


    public void enableSensor() {
        sensorManager.registerListener(this, sensorACC, freq);
        sensorManager.registerListener(this, sensorGrav, freq);
        sensorManager.registerListener(this, sensorMagnetic, freq);
        sensorManager.registerListener(this, sensorLinear, freq);
        sensorManager.registerListener(this, sensorGyro, freq);
        sensorManager.registerListener(this, sensorOrient, freq);
    }

    public void resetSensors() {
        sensorManager.unregisterListener(this);
        enableSensor();
        Log.i("My Code", "Reset sensors");

    }

    public void disableSensor() {
        sensorManager.unregisterListener(this);
    }

    public void setSensors() {

        if (sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0) {
            sensorACC = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


        } else {
            Toast.makeText(this, "You don't have an accelerometer", Toast.LENGTH_SHORT).show();
        }

        if (sensorManager.getSensorList(Sensor.TYPE_GRAVITY).size() != 0) {
            sensorGrav = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        } else {
            Toast.makeText(this, "You don't have an gravity sensor", Toast.LENGTH_SHORT).show();
        }

        if (sensorManager.getSensorList(Sensor.TYPE_LINEAR_ACCELERATION).size() != 0) {
            sensorLinear = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        } else {
            Toast.makeText(this, "You don't have an linear acceleration sensor", Toast.LENGTH_SHORT).show();
        }

        if (sensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD).size() != 0) {
            sensorMagnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        } else {
            Toast.makeText(this, "You don't have an magnetic field sensor", Toast.LENGTH_SHORT).show();
        }

        if (sensorManager.getSensorList(Sensor.TYPE_GYROSCOPE).size() != 0) {
            sensorGyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        } else {
            Toast.makeText(this, "You don't have a gyroscope", Toast.LENGTH_SHORT).show();
        }

        if (sensorManager.getSensorList(Sensor.TYPE_ORIENTATION).size() != 0) {
            sensorOrient = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        } else {
            Toast.makeText(this, "You don't have a oreination sensor", Toast.LENGTH_SHORT).show();
        }

    }


}
