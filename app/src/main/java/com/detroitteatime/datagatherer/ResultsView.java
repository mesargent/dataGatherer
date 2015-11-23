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

        String results = getIntent().getStringExtra("results");
        if(!results.contains("Process data first."))
            results = results.split("8", 2)[1];

        WebView wv = (WebView)findViewById(R.id.result_view);
        wv.loadData(results, "text/html", "utf-8");

    }

}

