package com.detroitteatime.datagatherer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.datagatherer.R;



public class ResultsView extends ActionBarActivity {
    private long predictorId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results);

        predictorId = getIntent().getLongExtra("predictorId", -1);
        Predictor predictor = DataBaseHelper.getInstance(this).getPredictorById(predictorId);

        TextView tv = (TextView)findViewById(R.id.name);
        tv.setText(predictor.getModel());

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


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
                DataBaseHelper dbHelper = DataBaseHelper.getInstance(this);
                dbHelper = DataBaseHelper.getInstance(this);
                dbHelper.getWritableDatabase().execSQL("delete from " + DataBaseHelper.SENSOR_TABLE_NAME);

                return true;
            case R.id.change_predictor:
                Intent intent = new Intent(ResultsView.this, ModelList.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}

