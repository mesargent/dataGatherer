package com.detroitteatime.datagatherer;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.datagatherer.R;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    private ToggleButton start;
    private ToggleButton label;
    private Button process, save;
    private TextView modelName, modelClass, modelMethod;

    private String format = "%.5f";

    private SensorService mBoundService;
    private boolean mBound, positive;

    private TextView title,
            xAcc, yAcc, zAcc,
            xGyro, yGyro, zGyro,
            xMag, yMag, zMag,
            deltaVal, sRateVal;


    private ResponseReceiver receiver;
    private DataBaseHelper dbHelper;
    private List<DataSet> dataArray;
    private long predictorId;
    private Predictor predictor;
    private SeekBar deltaBar, samplingBar;
    private int samplingRate = 200;
    private int delta;


    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Bind service if already running
        if (SensorService.isStarted) {
            bindService(new Intent(MainActivity.this,
                    SensorService.class), mConnection, BIND_AUTO_CREATE);
            mBound = true;

        }


        IntentFilter mStatusIntentFilter = new IntentFilter(
                Constants.BROADCAST_SENSOR_DATA);
        receiver = new ResponseReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, mStatusIntentFilter);

        DataBaseHelper helper = DataBaseHelper.getInstance(this);

        //Get the Predictor row id
        predictorId = this.getIntent().getLongExtra(DataBaseHelper.ID, 0);

        //Instantiate the Predictor
        predictor = helper.getPredictorById(predictorId);

        //Get all the textviews
        title = (TextView) findViewById(R.id.title_prompt);
        title.setText("Name: " + predictor.getName() + " Class: " + predictor.getCategory() + " Method: " + predictor.getMethod());

        xAcc = (TextView) findViewById(R.id.accelX);
        yAcc = (TextView) findViewById(R.id.accelY);
        zAcc = (TextView) findViewById(R.id.accelZ);
        xGyro = (TextView) findViewById(R.id.gyroX);
        yGyro = (TextView) findViewById(R.id.gyroY);
        zGyro = (TextView) findViewById(R.id.gyroZ);
        xMag = (TextView) findViewById(R.id.magX);
        yMag = (TextView) findViewById(R.id.magY);
        zMag = (TextView) findViewById(R.id.magZ);

        sRateVal = (TextView) findViewById(R.id.sample_rate_display);
        deltaVal = (TextView) findViewById(R.id.delta_display);

        //Get seekbars
        deltaBar = (SeekBar) findViewById(R.id.delta_value);
        samplingBar = (SeekBar) findViewById(R.id.sample_rate);

        deltaBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                delta = i;
                deltaVal.setText(String.valueOf(i) + " readings");
                mBoundService.setDelta(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        samplingBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                samplingRate = 200 + i * 50;
                sRateVal.setText(samplingRate + " ms");
                if(mBound){
                    mBoundService.setFreq(samplingRate);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        start = (ToggleButton) findViewById(R.id.start);

        if (SensorService.isStarted) start.setChecked(true);

        start.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    Intent intent = new Intent(buttonView.getContext(),
                            SensorService.class);

                    startService(intent);
                    bindService(new Intent(MainActivity.this,
                            SensorService.class), mConnection, BIND_AUTO_CREATE);
                    mBound = true;

                    dataArray = new ArrayList<>();


                } else {
                    stopService(new Intent(MainActivity.this,
                            SensorService.class));

                    if (mBound) {
                        mBoundService.disableSensor();
                        unbindService(mConnection);
                        stopService(new Intent(MainActivity.this,
                                SensorService.class));
                        mBound = false;
                    }
                }
            }
        });

        label = (ToggleButton) findViewById(R.id.label);
        label.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mBoundService == null)
                    Toast.makeText(buttonView.getContext(), "No running services", Toast.LENGTH_LONG).show();
                else if (isChecked) {
                    positive = true;
                } else {
                    positive = false;
                }
            }
        });

        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        dbHelper = (dbHelper == null) ? DataBaseHelper.getInstance(MainActivity.this) : dbHelper;
                        dbHelper.insertDataArray(dataArray);
                        dataArray.clear();

                    }
                }).start();
            }
        });

        process = (Button) findViewById(R.id.process);
        process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendJSONTask task = new SendJSONTask();
                task.execute();

            }
        });

        //get delta value

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.save_csv:
                DataAccess.saveToCSVFile(this);
                return true;
            case R.id.delete_csv:
                DataAccess.deleteCSV();
                return true;
            case R.id.clear_db:
                dbHelper = DataBaseHelper.getInstance(this);
                dbHelper.getWritableDatabase().execSQL("delete from " + DataBaseHelper.SENSOR_TABLE_NAME);

                return true;
            case R.id.change_predictor:
                Intent intent = new Intent(MainActivity.this, ModelList.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mBoundService = ((SensorService.LocalBinder) service).getService();
            mBoundService.setFreq(samplingRate);
            mBoundService.setHostingActivityRunning(true);
            dataArray = mBoundService.getDataArray();
        }

        public void onServiceDisconnected(ComponentName className) {
            mBoundService.setHostingActivityRunning(false);
            mBoundService = null;
        }
    };


    // Broadcast receiver for receiving status updates from the IntentService
    private class ResponseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            DataSet data = (DataSet) intent.getSerializableExtra(Constants.DATA);
            DataSet data2 = SerializationUtils.clone(data);
            data.setPositive(positive);
            Log.i("My Code", "Receieved value positive: " + data.isPositive());

            xAcc.setText(String.format(format, data.getAccelX()));
            yAcc.setText(String.format(format, data.getAccelY()));
            zAcc.setText(String.format(format, data.getAccelZ()));

            xGyro.setText(String.format(format, data.getGyroX()));
            yGyro.setText(String.format(format, data.getGyroY()));
            zGyro.setText(String.format(format, data.getGyroZ()));

            xMag.setText(String.format(format, data.getMagX()));
            yMag.setText(String.format(format, data.getMagY()));
            zMag.setText(String.format(format, data.getMagZ()));

            Log.i("My Code", "Received Object ref: " + data.toString());

        }
    }



    ///////////////////////////////////////////////////////////////////////////////////////////////
    class SendJSONTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            DataBaseHelper helper = DataBaseHelper.getInstance(MainActivity.this);

            Cursor cursor = helper.getData();

            JSONArray jArray = DataAccess.cursorToJSON(cursor);


            JSONObject bundle = new JSONObject();
            try {
                bundle.put("data", jArray);
                if(predictor.getName()!=null)
                bundle.put("name", predictor.getName());
                if(predictor.getCategory()!=null)
                bundle.put("class", predictor.getCategory());
                bundle.put("method", predictor.getMethod());
                bundle.put("params", predictor.getParameterString());
                bundle.put("delta", delta);


            } catch (JSONException e) {
                e.printStackTrace();
            }


            HttpClient httpClient = new DefaultHttpClient();
            HttpContext httpContext = new BasicHttpContext();

            HttpPost httpPost = new HttpPost("http://192.168.1.4:8000/classify/logistic_regression");

            try {

                StringEntity se = new StringEntity(bundle.toString());

                httpPost.setEntity(se);
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");

                HttpResponse response = httpClient.execute(httpPost, httpContext); //execute your request and parse response
                HttpEntity entity = response.getEntity();

                String jsonString = EntityUtils.toString(entity); //if response in JSON format
                Log.i("json", "returned: " + jsonString);
                predictor.setModel(jsonString);
                helper.persistPredictor(predictor);


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}

