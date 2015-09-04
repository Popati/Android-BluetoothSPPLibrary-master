package app.akexorcist.bluetoothspp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class dbTracking extends SQLiteOpenHelper  {
	

    // Database Version
    private static final int DATABASE_VERSION = 3;
 
    // Database Name
    private static final String DATABASE_NAME = "Tracking";

	public dbTracking(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}




	@Override
	public void onCreate(SQLiteDatabase db) {
		// Create Table Name
	    db.execSQL("CREATE TABLE Temp (Time DATETIME DEFAULT CURRENT_TIMESTAMP, Tem REAL, Node TEXT ,PRIMARY KEY (Time,Node));");
	    db.execSQL("CREATE TABLE Gyro (Time DATETIME PRIMARY KEY DEFAULT CURRENT_TIMESTAMP, x REAL, y REAL, z REAL);");
	    db.execSQL("CREATE TABLE Location (Time DATETIME PRIMARY KEY DEFAULT CURRENT_TIMESTAMP, lati REAL, long REAL);");
	    db.execSQL("CREATE TABLE HistoryTime (TimeStart DATETIME PRIMARY KEY DEFAULT CURRENT_TIMESTAMP, TimeStop DATETIME DEFAULT CURRENT_TIMESTAMP);");
		   
	    Log.e("CREATE TABLE","Create Table Successfully.");
	}
	// Insert Data Temp
	public long InsertDataTemp(String dt, double tem,String node) {
		 try {
			SQLiteDatabase db;
     		db = this.getWritableDatabase(); // Write Data
     		
     		ContentValues initialValues = new ContentValues(); 
     		initialValues.put("Time", dt);
     		initialValues.put("Tem", tem);
			initialValues.put("Node", node);
     		
     		long rowId = db.insert("Temp", null, initialValues);
     		
			db.close();
			return rowId; // return rows inserted.
           
		 } catch (Exception e) {
		    return -1;
		 }
	}
	// Insert Data History
	public long InsertHistory(String t1, String t2) {
		
		 try {
			SQLiteDatabase db;
     		db = this.getWritableDatabase(); // Write Data
     		
     		ContentValues initialValues = new ContentValues(); 
     		initialValues.put("TimeStart", t1);
     		initialValues.put("TimeStop", t2);
     		
     		long rowId = db.insert("HistoryTime", null, initialValues);
     		Log.v("InsertHistory",t1+ ","  + t2);
			db.close();
			return rowId; // return rows inserted.
           
		 } catch (Exception e) {
		    return -1;
		 }
	}
	// Insert Data Gyro
	public long InsertDataGyro(String dt,double x,double y,double z) {
		 try {
			SQLiteDatabase db;
     		db = this.getWritableDatabase(); // Write Data
     		
     		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
     		Date date = new Date();
     		ContentValues initialValues = new ContentValues(); 
     		initialValues.put("Time", dt);
     		initialValues.put("x", x);
     		initialValues.put("y", y);
     		initialValues.put("z", z);
     		
     		long rowId = db.insert("Gyro", null, initialValues);
     		
			db.close();
			return rowId; // return rows inserted.
           
		 } catch (Exception e) {
		    return -1;
		 }
	}
	// Insert Data Localtion
	public long InsertDataLocal(String dt,double lat,double lon) {
		// TODO Auto-generated method stub
		
		 try {
			SQLiteDatabase db;
     		db = this.getWritableDatabase(); // Write Data
     		
     		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
     		Date date = new Date();
     		ContentValues initialValues = new ContentValues(); 
     		initialValues.put("Time", dt);
     		initialValues.put("lati", lat);
     		initialValues.put("long", lon);
     		
     		long rowId = db.insert("Gyro", null, initialValues);
     		
			db.close();
			return rowId; // return rows inserted.
           
		 } catch (Exception e) {
		    return -1;
		 }
	}
	// Select Data
	public String[] SelectData() {
		// TODO Auto-generated method stub
		
		 try {
			String arrData[] = null;	
			
			SQLiteDatabase db;
			db = this.getReadableDatabase(); // Read Data
				
			Cursor cursor = db.rawQuery("SELECT * FROM HistoryTime",null);	
			arrData = new String[cursor.getColumnCount()];
			
		 	if(cursor != null)
		 	{
		 		for(int i=0;i<arrData.length;i++){
					if (cursor.moveToFirst()) {
						//Log.v("count",(cursor.getColumnCount()));
						arrData[i] = cursor.getString(0)+"-"+cursor.getString(1);
						//arrData[1] = cursor.getString(1);
					}
			 	}
			}
		 	cursor.close();
			db.close();
			return arrData;
				
		 } catch (Exception e) {
		    return null;
		 }

	}
	// Select All Data
	public class sMembers {
		String _MemberID, _Name, _Tel;
		
		// Set Value
		public void sMemberID(String vMemberID){
	        this._MemberID = vMemberID;
	    }
		public void sName(String vName){
	        this._Name = vName;
	    }
		public void sTel(String vTel){
	        this._Tel = vTel;
	    }	
		
		// Get Value
		public String gMemberID(){
	        return _MemberID;
	    }
		public String gName(){
			 return _Name;
	    }
		public String gTel(){
	        return _Tel;
	    }	
	}
	// Select All Data
	public class sGyro {
		String _Time;
		double _x, _y, _z;
		// Set Value
		public void sTime(String vT){
	        this._Time = vT;
	    }
		public void sX(double vX){
	        this._x = vX;
	    }
		public void sY(double vY){
	        this._y = vY;
	    }
		public void sZ(double vZ){
	        this._z = vZ;
	    }	
		
		// Get Value
		public String gTime(){
	        return _Time;
	    }
		public double gX(){
			 return _x;
	    }
		public double gY(){
	        return _y;
	    }	
		public double gZ(){
			return _z;
		}
	}
	// Select All Data
	public class sHistory {
		String _t1, _t2;
		
		// Set Value
		public void tt1(String t1){
	        this._t1 = t1;
	    }
		public void tt2(String t2){
	        this._t2 = t2;
		}	
		
		// Get Value
		public String gT1(){
	        return _t1;
	    }
		public String gT2(){
			 return _t2;
	    }
	}
	
	public List<sMembers> SelectAllDataTemp() {
		 try {
			 List<sMembers> MemberList = new ArrayList<sMembers>();
			 
			 SQLiteDatabase db;
			 db = this.getReadableDatabase(); // Read Data
				
			 String strSQL = "SELECT * FROM Temp";
			 Cursor cursor = db.rawQuery(strSQL, null);
			 
			 	if(cursor != null)
			 	{
			 	    if (cursor.moveToFirst()) {
			 	        do {
			 	        	sMembers cMember = new sMembers();
			 	        	cMember.sMemberID(cursor.getString(0));
			 	        	cMember.sName(cursor.getString(1));
							cMember.sTel(cursor.getString(2));
			 	        	MemberList.add(cMember);
			 	        } while (cursor.moveToNext());
			 	    }
			 	}
			 	cursor.close();
				db.close();
				return MemberList;
				
		 } catch (Exception e) {
		    return null;
		 }
	}
	public List<sHistory> SelectAllDataHistory() {
		
		 try {
			 List<sHistory> MemberList = new ArrayList<sHistory>();
			 
			 SQLiteDatabase db;
			 db = this.getReadableDatabase(); // Read Data
				
			 String strSQL = "SELECT * FROM HistoryTime ORDER BY TimeStart DESC";
			 Cursor cursor = db.rawQuery(strSQL, null);
			 
			 	if(cursor != null)
			 	{
			 	    if (cursor.moveToFirst()) {
			 	        do {
			 	        	sHistory cMember = new sHistory();
			 	        	cMember.tt1(cursor.getString(0));
			 	        	cMember.tt2(cursor.getString(1));
			 	        	MemberList.add(cMember);
			 	        } while (cursor.moveToNext());
			 	    }
			 	}
			 	cursor.close();
				db.close();
				return MemberList;
				
		 } catch (Exception e) {
		    return null;
		 }
	}
	public List<sMembers> SelectAllDataTemp(String timestart,String timestop,String node) {
		// TODO Auto-generated method stub
		
		 try {
			 List<sMembers> MemberList = new ArrayList<sMembers>();
			 
			 SQLiteDatabase db;
			 db = this.getReadableDatabase(); // Read Data
				
			 String strSQL = "SELECT * FROM Temp WHERE Time >= '"+timestart+"' AND Time < '"+timestop+"' AND Node ='"+node+"' ";
					 
			 Cursor cursor = db.rawQuery(strSQL, null);
			 
			 	if(cursor != null)
			 	{
			 	    if (cursor.moveToFirst()) {
			 	        do {
			 	        	sMembers cMember = new sMembers();
			 	        	cMember.sMemberID(cursor.getString(0));
			 	        	cMember.sName(cursor.getString(1));
			 	        	//cMember.sTel(cursor.getString(2));
			 	        	MemberList.add(cMember);
			 	        } while (cursor.moveToNext());
			 	    }
			 	}
			 	cursor.close();
				db.close();
				return MemberList;
				
		 } catch (Exception e) {
		    return null;
		 }
	}
	public List<sMembers> SelectRealTemp(String timestart,String node) {
		// TODO Auto-generated method stub

		try {
			List<sMembers> MemberList = new ArrayList<sMembers>();

			SQLiteDatabase db;
			db = this.getReadableDatabase(); // Read Data

			String strSQL = "SELECT * FROM Temp WHERE Time >= '"+timestart+"' AND Node ='"+node+"' ";

			Cursor cursor = db.rawQuery(strSQL, null);

			if(cursor != null)
			{
				if (cursor.moveToFirst()) {
					do {
						sMembers cMember = new sMembers();
						cMember.sMemberID(cursor.getString(0));
						cMember.sName(cursor.getString(1));
						cMember.sTel(cursor.getString(2));
						MemberList.add(cMember);
					} while (cursor.moveToNext());
				}
			}
			cursor.close();
			db.close();
			return MemberList;

		} catch (Exception e) {
			return null;
		}
	}
	public List<sGyro> SelectAllDataGyro() {
		// TODO Auto-generated method stub
		 try {
			 List<sGyro> MemberList = new ArrayList<sGyro>();
			 
			 SQLiteDatabase db;
			 db = this.getReadableDatabase(); // Read Data
				
			 String strSQL = "SELECT * FROM Gyro";
			 Cursor cursor = db.rawQuery(strSQL, null);
			 
			 	if(cursor != null)
			 	{
			 	    if (cursor.moveToFirst()) {
			 	        do {
			 	        	sGyro cMember = new sGyro();
			 	        	cMember.sTime(cursor.getString(0));
			 	        	cMember.sX(cursor.getDouble(1));
			 	        	cMember.sY(cursor.getDouble(2));
			 	        	cMember.sZ(cursor.getDouble(3));
			 	        	//cMember.sTel(cursor.getString(2));
			 	        	MemberList.add(cMember);
			 	        } while (cursor.moveToNext());
			 	    }
			 	}
			 	cursor.close();
				db.close();
				return MemberList;
				
		 } catch (Exception e) {
		    return null;
		 }

	}

	public float SelectLastDataTemp(String time,String node) {
		// TODO Auto-generated method stub

		try {
			float Value= (float) 0.00;
			String data=null;

			SQLiteDatabase db;
			db = this.getReadableDatabase(); // Read Data

			String strSQL = "SELECT * FROM Temp WHERE Time = '"+time+"' AND Node ='"+node+"' ";

			Cursor cursor = db.rawQuery(strSQL, null);

			if(cursor != null)
			{
				if (cursor.moveToFirst()) {
					do {
						data=cursor.getString(1);
						Value=Float.parseFloat(data);
					} while (cursor.moveToNext());
				}
			}
			cursor.close();
			db.close();
			return Value;
		} catch (Exception e) {
			return -1;
		}
	}

	// Delete Data
	public void DeleteData() {
		 try {
			SQLiteDatabase db;
     		db = this.getWritableDatabase(); // Write Data
     		
     		db.execSQL("delete from Temp");
     		db.execSQL("delete from Gyro");
     		db.execSQL("delete from HistoryTime");
			db.close();
			//return rows; // return rows delete.

		 } catch (Exception e) {
		    //return -1;
		 }

	}
	public void DropTable() {
		// TODO Auto-generated method stub
		
		 try {
			
			SQLiteDatabase db;
     		db = this.getWritableDatabase(); // Write Data
     		
     		db.execSQL("DROP TABLE IF EXISTS Temp");
            db.execSQL("DROP TABLE IF EXISTS Gyro");
            db.execSQL("DROP TABLE IF EXISTS Location");
            db.execSQL("DROP TABLE IF EXISTS HistoryTime");
            Log.v("DROP","OK");
            onCreate(db);
			//return rows; // return rows delete.

		 } catch (Exception e) {
		    //return -1;
		 }

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS Temp");
        db.execSQL("DROP TABLE IF EXISTS Gyro");
        db.execSQL("DROP TABLE IF EXISTS Location");
        db.execSQL("DROP TABLE IF EXISTS HistoryTime");
        
        // Re Create on method  onCreate
        onCreate(db);
	}
}
