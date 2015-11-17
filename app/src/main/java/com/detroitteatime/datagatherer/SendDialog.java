package com.detroitteatime.datagatherer;

/**
 * Created by mark on 10/29/15.
 */
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.datagatherer.R;

import java.io.File;

public class SendDialog extends Activity {


    private Button send, cancel;
    private int task = 0;
    private ProgressBar progress;
    private String filename;
    private long predictorNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send);
        progress = (ProgressBar) findViewById(R.id.progressBar);

        Intent intent = getIntent();
        filename = intent.getStringExtra("model_file");
        predictorNumber = intent.getLongExtra("model_file_id", 0);


        send = (Button) findViewById(R.id.send);
        send.getBackground().setColorFilter(Color.parseColor("#663033"), PorterDuff.Mode.MULTIPLY);

        cancel = (Button) findViewById(R.id.cancel);
        cancel.getBackground().setColorFilter(Color.parseColor("#663033"), PorterDuff.Mode.MULTIPLY);


        final File myDir = new File(Environment.getExternalStorageDirectory() + "/my_classifier_files/" +predictorNumber+ "/" + filename + ".txt");

        send.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                final Intent emailIntent = new Intent(Intent.ACTION_SEND);

                String subjectString = "Classifier Parameters for " + filename;

                /* Fill it with Data */
                emailIntent.setType("plain/text");
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subjectString);
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Attached is your classifier parameters.");
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

        if (task == 0) {
            send.setText("Save");
        } else {
            send.setText("Send");
        }


    }




}