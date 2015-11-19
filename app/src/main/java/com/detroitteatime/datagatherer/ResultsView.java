package com.detroitteatime.datagatherer;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.webkit.WebView;

import com.example.datagatherer.R;



public class ResultsView extends ActionBarActivity {
    private long predictorId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webresult);

        predictorId = getIntent().getLongExtra("predictorId", -1);
        Predictor predictor = DataBaseHelper.getInstance(this).getPredictorById(predictorId);
        String results = getIntent().getStringExtra("results");

        WebView wv = (WebView)findViewById(R.id.result_view);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.loadData(results, "text/html", "utf-8");




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

