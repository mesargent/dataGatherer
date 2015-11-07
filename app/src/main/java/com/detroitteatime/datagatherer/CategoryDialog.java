package com.detroitteatime.datagatherer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.datagatherer.R;

import org.json.JSONObject;

import java.util.ArrayList;

public class CategoryDialog extends Activity {
    protected Button enter;
    protected EditText name, category, noPCs;
    protected RadioGroup methodGroup;
    protected CheckBox cp01, cp1, c1, c10, c100, xAcc, yAcc, zAcc,
            xGyro, yGyro, zGyro, pca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_dialog);
        methodGroup = (RadioGroup) findViewById(R.id.method_group);
        name = (EditText) findViewById(R.id.name);
        category = (EditText) findViewById(R.id.category);
        enter = (Button) findViewById(R.id.enter_category);
        cp01 = (CheckBox) findViewById(R.id.cp01);
        cp1 = (CheckBox) findViewById(R.id.cp1);
        c1 = (CheckBox) findViewById(R.id.c1);
        c10 = (CheckBox) findViewById(R.id.c10);
        c100 = (CheckBox) findViewById(R.id.c100);
        xAcc = (CheckBox) findViewById(R.id.x_accel);
        yAcc = (CheckBox) findViewById(R.id.y_accel);
        zAcc = (CheckBox) findViewById(R.id.z_accel);
        xGyro = (CheckBox) findViewById(R.id.x_gyro);
        yGyro = (CheckBox) findViewById(R.id.y_gyro);
        zGyro = (CheckBox) findViewById(R.id.z_gyro);
        pca = (CheckBox) findViewById(R.id.pca);
        noPCs = (EditText) findViewById(R.id.no_pcs);

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = methodGroup.getCheckedRadioButtonId();
                RadioButton selected = (RadioButton) findViewById(selectedId);
                String method = selected.getText().toString();

                switch (method) {
                    case "Logistic Regression":
                        method = Constants.LOGISIC_REGRESSION;
                        break;
                    case "Support Vector Machine":
                        method = Constants.SVM;
                        break;
//                    case "Neural Net":
//                        method = Constants.NEURAL_NET;
//                        break;
                }

                ArrayList<Double> cParams = new ArrayList<Double>();

                if (cp01.isChecked()) {
                    cParams.add(.01);
                }
                if (cp1.isChecked()) {
                    cParams.add(.1);
                }
                if (c1.isChecked()) {
                    cParams.add(1.0);
                }
                if (c10.isChecked()) {
                    cParams.add(10.0);
                }
                if (c100.isChecked()) {
                    cParams.add(100.0);
                }

                ArrayList<String> sensors = new ArrayList<String>();

                if (xAcc.isChecked()) {
                    sensors.add(DataBaseHelper.ACCELX);
                }
                if (yAcc.isChecked()) {
                    sensors.add(DataBaseHelper.ACCELY);
                }
                if (zAcc.isChecked()) {
                    sensors.add(DataBaseHelper.ACCELZ);
                }

                if (xGyro.isChecked()) {
                    sensors.add(DataBaseHelper.GYROX);
                }
                if (yGyro.isChecked()) {
                    sensors.add(DataBaseHelper.GYROY);
                }
                if (zGyro.isChecked()) {
                    sensors.add(DataBaseHelper.GYROZ);
                }

                String pcString = noPCs.getText().toString();


                int pcs = (pcString != null && pcString.matches("^[0-9]*[1-9][0-9]*$")) ?
                        Integer.valueOf(noPCs.getText().toString()) : 0;

                JSONObject params = DataAccess.makeParamJSON(cParams, sensors, pca.isChecked(), pcs);

                DataBaseHelper helper = new DataBaseHelper(CategoryDialog.this);
                helper.insertModel(name.getText().toString(), category.getText().toString(), method, params.toString(), null);
                helper.close();

                Intent intent = new Intent(CategoryDialog.this, ModelList.class);
                startActivity(intent);
            }
        });
    }
}