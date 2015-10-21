package com.detroitteatime.datagatherer;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class DataAccess {
    public static final String dir = "SpeedData";

    public static JSONArray cursorToJSON(Cursor cursor) {
        JSONArray resultSet = new JSONArray();
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();
            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        rowObject.put(cursor.getColumnName(i),
                                cursor.getString(i));
                    } catch (Exception e) {
                        Log.d("data", e.getMessage());
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }

        cursor.close();
        return resultSet;
    }

    public static void saveToCSVFile(Context context) {

        DataBaseHelper helper = new DataBaseHelper(context);
        helper.open(DataBaseHelper.READABLE);

        Cursor cursor = helper.getData();
        BufferedWriter writer = null;

        String fileName = DateFormat.getDateTimeInstance().format(new Date()) + ".csv";

        File myDir = new File(Environment.getExternalStorageDirectory(), dir);
        myDir.mkdirs();

        File file = new File(myDir.getAbsolutePath(), fileName);


        try {
            writer = new BufferedWriter(new FileWriter(file));

            writer.write("Id");
            writer.write(",");
            writer.write("Speed");
            writer.write(",");

            writer.write("AccX");
            writer.write(",");
            writer.write("AccY");
            writer.write(",");
            writer.write("AccZ");
            writer.write(",");

            writer.write("LinAccX");
            writer.write(",");
            writer.write("LinAccY");
            writer.write(",");
            writer.write("LinAccZ");
            writer.write(",");

            writer.write("GravityX");
            writer.write(",");
            writer.write("GravityY");
            writer.write(",");
            writer.write("GravityZ");
            writer.write(",");

            writer.write("MagneticX");
            writer.write(",");
            writer.write("MagneticY");
            writer.write(",");
            writer.write("MagneticZ");
            writer.write(",");

            writer.write("GyroscopeX");
            writer.write(",");
            writer.write("GyroscopeY");
            writer.write(",");
            writer.write("GyroscopeZ");
            writer.write(",");

            writer.write("OrientationX");
            writer.write(",");
            writer.write("OrientationY");
            writer.write(",");
            writer.write("OrientationZ");
            writer.write(",");

            writer.write("Lat");
            writer.write(",");
            writer.write("Long");
            writer.write(",");
            writer.write("Time");
            writer.write(",");
            writer.write("");
            writer.write('\n');

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex("_id"));
                String speed = cursor.getString(cursor.getColumnIndex(DataBaseHelper.SPEED_GPS));
                String ax = cursor.getString(cursor.getColumnIndex(DataBaseHelper.ACCELX));
                String ay = cursor.getString(cursor.getColumnIndex(DataBaseHelper.ACCELY));
                String az = cursor.getString(cursor.getColumnIndex(DataBaseHelper.ACCELZ));

                String lx = cursor.getString(cursor.getColumnIndex(DataBaseHelper.LINEARX));
                String ly = cursor.getString(cursor.getColumnIndex(DataBaseHelper.LINEARY));
                String lz = cursor.getString(cursor.getColumnIndex(DataBaseHelper.LINEARZ));

                String gx = cursor.getString(cursor.getColumnIndex(DataBaseHelper.GRAVITYX));
                String gy = cursor.getString(cursor.getColumnIndex(DataBaseHelper.GRAVITYY));
                String gz = cursor.getString(cursor.getColumnIndex(DataBaseHelper.GRAVITYZ));

                String mx = cursor.getString(cursor.getColumnIndex(DataBaseHelper.MAGNETICX));
                String my = cursor.getString(cursor.getColumnIndex(DataBaseHelper.MAGNETICY));
                String mz = cursor.getString(cursor.getColumnIndex(DataBaseHelper.MAGNETICZ));

                String gyrx = cursor.getString(cursor.getColumnIndex(DataBaseHelper.GYROX));
                String gyry = cursor.getString(cursor.getColumnIndex(DataBaseHelper.GYROY));
                String gyrz = cursor.getString(cursor.getColumnIndex(DataBaseHelper.GYROZ));

                String ox = cursor.getString(cursor.getColumnIndex(DataBaseHelper.ORIENTX));
                String oy = cursor.getString(cursor.getColumnIndex(DataBaseHelper.ORIENTY));
                String oz = cursor.getString(cursor.getColumnIndex(DataBaseHelper.ORIENTZ));

                String lat = cursor.getString(cursor.getColumnIndex(DataBaseHelper.LATTITUDE));
                String lng = cursor.getString(cursor.getColumnIndex(DataBaseHelper.LONGITUDE));
                String time = cursor.getString(cursor.getColumnIndex(DataBaseHelper.TIME));
                String positive = cursor.getString(cursor.getColumnIndex(DataBaseHelper.POSITIVE));

                if (positive == null) {
                    positive = "";
                }


                writer.write(String.valueOf(id));
                writer.write(",");
                writer.write(speed);
                writer.write(",");

                writer.write(ax);
                writer.write(",");
                writer.write(ay);
                writer.write(",");
                writer.write(az);
                writer.write(",");

                writer.write(lx);
                writer.write(",");
                writer.write(ly);
                writer.write(",");
                writer.write(lz);
                writer.write(",");

                writer.write(gx);
                writer.write(",");
                writer.write(gy);
                writer.write(",");
                writer.write(gz);
                writer.write(",");

                writer.write(mx);
                writer.write(",");
                writer.write(my);
                writer.write(",");
                writer.write(mz);
                writer.write(",");

                writer.write(gyrx);
                writer.write(",");
                writer.write(gyry);
                writer.write(",");
                writer.write(gyrz);
                writer.write(",");

                writer.write(ox);
                writer.write(",");
                writer.write(oy);
                writer.write(",");
                writer.write(oz);
                writer.write(",");

                writer.write(lat);
                writer.write(",");
                writer.write(lng);
                writer.write(",");
                writer.write(time);
                writer.write(",");
                writer.write(positive);
                writer.write(",");
                writer.write('\n');

            }

            writer.close();


        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e("My Code", "Writing CSV file: " + e.getMessage() + " Make sure your sd card is not full.");
        }

        cursor.close();
        helper.close();

    }

    public static void deleteCSV() {

        File myDir = new File(Environment.getExternalStorageDirectory() + "/" + dir);

        deleteRecursive(myDir);

    }


    public static void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();

    }
}
