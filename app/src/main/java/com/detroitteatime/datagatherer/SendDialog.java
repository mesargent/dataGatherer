package com.detroitteatime.datagatherer;

/**
 * Created by mark on 10/29/15.
 */
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.datagatherer.R;

import java.io.File;

public class SendDialog extends Activity {


    private Button send, cancel;
    private int task = 0;
    private ProgressBar progress;
    private String filename, subject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send);
        progress = (ProgressBar) findViewById(R.id.progressBar);

        Intent intent = getIntent();
        filename = intent.getStringExtra("path");
        subject = intent.getStringExtra("subject");
        send = (Button) findViewById(R.id.send);
        cancel = (Button) findViewById(R.id.cancel);

        final File myDir = new File(filename);

        send.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                final Intent emailIntent = new Intent(Intent.ACTION_SEND);
                /* Fill it with Data */
                emailIntent.setType("plain/text");
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Attached is your Data Gatherer file.");
                emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(myDir));
                /* Send it off to the Activity-Chooser */
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));

            }
        });

        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

    }
}