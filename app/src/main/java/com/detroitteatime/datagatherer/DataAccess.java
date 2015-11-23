package com.detroitteatime.datagatherer;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class DataAccess {
    public static final String dir = "SensorData";

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
            Log.i("My Code", "data row: " + rowObject.toString());
            cursor.moveToNext();
        }

        cursor.close();
        return resultSet;
    }

    public static File makeClassifierParametersFile(Context context, String params, File file) {
        file.getParentFile().mkdirs();
//        String path = file.getAbsolutePath();
//        Log.i("My Code", file.getAbsolutePath());
        ///storage/emulated/0/my_classifier_files/1/test1.txt
        BufferedWriter writer = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(params);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static void loadCSV(Context context, File file) throws IOException {
        DataBaseHelper helper = DataBaseHelper.getInstance(context);

        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        DataSet ds = new DataSet();

            br = new BufferedReader(new FileReader(file));
            br.readLine(); //consume header row
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] dataset = line.split(cvsSplitBy);
                //skipping id column
                ds.setAccelX(Double.parseDouble(dataset[1]));
                ds.setAccelY(Double.parseDouble(dataset[2]));
                ds.setAccelZ(Double.parseDouble(dataset[3]));

                ds.setLinX(Double.parseDouble(dataset[4]));
                ds.setLinY(Double.parseDouble(dataset[5]));
                ds.setLinZ(Double.parseDouble(dataset[6]));

                ds.setGravX(Double.parseDouble(dataset[7]));
                ds.setGravY(Double.parseDouble(dataset[8]));
                ds.setGravZ(Double.parseDouble(dataset[9]));

                ds.setMagX(Double.parseDouble(dataset[10]));
                ds.setMagY(Double.parseDouble(dataset[11]));
                ds.setMagZ(Double.parseDouble(dataset[12]));

                ds.setGyroX(Double.parseDouble(dataset[13]));
                ds.setGyroY(Double.parseDouble(dataset[14]));
                ds.setGyroZ(Double.parseDouble(dataset[15]));

                ds.setOrientX(Double.parseDouble(dataset[16]));
                ds.setOrientY(Double.parseDouble(dataset[17]));
                ds.setOrientZ(Double.parseDouble(dataset[18]));

                ds.setD_accelX(Double.parseDouble(dataset[19]));
                ds.setD_accelY(Double.parseDouble(dataset[20]));
                ds.setD_accelZ(Double.parseDouble(dataset[21]));

                ds.setD_magX(Double.parseDouble(dataset[22]));
                ds.setD_magY(Double.parseDouble(dataset[23]));
                ds.setD_magZ(Double.parseDouble(dataset[24]));

                ds.setD_gyroX(Double.parseDouble(dataset[25]));
                ds.setD_gyroY(Double.parseDouble(dataset[26]));
                ds.setD_gyroZ(Double.parseDouble(dataset[27]));

                ds.setTime(new Date(dataset[28]));
                ds.setPositive(Integer.parseInt(dataset[29])==1);

                helper.insertData(ds);
            }
    }

    public static File saveToCSVFile(Context context, File file) {
        file.getParentFile().mkdirs();

        DataBaseHelper helper = DataBaseHelper.getInstance(context);

        Cursor cursor = helper.getData();
        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(file));

            writer.write("Id");
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

            writer.write("D_AccX");
            writer.write(",");
            writer.write("D_AccY");
            writer.write(",");
            writer.write("D_AccZ");
            writer.write(",");

            writer.write("D_MagneticX");
            writer.write(",");
            writer.write("D_MagneticY");
            writer.write(",");
            writer.write("D_MagneticZ");
            writer.write(",");

            writer.write("D_GyroscopeX");
            writer.write(",");
            writer.write("D_GyroscopeY");
            writer.write(",");
            writer.write("D_GyroscopeZ");
            writer.write(",");
            writer.write("Time");
            writer.write(",");
            writer.write("Label");
            writer.write(",");

            writer.write('\n');

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex("_id"));

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

                String dax = cursor.getString(cursor.getColumnIndex(DataBaseHelper.D_ACCELX));
                String day = cursor.getString(cursor.getColumnIndex(DataBaseHelper.D_ACCELY));
                String daz = cursor.getString(cursor.getColumnIndex(DataBaseHelper.D_ACCELZ));

                String dmx = cursor.getString(cursor.getColumnIndex(DataBaseHelper.D_MAGNETICX));
                String dmy = cursor.getString(cursor.getColumnIndex(DataBaseHelper.D_MAGNETICY));
                String dmz = cursor.getString(cursor.getColumnIndex(DataBaseHelper.D_MAGNETICZ));

                String dgyrx = cursor.getString(cursor.getColumnIndex(DataBaseHelper.D_GYROX));
                String dgyry = cursor.getString(cursor.getColumnIndex(DataBaseHelper.D_GYROY));
                String dgyrz = cursor.getString(cursor.getColumnIndex(DataBaseHelper.D_GYROZ));


                String time = cursor.getString(cursor.getColumnIndex(DataBaseHelper.TIME));
                String positive = cursor.getString(cursor.getColumnIndex(DataBaseHelper.POSITIVE));


                writer.write(String.valueOf(id));
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

                writer.write(dax);
                writer.write(",");
                writer.write(day);
                writer.write(",");
                writer.write(daz);
                writer.write(",");

                writer.write(dmx);
                writer.write(",");
                writer.write(dmy);
                writer.write(",");
                writer.write(dmz);
                writer.write(",");

                writer.write(dgyrx);
                writer.write(",");
                writer.write(dgyry);
                writer.write(",");
                writer.write(dgyrz);
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

        return file;
    }



    public static void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();

    }

    public static JSONObject makeParamJSON(ArrayList<Double> cValues, ArrayList<String> sensors) {
        JSONObject obj = new JSONObject();
        JSONArray cArray = new JSONArray();
        JSONArray sArray = new JSONArray();

        try {

            for (double val : cValues) {
                cArray.put(val);
            }

            obj.put("C_array", cArray);

            for (String s : sensors) {
                sArray.put(s);
            }

            obj.put("sensor_array", sArray);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }


}
