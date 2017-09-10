package com.amul.dc.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.amul.dc.pojos.TransactionBeans;

import java.util.ArrayList;

public class DataHelperClass {
	private Context mContext;

	public DataHelperClass(Context con) {
		mContext = con;
	}

	public void addDcDetails(TransactionBeans transactionBeans) {
		DBOpenHelperClass DBOHC = DBOpenHelperClass.getSharedObject(mContext);
		SQLiteDatabase SQDB = DBOHC.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DBOHC.COLUMN_IMAGE_1, transactionBeans.getImageOne());
		values.put(DBOHC.COLUMN_IMAGE_2, transactionBeans.getImageTwo());
		SQDB.beginTransaction();
		try {
			if(!isRecordExist(transactionBeans.getUniqueId())){
				values.put(DBOHC.COLUMN_STORE_NAME, transactionBeans.getStoreName());
				values.put(DBOHC.COLUMN_STORE_LOCATION, transactionBeans.getStoreLocation());
				values.put(DBOHC.COLUMN_SCAN_DATE_TIME, transactionBeans.getScandatetime());
				values.put(DBOHC.COLUMN_GPS_COORDINATE, transactionBeans.getGpscoordinate());

				values.put(DBOHC.COLUMN_CITY_ID, transactionBeans.getCityId());
				values.put(DBOHC.COLUMN_ROUTE_ID, transactionBeans.getRouteId());
				values.put(DBOHC.COLUMN_LATITUDE, transactionBeans.getLatitude());
				values.put(DBOHC.COLUMN_LONGITUDE, transactionBeans.getLongitude());

				values.put(DBOHC.COLUMN_STATUS, "Pending");
				values.put(DBOHC.COLUMN_ADDITIONAL_1, "");
				values.put(DBOHC.COLUMN_ADDITIONAL_2, "");
				values.put(DBOHC.COLUMN_ADDITIONAL_3, "");
			SQDB.insert(DBOHC.TABLE_STORE_DETAILS, null, values);
			}
			else{
				SQDB.update(DBOHC.TABLE_STORE_DETAILS, values, DBOHC.COLUMN_UNIQUE_ID  + " = '" + transactionBeans.getUniqueId() + "'",null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		SQDB.setTransactionSuccessful();
		SQDB.endTransaction();

	}

	public ArrayList<TransactionBeans> getDcDetailsData() {
		{
			DBOpenHelperClass DBOHC = DBOpenHelperClass.getSharedObject(mContext);
			SQLiteDatabase SQDB = DBOHC.getWritableDatabase();
			ArrayList<TransactionBeans> scanList = new ArrayList<TransactionBeans>();
			String myQuery = "SELECT  * FROM " + DBOHC.TABLE_STORE_DETAILS;
			// + " ORDER BY NOTIFICATION_ID DESC";
			try {
				Cursor cursor = SQDB.rawQuery(myQuery, null);
				if (cursor != null) {
					if (cursor.moveToFirst()) {
						do {
							try {
								TransactionBeans transactionBeans = new TransactionBeans();
								transactionBeans.setUniqueId(cursor.getInt(0));
								transactionBeans.setStoreName(cursor.getString(1));
								transactionBeans.setStoreLocation(cursor.getString(2));
								transactionBeans.setScandatetime(cursor.getString(3));
								transactionBeans.setGpscoordinate(cursor.getString(4));
								transactionBeans.setCityId(cursor.getString(5));
								transactionBeans.setRouteId(cursor.getString(6));
								transactionBeans.setLatitude(cursor.getString(7));
								transactionBeans.setLongitude(cursor.getString(8));
								transactionBeans.setImageOne(cursor.getBlob(9));
								transactionBeans.setImageTwo(cursor.getBlob(10));
								transactionBeans.setStatus(cursor.getString(11));
								scanList.add(transactionBeans);
							} catch (Exception e) {
								Log.d("DB_EXCEPTION" + "OBJ_NOT : ", e.getMessage());
							}
						} while (cursor.moveToNext());
					}
				}
				cursor.close();
				// SQDB.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return scanList;
		}

	}

	public boolean isRecordExist() {

		DBOpenHelperClass DBOHC = DBOpenHelperClass.getSharedObject(mContext);
		String selectQuery = "SELECT * FROM  "+DBOHC.TABLE_STORE_DETAILS;
		SQLiteDatabase db = DBOHC.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		boolean isexit = cursor.getCount() > 0 ? true : false;
		if (null != cursor)
			cursor.close();

		return isexit;

	}
	
	public boolean isRecordExist(int unique_id) {

		DBOpenHelperClass DBOHC = DBOpenHelperClass.getSharedObject(mContext);
		SQLiteDatabase db = DBOHC.getWritableDatabase();
		String sql = "SELECT * FROM " + DBOHC.TABLE_STORE_DETAILS + " WHERE UNIQUE_ID=" + unique_id;
		Cursor cursor = db.rawQuery(sql, null);

		boolean isexit = cursor.getCount() > 0 ? true : false;
		if (null != cursor)
			cursor.close();
		return isexit;
	}

	public boolean deleteRecord(int name) {
		DBOpenHelperClass DBOHC = DBOpenHelperClass.getSharedObject(mContext);
		SQLiteDatabase db = DBOHC.getWritableDatabase();
		return db.delete(DBOHC.TABLE_STORE_DETAILS, DBOHC.COLUMN_UNIQUE_ID + "=" + name, null) > 0;
	}
	
	
}
