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
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SensorService extends Service implements SensorEventListener {
    public static boolean isStarted;

    public static boolean isPositive() {
        return positive;
    }

    public static void setPositive(boolean positive) {
        SensorService.positive = positive;
    }

    public static boolean positive;

    private LocationManager manager;
    private Criteria criteria;
    private String provider;
    private Location location;

    private int freq = 500000;
    private boolean hostingActivityRunning;
    private List<DataSet> dataArray;
    private int delta;

    private double lattitude, longitude, speedGps;


    private String timeStamp;
    private long lastBroadcastTime;



    private SensorManager sensorManager;

    private Sensor
            sensorACC,
            sensorGrav,
            sensorLinear,
            sensorMagnetic,
            sensorGyro,
            sensorOrient;

    private PowerManager.WakeLock mWakeLock;
    private PowerManager pm;
    private int wakeLockType = PowerManager.PARTIAL_WAKE_LOCK;
    private PowerManager.WakeLock fWakelock;

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    private SimpleDateFormat s;


    private final IBinder mBinder = new LocalBinder();

    DataSet data;

    public void setDelta(int delta) {
        this.delta = delta;
    }

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
        dataArray = new ArrayList<>();

        s = new SimpleDateFormat(DATE_FORMAT);
        Log.i("My Code", "Service created");
        Toast.makeText(this, "Service created ...", Toast.LENGTH_LONG).show();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        setSensors();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        enableSensor();
        Log.i("My Code", "Service started");
        isStarted = true;

        if (intent != null && intent.hasExtra("sampleRate"))
            freq = intent.getIntExtra("sampleRate", 500) * 1000;


        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

        mWakeLock = pm
                .newWakeLock(
                        (PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP),
                        "bbb");

        mWakeLock.acquire();
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        isStarted = false;

        disableSensor();

        if (mWakeLock != null && mWakeLock.isHeld())
            mWakeLock.release();

        if (fWakelock != null && fWakelock.isHeld())
            fWakelock.release();
    }




    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long time = System.currentTimeMillis();
        //only broadcast if freq amount time has passed; android doesn't always respect this
        //in getting sensor updates

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

        data.setSpeedGPS(speedGps);
        data.setTime(timeStamp);
        data.setLat(lattitude);
        data.setLng(longitude);

        //Log.i("My Code", "Value positive: " + data.isPositive());
        timeStamp = s.format(new Date());

        if (time - lastBroadcastTime >= freq / 1000 && hostingActivityRunning) {

            if(delta > dataArray.size()){
                data.setD_accelX(data.getAccelX() - getDeltaDataSet(delta, dataArray).getAccelX());
                data.setD_accelY(data.getAccelY() - getDeltaDataSet(delta, dataArray).getAccelY());
                data.setD_accelZ(data.getAccelZ() - getDeltaDataSet(delta, dataArray).getAccelZ());

                data.setD_gyroX(data.getGyroX() - getDeltaDataSet(delta, dataArray).getGyroX());
                data.setD_gyroY(data.getGyroY() - getDeltaDataSet(delta, dataArray).getGyroY());
                data.setD_gyroZ(data.getGyroZ() - getDeltaDataSet(delta, dataArray).getGyroZ());

                data.setD_magX(data.getMagX() - getDeltaDataSet(delta, dataArray).getMagX());
                data.setD_magY(data.getMagY() - getDeltaDataSet(delta, dataArray).getMagY());
                data.setD_magZ(data.getMagZ() - getDeltaDataSet(delta, dataArray).getMagZ());

            }
            data.setPositive(positive);

            try {
                dataArray.add((DataSet)data.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            lastBroadcastTime = time;
            //Log.i("My Code", "Sensor data from service "+ data.toString());
            Intent localIntent = new Intent(Constants.BROADCAST_SENSOR_DATA).putExtra(Constants.DATA, data);
            LocalBroadcastManager.getInstance(SensorService.this).sendBroadcast(localIntent);

        }

    }

    public DataSet getDeltaDataSet(int delta, List<DataSet> dataArray){
        return dataArray.get(dataArray.size() - delta);
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

    public int getFreq() {
        return freq;
    }

    public List<DataSet> getDataArray() {
        return dataArray;
    }

    public void setDataArray(List<DataSet> dataArray) {
        this.dataArray = dataArray;
    }

    public void setFreq(int freq) {
        this.freq = freq*1000;
    }

    public static boolean isStarted() {
        return isStarted;
    }

    public static void setIsStarted(boolean isStarted) {
        SensorService.isStarted = isStarted;
    }

    public boolean isHostingActivityRunning() {
        return hostingActivityRunning;
    }

    public void setHostingActivityRunning(boolean hostingActivityRunning) {
        this.hostingActivityRunning = hostingActivityRunning;
    }

}
