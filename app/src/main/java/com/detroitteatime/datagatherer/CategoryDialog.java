package com.detroitteatime.datagatherer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.example.datagatherer.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CategoryDialog extends Activity {
    protected Button enter, delete;
    protected EditText name, category;
    protected RadioGroup methodGroup;
    protected CheckBox cp01, cp1, c1, c10, c100, xAcc, yAcc, zAcc,
            xGyro, yGyro, zGyro, xMag, yMag, zMag, dxAcc, dyAcc, dzAcc,
            dxGyro, dyGyro, dzGyro, dxMag, dyMag, dzMag;
    private long id = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_dialog);
        methodGroup = (RadioGroup) findViewById(R.id.method_group);
        name = (EditText) findViewById(R.id.name);
        category = (EditText) findViewById(R.id.category);
        enter = (Button) findViewById(R.id.enter_category);
        delete = (Button) findViewById(R.id.delete_category);
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

        xMag = (CheckBox) findViewById(R.id.x_mag);
        yMag = (CheckBox) findViewById(R.id.y_mag);
        zMag = (CheckBox) findViewById(R.id.z_mag);

        dxAcc = (CheckBox) findViewById(R.id.dx_accel);
        dyAcc = (CheckBox) findViewById(R.id.dy_accel);
        dzAcc = (CheckBox) findViewById(R.id.dz_accel);

        dxGyro = (CheckBox) findViewById(R.id.dx_gyro);
        dyGyro = (CheckBox) findViewById(R.id.dy_gyro);
        dzGyro = (CheckBox) findViewById(R.id.dz_gyro);

        dxMag = (CheckBox) findViewById(R.id.dx_mag);
        dyMag = (CheckBox) findViewById(R.id.dy_mag);
        dzMag = (CheckBox) findViewById(R.id.dz_mag);


        //Get predictor's id number
        Intent intent = getIntent();
        id = intent.getLongExtra("id", -1);

        if(id==-1){
            delete.setVisibility(View.GONE);

            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            p.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

            enter.setLayoutParams(p);

        }

        //Check if editing as opposed to creating --- id will be -1 if creating and
        //no id was passed to this activity
        if(id !=-1){
            DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(this);

            Predictor predictor = dataBaseHelper.getPredictorById(id);

            name.setText(predictor.getName());

            category.setText(predictor.getCategory());
            String params = predictor.getParameterString();
            try {
                JSONObject obj = new JSONObject(params);

                JSONArray sArray = obj.getJSONArray("sensor_array");
                JSONArray cArray = obj.getJSONArray("C_array");

                cp01.setChecked(cArray.toString().contains(".01"));
                cp1.setChecked(cArray.toString().contains(".1"));
                c1.setChecked(cArray.toString().contains("1,"));
                c10.setChecked(cArray.toString().contains("10,"));
                c100.setChecked(cArray.toString().contains("100,"));

                xAcc.setChecked(sArray.toString().contains(DataBaseHelper.ACCELX));
                yAcc.setChecked(sArray.toString().contains(DataBaseHelper.ACCELY));
                zAcc.setChecked(sArray.toString().contains(DataBaseHelper.ACCELZ));
                xGyro.setChecked(sArray.toString().contains(DataBaseHelper.GYROX));
                yGyro.setChecked(sArray.toString().contains(DataBaseHelper.GYROY));
                zGyro.setChecked(sArray.toString().contains(DataBaseHelper.GYROZ));
                xMag.setChecked(sArray.toString().contains(DataBaseHelper.MAGNETICX));
                yMag.setChecked(sArray.toString().contains(DataBaseHelper.MAGNETICY));
                zMag.setChecked(sArray.toString().contains(DataBaseHelper.MAGNETICZ));

                dxAcc.setChecked(sArray.toString().contains(DataBaseHelper.D_ACCELX));
                dyAcc.setChecked(sArray.toString().contains(DataBaseHelper.D_ACCELY));
                dzAcc.setChecked(sArray.toString().contains(DataBaseHelper.D_ACCELZ));
                dxGyro.setChecked(sArray.toString().contains(DataBaseHelper.D_GYROX));
                dyGyro.setChecked(sArray.toString().contains(DataBaseHelper.D_GYROY));
                dzGyro.setChecked(sArray.toString().contains(DataBaseHelper.D_GYROZ));
                dxMag.setChecked(sArray.toString().contains(DataBaseHelper.D_MAGNETICX));
                dyMag.setChecked(sArray.toString().contains(DataBaseHelper.D_MAGNETICY));
                dzMag.setChecked(sArray.toString().contains(DataBaseHelper.D_MAGNETICZ));



                switch (predictor.getMethod()){
                    case Constants.LOGISIC_REGRESSION:
                        methodGroup.check(R.id.log_reg);
                        break;

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


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

                if (xMag.isChecked()) {
                    sensors.add(DataBaseHelper.MAGNETICX);
                }
                if (yMag.isChecked()) {
                    sensors.add(DataBaseHelper.MAGNETICY);
                }
                if (zMag.isChecked()) {
                    sensors.add(DataBaseHelper.MAGNETICZ);
                }

                if (dxAcc.isChecked()) {
                    sensors.add(DataBaseHelper.D_ACCELX);
                }
                if (dyAcc.isChecked()) {
                    sensors.add(DataBaseHelper.D_ACCELY);
                }
                if (dzAcc.isChecked()) {
                    sensors.add(DataBaseHelper.D_ACCELZ);
                }

                if (dxGyro.isChecked()) {
                    sensors.add(DataBaseHelper.D_GYROX);
                }
                if (dyGyro.isChecked()) {
                    sensors.add(DataBaseHelper.D_GYROY);
                }
                if (dzGyro.isChecked()) {
                    sensors.add(DataBaseHelper.D_GYROZ);
                }

                if (dxMag.isChecked()) {
                    sensors.add(DataBaseHelper.D_MAGNETICX);
                }
                if (dyMag.isChecked()) {
                    sensors.add(DataBaseHelper.D_MAGNETICY);
                }
                if (dzMag.isChecked()) {
                    sensors.add(DataBaseHelper.D_MAGNETICZ);
                }

                JSONObject params = DataAccess.makeParamJSON(cParams, sensors);

                DataBaseHelper helper = DataBaseHelper.getInstance(CategoryDialog.this);

                if(id!=-1){
                    helper.editPredictor(id, name.getText().toString(), category.getText().toString(), method, params.toString(), null, null);
                }else{

                    helper.insertModel(name.getText().toString(), category.getText().toString(), method, params.toString(), null, null);
                }

                Intent intent = new Intent(CategoryDialog.this, ModelList.class);
                startActivity(intent);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataBaseHelper helper = DataBaseHelper.getInstance(CategoryDialog.this);

                helper.deletePredictor(id);

                Intent intent = new Intent(CategoryDialog.this, ModelList.class);
                startActivity(intent);
            }
        });
    }
}