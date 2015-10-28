package com.detroitteatime.datagatherer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper{

	private SQLiteDatabase ourDatabase;
	
	public static final int READABLE = 0;
	public static final int WRITEABLE = 1;
	
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final String SHORT_DATE_FORMAT = "MM/dd";

	public static final String SENSOR_TABLE_NAME = "sensor_data";
	public static final String MODEL_TABLE_NAME = "models";

	public static final String MODEL = "model";
	public static final String NAME = "name";
	public static final String CLASS = "class";
    public static final String PARAMETERS = "parameters";
	public static final String METHOD = "method";

	public static final String SPEED_GPS = "speed_GPS";
	public static final String SPEED_ACCEL = "speed_accel";
	public static final String ACCELX = "accelerationX";
	public static final String ACCELY = "accelerationY";
	public static final String ACCELZ = "accelerationZ";
	public static final String TIME = "time";
	public static final String LATTITUDE = "lattitude";
	public static final String LONGITUDE = "longitude";
	public static final String SOUND = "sound_level";
	public static final String GRAVITYX = "gravityX";
	public static final String GRAVITYY = "gravityY";
	public static final String GRAVITYZ = "gravityZ";
	public static final String LINEARX = "linear_AccelX";
	public static final String LINEARY = "linear_AccelY";
	public static final String LINEARZ = "linear_AccelZ";
	public static final String MAGNETICX = "magneticX";
	public static final String MAGNETICY = "magneticY";
	public static final String MAGNETICZ = "magneticZ";
	public static final String GYROX = "gyroX";
	public static final String GYROY = "gyroY";
	public static final String GYROZ = "gyroZ";
	public static final String ORIENTX = "orientationX";
	public static final String ORIENTY = "orientationY";
	public static final String ORIENTZ = "orientationZ";
	public static final String TEMPERATURE = "temperature";
	public static final String POSITIVE = "in_category";
	public static final String DB_NAME = "data";
	public static final String ID = "_id";

	
	public DataBaseHelper(Context context) {
		super(context, DB_NAME, null, 1);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		try{
			db.execSQL("CREATE TABLE " + SENSOR_TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
					SPEED_GPS + " REAL, " +
					LATTITUDE + " REAL, " +
					LONGITUDE + " REAL, " +
					
					ACCELX + " REAL, " +
					ACCELY + " REAL, " +
					ACCELZ + " REAL, " +
					
					GRAVITYX + " REAL, " +
					GRAVITYY + " REAL, " +
					GRAVITYZ + " REAL, " +
					
					LINEARX + " REAL, " +
					LINEARY + " REAL, " +
					LINEARZ + " REAL, " +
					
					MAGNETICX + " REAL, " +
					MAGNETICY + " REAL, " +
					MAGNETICZ + " REAL, " +
					
					GYROX + " REAL, " +
					GYROY + " REAL, " +
					GYROZ + " REAL, " +
					
					ORIENTX + " REAL, " +
					ORIENTY + " REAL, " +
					ORIENTZ + " REAL, " +
					
					TIME + " TEXT, " + 
					POSITIVE + " BOOLEAN);");

			db.execSQL("CREATE TABLE " + MODEL_TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
					NAME + " TEXT, " +
					CLASS + " TEXT, " +
					MODEL + " TEXT, " +
					METHOD + " TEXT, " +
                    PARAMETERS + " TEXT);");

		}catch(SQLiteException e){
			Log.e("My Code", "DataBaseHelper, db.execSQL(): CREATE TABLE Speed Data " + e.getMessage());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Add some sql later

	}
	public void open(int i){

		if (i == WRITEABLE){
			ourDatabase = getWritableDatabase();
		}else{
			ourDatabase = getReadableDatabase();
		}
	}

	public void close(){
		ourDatabase.close();
	}

	//Data Table Methods /////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////

	public void insertData(DataSet data){
		ContentValues cv = setValues(data);
		
		try{
			ourDatabase = getWritableDatabase();
			ourDatabase.insertOrThrow(SENSOR_TABLE_NAME, "nullColumnHack", cv);

		}catch(Exception e){
			if(e.getMessage()!= null) Log.e("My Code", e.getMessage());
		}
	}

	public void insertDataArray(List<DataSet> dataList){
		ourDatabase = getWritableDatabase();
		for(DataSet d: dataList){
			ContentValues cv = setValues(d);

			try{
				ourDatabase.insertOrThrow(SENSOR_TABLE_NAME, "nullColumnHack", cv);
				ourDatabase.close();
			}catch(Exception e){
				if(e.getMessage()!= null) Log.e("My Code", e.getMessage());
			}
		}

	}
	
	public Cursor getData(){
		ourDatabase = getReadableDatabase();
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(SENSOR_TABLE_NAME);
		Cursor cursor = builder.query(ourDatabase, null, null, null, 
				null, null, TIME, null);

		return cursor;
		
	}
	
	public ContentValues setValues(DataSet data){
		
		ContentValues cv = new ContentValues();
		cv.put(SPEED_GPS, data.getSpeedGPS());
		cv.put(LONGITUDE, data.getLng());
		cv.put(LATTITUDE, data.getLat());
		cv.put(TIME, data.getTime());
		cv.put(ACCELX, data.getAccelX());
		cv.put(ACCELY, data.getAccelY());
		cv.put(ACCELZ, data.getAccelZ());
		cv.put(GRAVITYX, data.getGravX());
		cv.put(GRAVITYY, data.getGravY());
		cv.put(GRAVITYZ, data.getGravZ());
		cv.put(LINEARX, data.getLinX());
		cv.put(LINEARY, data.getLinY());
		cv.put(LINEARZ, data.getLinZ());
		cv.put(MAGNETICX, data.getMagX());
		cv.put(MAGNETICY, data.getMagY());
		cv.put(MAGNETICZ, data.getMagZ());
		cv.put(GYROX, data.getGyroX());
		cv.put(GYROY, data.getGyroY());
		cv.put(GYROZ, data.getGyroZ());
		cv.put(ORIENTX, data.getOrientX());
		cv.put(ORIENTY, data.getOrientY());
		cv.put(ORIENTZ, data.getOrientZ());
		cv.put(POSITIVE, data.isPositive());
		
		return cv;
	}

	//// Model methods /////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////

	public ContentValues setModelCV(String name, String c, String method, String params, String model){

		ContentValues cv = new ContentValues();
		cv.put(NAME, name);
		cv.put(MODEL, model);
		cv.put(CLASS, c);
		cv.put(METHOD, method);
		cv.put(PARAMETERS, params);

		return cv;
	}

	public void insertModel(String name,  String c, String method, String params, String model){
		ourDatabase = getWritableDatabase();
		ContentValues cv = setModelCV(name, c, method, params, model);
		try{
			ourDatabase.insertOrThrow(MODEL_TABLE_NAME, "nullColumnHack", cv);

		}catch(Exception e){
			if(e.getMessage()!= null) Log.e("My Code", e.getMessage());
		}
	}

	public void editModel(long id, String name,  String c, String method, String params, String model){
		ourDatabase = getWritableDatabase();
		ContentValues cv = setModelCV(name, model, c, method, params);
		String[] ids = {String.valueOf(id)};
		ourDatabase.update(MODEL_TABLE_NAME, cv, "_id=?", ids);

	}

	public Cursor getModelById(long id){
		ourDatabase = getReadableDatabase();
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(MODEL_TABLE_NAME);
		String[] ids = {String.valueOf(id)};

		Cursor cursor = builder.query(ourDatabase, null, "_id=?", ids,
				null, null, null, null);

		return cursor;
	}

	public Cursor getModelData(){
		ourDatabase = getReadableDatabase();
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(MODEL_TABLE_NAME);

		Cursor cursor = builder.query(ourDatabase, null, null, null,
				null, null, null, null);

		return cursor;
	}

	public Predictor getPredictorById(long id){
		ourDatabase = getReadableDatabase();
		Cursor c = getModelById(id);
		c.moveToFirst();
		int idx = c.getColumnIndex(METHOD);
		Predictor p = null;
		String method = c.getString(idx);
		String name = c.getString(c.getColumnIndex(NAME));
		String model = c.getString(c.getColumnIndex(MODEL));
		String category = c.getString(c.getColumnIndex(CLASS));
		String parameters = c.getString(c.getColumnIndex(PARAMETERS));

		switch(method){

			case Constants.LOGISIC_REGRESSION:
				p = new LogisticPredictor();
				p.setCategory(category);
				p.setModel(model);
				p.setName(name);
				p.setParameterString(parameters);
				p.setMethod(method);
				break;

			case Constants.SVM:
				break;
			case Constants.NEURAL_NET:
				break;
		}
		c.close();

		return p;
	}

	public void persistPredictor(Predictor p){
		insertModel(p.getName(), p.getCategory(), p.getMethod(), p.getParameterString(), p.getModel());
	}
}