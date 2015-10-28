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
import android.widget.FrameLayout;
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

    private TextView
            xAcc, yAcc, zAcc,
            xGyro, yGyro, zGyro,
            xMag, yMag, zMag,
            speed, predictorInfo;

    private FrameLayout predictionDisplay;

    private ResponseReceiver receiver;
    private DataBaseHelper dbHelper;
    private List<DataSet> dataArray;
    private long predictorId;
    private Predictor predictor;
    private boolean isDisplayingPredictions;



    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IntentFilter mStatusIntentFilter = new IntentFilter(
                Constants.BROADCAST_SENSOR_DATA);
        receiver = new ResponseReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, mStatusIntentFilter);

        DataBaseHelper helper = new DataBaseHelper(MainActivity.this);
        //Get the Predictor row id
        predictorId = this.getIntent().getLongExtra(DataBaseHelper.ID, 0);

        //Instantiate the Predictor
        predictor = helper.getPredictorById(predictorId);


        //Get all the textviews
        predictorInfo = (TextView)findViewById(R.id.predictorInfo);
        xAcc = (TextView) findViewById(R.id.accelX);
        yAcc = (TextView) findViewById(R.id.accelY);
        zAcc = (TextView) findViewById(R.id.accelZ);
        xGyro = (TextView) findViewById(R.id.gyroX);
        yGyro = (TextView) findViewById(R.id.gyroY);
        zGyro = (TextView) findViewById(R.id.gyroZ);
        xMag = (TextView) findViewById(R.id.magX);
        yMag = (TextView) findViewById(R.id.magY);
        zMag = (TextView) findViewById(R.id.magZ);
        speed = (TextView) findViewById(R.id.speedView);

        //set predictorInfo
        predictorInfo.setText(String.format("Name: %s, Category: %s, Method: %s",
                predictor.getName(), predictor.getCategory(), predictor.getMethod()));


        start = (ToggleButton) findViewById(R.id.start);
        start.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startService(new Intent(MainActivity.this,
                            SensorService.class));
                    bindService(new Intent(MainActivity.this,
                            SensorService.class), mConnection, BIND_AUTO_CREATE);
                    mBound = true;
                    dataArray = new ArrayList<>();
                    isDisplayingPredictions = false;

                } else {
                    //These operations were making the UI unresponsive
                    new Thread(new Runnable(){
                        @Override
                        public void run() {
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
                    }).start();
                }
            }
        });

        label = (ToggleButton) findViewById(R.id.label);
        label.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mBoundService == null) Toast.makeText(buttonView.getContext(), "No running services", Toast.LENGTH_LONG).show();
                else if (isChecked) {
                    positive = true;
                } else {
                    positive = false;
                }
            }
        });

        save = (Button)findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                new Thread(new Runnable(){
                    @Override
                    public void run() {

                        dbHelper = (dbHelper == null) ? new DataBaseHelper(MainActivity.this): dbHelper;
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

        predictionDisplay = (FrameLayout)findViewById(R.id.predictor_display);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
                deleteDatabase(DataBaseHelper.DB_NAME);
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
        }

        public void onServiceDisconnected(ComponentName className) {
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
            dataArray.add(data2);

            xAcc.setText(String.format(format, data.getAccelX()));
            yAcc.setText(String.format(format, data.getAccelY()));
            zAcc.setText(String.format(format, data.getAccelZ()));

            xGyro.setText(String.format(format, data.getGyroX()));
            yGyro.setText(String.format(format, data.getGyroY()));
            zGyro.setText(String.format(format, data.getGyroZ()));

            xMag.setText(String.format(format, data.getMagX()));
            yMag.setText(String.format(format, data.getMagY()));
            zMag.setText(String.format(format, data.getMagZ()));

            speed.setText(String.format(format, data.getSpeedGPS()));
            Log.i("My Code", "Received Object ref: " + data.toString());

            if(isDisplayingPredictions){
                double[] features = {data.getAccelX(), data.getAccelY(), data.getAccelZ()};
                if(predictor.predict(features))
                    predictionDisplay.setBackgroundColor(getResources().getColor(R.color.green));
                else
                    predictionDisplay.setBackgroundColor(getResources().getColor(R.color.red));
            }
        }
    }

    class SendJSONTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            DataBaseHelper helper = new DataBaseHelper(MainActivity.this);

            helper.open(DataBaseHelper.WRITEABLE);

            Cursor cursor = helper.getData();

            JSONArray jArray = DataAccess.cursorToJSON(cursor);
            helper.close();
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext httpContext = new BasicHttpContext();

            HttpPost httpPost = new HttpPost("http://192.168.1.7:8000/classify/logistic_regression");

            try {

                StringEntity se = new StringEntity(jArray.toString());

                httpPost.setEntity(se);
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");

                HttpResponse response = httpClient.execute(httpPost, httpContext); //execute your request and parse response
                HttpEntity entity = response.getEntity();

                String jsonString = EntityUtils.toString(entity); //if response in JSON format
                Log.i("json", "returned: " + jsonString);
                predictor.setModel(jsonString);
                helper.persistPredictor(predictor);
                isDisplayingPredictions = true;
                helper.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}

