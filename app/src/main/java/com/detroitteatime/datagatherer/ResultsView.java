package com.detroitteatime.datagatherer;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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


}

