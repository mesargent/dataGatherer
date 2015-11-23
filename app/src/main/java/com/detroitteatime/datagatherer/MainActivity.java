package com.detroitteatime.datagatherer;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.datagatherer.R;

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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    private ToggleButton start;
    private ToggleButton label;
    private Button process, save, results, cancel;
    private ProgressBar progress;

    private String format = "%.5f";

    private SensorService mBoundService;
    private boolean mBound, positive;

    private TextView title,
            xAcc, yAcc, zAcc,
            xGyro, yGyro, zGyro,
            xMag, yMag, zMag,
            sRateVal;


    private ResponseReceiver receiver;
    private DataBaseHelper dbHelper;

    private long predictorId;
    private Predictor predictor;
    private SeekBar samplingBar;
    private int samplingRate = 200;

    private String jsonString;
    private String html = "Process data first.";

    SendJSONTask task;

    private List<DataSet> tempArray = new ArrayList<>();
    private String path;


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

        path = Environment.getExternalStorageDirectory() + "/my_classifier_files/" + predictor.getId() + "_" + predictor.getName() + "/" + predictor.getName() + ".csv";

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
        //Get seekbars
        sRateVal.setText("10 ms");

        samplingBar = (SeekBar) findViewById(R.id.sample_rate);

        samplingBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                samplingRate = 10 + i * 50;
                sRateVal.setText(samplingRate + " ms");
                if (mBound) {
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


                } else {
                    stopService(new Intent(MainActivity.this,
                            SensorService.class));

                    if (mBound) {
                        tempArray = mBoundService.getDataArray();
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
                positive = (isChecked) ? true : false;
                if (mBoundService == null)
                    Toast.makeText(buttonView.getContext(), "No running services", Toast.LENGTH_LONG).show();
                else mBoundService.setPositive(positive);
            }
        });

        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsyncTask saveTask = new Save().execute();
            }
        });

        process = (Button) findViewById(R.id.process);
        process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                task = new SendJSONTask();
                task.execute();

            }
        });

        results = (Button) findViewById(R.id.view_results);
        results.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ResultsView.class);
                intent.putExtra("predictorId", predictorId);
                intent.putExtra("results", html);
                startActivity(intent);
            }
        });

        progress = (ProgressBar) findViewById(R.id.progressBar);

        if (predictor.getrHhtml() != null) {
            results.setVisibility(View.VISIBLE);
            html = predictor.getrHhtml();
        }

        cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                task.cancel(true);
                process.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
                results.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
            }
        });

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
                AsyncTask make = new MakeCSV().execute();
                return true;

            case R.id.send_csv:
                Intent intent1 = new Intent(MainActivity.this, SendDialog.class);
                intent1.putExtra("path", path);
                intent1.putExtra("subject", "Your Data CSV File for: " + predictor.getName());
                startActivity(intent1);
                return true;

            case R.id.load_csv:
                AsyncTask load = new LoadCSV().execute();
                return true;

            case R.id.delete_csv:
                File csvFile = new File(path);
                DataAccess.deleteRecursive(csvFile);
                return true;

            case R.id.clear_db:
                dbHelper = DataBaseHelper.getInstance(this);
                dbHelper.getWritableDatabase().execSQL("delete from " + DataBaseHelper.SENSOR_TABLE_NAME);
                if (mBoundService != null) {
                    List<DataSet> dataArray = mBoundService.getDataArray();
                    dataArray.clear();
                }
                if (tempArray != null)
                    tempArray.clear();
                return true;

            case R.id.change_predictor:
                Intent intent = new Intent(MainActivity.this, ModelList.class);
                startActivity(intent);
                return true;

            case R.id.send_params:
                Intent intent2 = new Intent(MainActivity.this, SendDialog.class);
                intent2.putExtra("path", path);
                intent2.putExtra("subject", "Your Trained Parameters for: " + predictor.getName());
                startActivity(intent2);
                return true;

            case R.id.delete_params:
                File paramFile = new File(path);
                DataAccess.deleteRecursive(paramFile);
                return true;

            case R.id.folder:
                Toast.makeText(this, "Your data folder location file" + path, Toast.LENGTH_LONG);
                Intent intent3 = new Intent(Intent.ACTION_GET_CONTENT);
                Uri uri = Uri.parse(path);
                intent3.setDataAndType(uri, "text/csv");
                startActivity(Intent.createChooser(intent3, "Open folder"));

                if (intent3.resolveActivityInfo(getPackageManager(), 0) != null)
                {
                    startActivity(intent3);
                }
                else
                {
                     Toast.makeText(this, "You don't have a file choosing app. Try Open Intent's File Manager. ", Toast.LENGTH_LONG).show();
                }
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
            mBoundService.setPositive(positive);
            mBoundService.setDataArray(tempArray);
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

            // Log.i("My Code", "Receieved value positive: " + data.isPositive());

            xAcc.setText(String.format(format, data.getAccelX()));
            yAcc.setText(String.format(format, data.getAccelY()));
            zAcc.setText(String.format(format, data.getAccelZ()));

            xGyro.setText(String.format(format, data.getGyroX()));
            yGyro.setText(String.format(format, data.getGyroY()));
            zGyro.setText(String.format(format, data.getGyroZ()));

            xMag.setText(String.format(format, data.getMagX()));
            yMag.setText(String.format(format, data.getMagY()));
            zMag.setText(String.format(format, data.getMagZ()));

            // Log.i("My Code", "Received Object ref: " + data.toString());

        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    class SendJSONTask extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setVisibility(View.VISIBLE);
            results.setVisibility(View.GONE);
            cancel.setVisibility(View.VISIBLE);
            Log.i("My Code", "JSONASYNCTASK: preExecute " + System.currentTimeMillis());
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progress.setVisibility(View.GONE);
            results.setVisibility(View.VISIBLE);
            cancel.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(String... strings) {

            DataBaseHelper helper = DataBaseHelper.getInstance(MainActivity.this);

            Cursor cursor = helper.getData();
            Log.i("My Code", "JSONASYNCTASK: cursor created " + System.currentTimeMillis());
            JSONArray jArray = DataAccess.cursorToJSON(cursor);
            Log.i("My Code", "JSONASYNCTASK: JSONarray made " + System.currentTimeMillis());
            JSONObject bundle = new JSONObject();
            try {
                bundle.put("data", jArray);
                if (predictor.getName() != null)
                    bundle.put("name", predictor.getName());
                if (predictor.getCategory() != null)
                    bundle.put("class", predictor.getCategory());
                bundle.put("method", predictor.getMethod());
                bundle.put("params", predictor.getParameterString());


            } catch (JSONException e) {
                e.printStackTrace();
            }


            HttpClient httpClient = new DefaultHttpClient();
            HttpContext httpContext = new BasicHttpContext();

            HttpPost httpPost = new HttpPost("http://162.243.28.75/classify/logistic_regression");
            //HttpPost httpPost = new HttpPost("http://192.168.1.4:8000/classify/logistic_regression");

            try {
                StringEntity se = new StringEntity(bundle.toString());

                httpPost.setEntity(se);
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");

                HttpResponse response = httpClient.execute(httpPost, httpContext); //execute your request and parse response
                HttpEntity entity = response.getEntity();

                jsonString = EntityUtils.toString(entity);

                if(jsonString.startsWith("{")){
                    JSONObject reply = new JSONObject(jsonString);
                    Log.i("My Code", "Returned JSON: " + reply.toString());
                    String model = reply.getString("results");
                    html = reply.getString("html");
                    predictor.setModel(model);
                    predictor.setrHhtml(html);
                    helper.editPredictor(predictor);
                }else{
                    html = jsonString;
                }



                if (DataAccess.isExternalStorageWritable()) {
                    File myDir = new File(Environment.getExternalStorageDirectory() + "/my_classifier_files/" + predictor.getId() + "/" + predictor.getName() + ".txt");
                    DataAccess.makeClassifierParametersFile(MainActivity.this, jsonString, myDir);
                } else {
                    Log.e("My Code", "storage not available");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class MakeCSV extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            progress.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(String... strings) {
            File myDir = new File(path);
            DataAccess.saveToCSVFile(MainActivity.this, myDir);
            return null;
        }
    }

    class LoadCSV extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progress.setVisibility(View.GONE);

        }

        @Override
        protected Void doInBackground(String... strings) {
            File file = new File(path);
            if (file.exists()) {
                dbHelper = DataBaseHelper.getInstance(MainActivity.this);
                try {
                    DataAccess.loadCSV(MainActivity.this, file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
    }

    class Save extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progress.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(String... strings) {
            if (mBoundService != null) {

                List<DataSet> dataArray = mBoundService.getDataArray();
                dbHelper = (dbHelper == null) ? DataBaseHelper.getInstance(MainActivity.this) : dbHelper;
                dbHelper.insertDataArray(dataArray);


            }
            return null;
        }
    }
}

