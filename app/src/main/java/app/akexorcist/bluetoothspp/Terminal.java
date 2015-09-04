package app.akexorcist.bluetoothspp;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ubidots.ApiClient;
import com.ubidots.Variable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothSPP.BluetoothConnectionListener;
import app.akexorcist.bluetotohspp.library.BluetoothSPP.OnDataReceivedListener;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class Terminal extends Activity {

	BluetoothSPP bt;
    TextView textStatus, numcel, numfer,temp,numcel2,numfer2,numcel3,numfer3;
    private Timer timer;
    Menu menu;
    TextView numx, numy, numz;
	SensorManager sensorManager;
	Sensor sensor;
	RelativeLayout l;
	
    private Thread thread;
    private Handler handler = new Handler();
    Double timeStart,timeStop;
    String timeStart2,timeStop2,page,tStart,tStop;

	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal);

        HideAct();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		//sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);//‡√‘Ë¡„™È service
		//sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		
		numx = (TextView) findViewById(R.id.numx);
		numy = (TextView) findViewById(R.id.numy);
		numz = (TextView) findViewById(R.id.numz);
		
		final dbTracking myDb = new dbTracking(this);
		myDb.getWritableDatabase(); // First method
		//myDb.DeleteData();

        Log.i("Check", "onCreate");
/*
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH.mm");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH.mm");
        Date date = new Date();
        timeStart= Double.parseDouble(dateFormat.format(date));
        timeStart2= dateFormat2.format(date);
*/
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        tStart= dateFormat.format(date);
        timeStart2= dateFormat2.format(date);

        Log.v("tStart",tStart);
        Log.v("timeStart2",timeStart2);

        // Permission StrictMode
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        numcel = (TextView)findViewById(R.id.numcel);
        numfer = (TextView)findViewById(R.id.numfer);
        numcel2 = (TextView)findViewById(R.id.numcel2);
        numfer2 = (TextView)findViewById(R.id.numfer2);
        numcel3 = (TextView)findViewById(R.id.numcel3);
        numfer3 = (TextView)findViewById(R.id.numfer3);

        textStatus = (TextView)findViewById(R.id.textStatus);

        bt = new BluetoothSPP(this);

        if(!bt.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }
        
        bt.setBluetoothConnectionListener(new BluetoothConnectionListener() {
            public void onDeviceDisconnected() {
                textStatus.setText("Status : Not connect");
                menu.clear();
                getMenuInflater().inflate(R.menu.menu_connection, menu);
                RelativeLayout layone= (RelativeLayout) findViewById(R.id.layout);// change id here
                layone.setVisibility(View.INVISIBLE);
            }

            public void onDeviceConnectionFailed() {
                textStatus.setText("Status : Connection failed");
            }

            public void onDeviceConnected(String name, String address) {
                textStatus.setText("Status : Connected to " + name);
                menu.clear();
                getMenuInflater().inflate(R.menu.menu_disconnection, menu);

                ShowAct();
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
         		Date date = new Date();
                tStart= dateFormat.format(date);
            	timeStart2= dateFormat2.format(date);
            	Log.v("TimeStart", tStart.toString() + " | " + timeStart2);

                getHttpGet("http://www.tqfsmart.info/delUbidots.php");
                getHttpGet("http://www.tqfsmart.info/delUbidots2.php");
                getHttpGet("http://www.tqfsmart.info/delUbidotsG.php");
            }
        });
        // Button(Delete)
        final Button btn5 = (Button) findViewById(R.id.btnClear);
        btn5.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	myDb.DeleteData();
            }
        });
        
    }

    public class ApiUbidots extends AsyncTask<Double, Void, Void> {
        private final String API_KEY = "ce3bdae0671276a0a8f26f8655def30839f5a894";
        private final String VARIABLE_ID = "55ded3327625424a0f32fdcc";

        @Override
        protected Void doInBackground(Double... params) {
            ApiClient apiClient = new ApiClient(API_KEY);
            Variable TempLevel = apiClient.getVariable(VARIABLE_ID);

            TempLevel.saveValue(params[0]);

            return null;
        }
    }
    public class ApiUbidots2 extends AsyncTask<Double, Void, Void> {
        private final String API_KEY = "ce3bdae0671276a0a8f26f8655def30839f5a894";
        private final String VARIABLE_ID = "55a738267625422d450e912a";

        @Override
        protected Void doInBackground(Double... params) {
            ApiClient apiClient = new ApiClient(API_KEY);
            Variable TempLevel = apiClient.getVariable(VARIABLE_ID);

            TempLevel.saveValue(params[0]);

            return null;
        }
    }
    public class ApiUbidotsG extends AsyncTask<Double, Void, Void> {
        private final String API_KEY = "ce3bdae0671276a0a8f26f8655def30839f5a894";
        private final String VARIABLE_ID = "55a738317625422a17882088";

        @Override
        protected Void doInBackground(Double... params) {
            ApiClient apiClient = new ApiClient(API_KEY);
            Variable TempLevel = apiClient.getVariable(VARIABLE_ID);

            TempLevel.saveValue(params[0]);
            //TempLevel.

            return null;
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_connection, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_android_connect) {
            bt.setDeviceTarget(BluetoothState.DEVICE_ANDROID);
			
			if(bt.getServiceState() == BluetoothState.STATE_CONNECTED)
    			bt.disconnect();
            Intent intent = new Intent(getApplicationContext(), DeviceList.class);
            startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
        } else if(id == R.id.menu_device_connect) {
            bt.setDeviceTarget(BluetoothState.DEVICE_OTHER);
			
			if(bt.getServiceState() == BluetoothState.STATE_CONNECTED)
    			bt.disconnect();
            Intent intent = new Intent(getApplicationContext(), DeviceList.class);
            startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
        } else if(id == R.id.menu_disconnect) {
            if(bt.getServiceState() == BluetoothState.STATE_CONNECTED){
                bt.disconnect();
                Intent in=new Intent(this,Home.class);
                startActivity(in);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void onDestroy() {
        super.onDestroy();
        //bt.stopService();
    }

    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if(!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                showTemp();
            }
        }
    }
    
    public void onResume() {
		super.onResume();
		//sensorManager.registerListener(gyroListener, sensor,
                //SensorManager.SENSOR_DELAY_NORMAL);
	}
 
	public void onStop() {
		super.onStop();
		//sensorManager.unregisterListener(gyroListener);

	}
/*
	public SensorEventListener gyroListener = new SensorEventListener() {
		public void onAccuracyChanged(Sensor sensor, int acc) { }
 
		public void onSensorChanged(SensorEvent event) {
			
			float x = event.values[0];
			float y = event.values[1];
			float z = event.values[2];

			numx.setText("X :  " + (int)x + "  rad/s");
			numy.setText("Y :  " + (int)y + "  rad/s");
			numz.setText("Z :  " + (int)z + "  rad/s");
			
			if((int)x>2 || (int)x<-2 || (int)y>2 || (int)y<-2 || (int)z>2 || (int)z<-2)
				InsertGyro((int)x,(int)y,(int)z);
		}
	};*/
	public void InsertGyro(double x,double y,double z){
		final dbTracking myDb = new dbTracking(this);
		myDb.getWritableDatabase(); // First method
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH.mm");
 		Date date = new Date();
    	myDb.InsertDataGyro(dateFormat.format(date), x, y, z);
    	Log.v("showGyro", dateFormat.format(date) + "," + x + "," + y + "," + z);
    	
	}
    int count=0;
    public void showTemp() {
    	final dbTracking myDb = new dbTracking(this);
		myDb.getWritableDatabase(); // First method

    	thread = new Thread() {
            public void run() {
                while (true) {
                    try {
                        bt.setOnDataReceivedListener(new OnDataReceivedListener() {

                            public void onDataReceived(byte[] data, String message) {
                                count=count+1;
                                String hallostring = message;
                                String cel = null;

                                //Log.i("Count", String.valueOf(count));
                                //Log.i("Check", "Message : " + hallostring.length());
                                //Toast.makeText(Terminal.this, hallostring.length(), Toast.LENGTH_SHORT).show();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                Date date = new Date();

                                if(count>=300) {
                                    count = 0;
                                }
                                if(count<=6) {

                                    if (hallostring.length() == 27) {
                                        String node = hallostring.substring(0, 5);
                                        cel = hallostring.substring(8, 13);
                                        String fer = hallostring.substring(18, 23);
                                        double tc = Double.parseDouble(cel);

                                        SharedPreferences sp = getSharedPreferences("TempNotification", Context.MODE_PRIVATE);
                                        float maxnode1=sp.getFloat("MaxNode1", -1);
                                        float minnode1=sp.getFloat("MinNode1", -1);
                                        float maxnode2=sp.getFloat("MaxNode2", -1);
                                        float minnode2=sp.getFloat("MinNode2", -1);
                                        float maxnode3=sp.getFloat("MaxNode3", -1);
                                        float minnode3=sp.getFloat("MinNode3", -1);

                                        if (node.equals("node1")) {
                                            numcel.setText(cel);
                                            numfer.setText(fer);
                                            numcel.setTextColor(Color.parseColor("#FFFFFF"));
                                            numfer.setTextColor(Color.parseColor("#FFFFFF"));
                                            myDb.InsertDataTemp(dateFormat.format(date), tc, "node1");//insert temp to sqllite
                                            insertTOserver(dateFormat.format(date), tc, "node1");//insert temp to server


                                            new ApiUbidots().execute(tc);//send to ubidots

                                            float fc=Float.parseFloat(cel);
                                            Log.i("fc : maxnode1", fc+" "+Float.toString(maxnode1));
                                            if(fc>maxnode1){
                                                numcel.setTextColor(Color.parseColor("#FF0000"));
                                                numfer.setTextColor(Color.parseColor("#FF0000"));
                                                Toast.makeText(getApplicationContext(), "ขณะนี้เซนเซอร์ที่1อุณหภูมิมากกว่า "+maxnode1+" C", Toast.LENGTH_SHORT).show();
                                                createNotification("ขณะนี้เซนเซอร์ที่1อุณหภูมิมากกว่า " + maxnode1+" C");
                                            }
                                            if(fc<=minnode1){
                                                numcel.setTextColor(Color.parseColor("#FFFF66"));
                                                numfer.setTextColor(Color.parseColor("#FFFF66"));
                                                Toast.makeText(getApplicationContext(), "ขณะนี้เซนเซอร์ที่1อุณหภูมิต่ำกว่า "+minnode1+" C", Toast.LENGTH_SHORT).show();
                                                createNotification("ขณะนี้เซนเซอร์ที่1อุณหภูมิต่ำกว่า " + minnode1+" C");
                                            }
                                        }

                                        if (node.equals("node2")) {
                                            numcel2.setText(cel);
                                            numfer2.setText(fer);
                                            numcel2.setTextColor(Color.parseColor("#FFFFFF"));
                                            numfer2.setTextColor(Color.parseColor("#FFFFFF"));
                                            myDb.InsertDataTemp(dateFormat.format(date), tc, "node2");
                                            insertTOserver(dateFormat.format(date), tc, "node2");//insert temp to server

                                            new ApiUbidots2().execute(tc);//send to ubidots
                                            float fc=Float.parseFloat(cel);

                                            Log.i("fc : maxnode2", fc+" "+Float.toString(maxnode2));
                                            if(fc>maxnode2){
                                                numcel2.setTextColor(Color.parseColor("#FF0000"));
                                                numfer2.setTextColor(Color.parseColor("#FF0000"));
                                                Toast.makeText(getApplicationContext(), "ขณะนี้เซนเซอร์ที่2อุณหภูมิมากกว่า "+maxnode2+" C", Toast.LENGTH_SHORT).show();
                                                createNotification("ขณะนี้เซนเซอร์ที่2อุณหภูมิมากกว่า " + maxnode2+" C");
                                            }
                                            if(fc<=minnode2){
                                                numcel2.setTextColor(Color.parseColor("#FFFF66"));
                                                numfer2.setTextColor(Color.parseColor("#FFFF66"));
                                                Toast.makeText(getApplicationContext(), "ขณะนี้เซนเซอร์ที่2อุณหภูมิต่ำกว่า "+minnode2+" C", Toast.LENGTH_SHORT).show();
                                                createNotification("ขณะนี้เซนเซอร์ที่2อุณหภูมิต่ำกว่า " + minnode2+" C");
                                            }
                                        }
                                        if (node.equals("nodeg")) {
                                            numcel3.setText(cel);
                                            numfer3.setText(fer);
                                            numcel3.setTextColor(Color.parseColor("#FFFFFF"));
                                            numfer3.setTextColor(Color.parseColor("#FFFFFF"));
                                            myDb.InsertDataTemp(dateFormat.format(date), tc, "nodeg");
                                            insertTOserver(dateFormat.format(date), tc, "nodeg");//insert temp to server

                                            new ApiUbidotsG().execute(tc);//send to ubidots

                                            float fc=Float.parseFloat(cel);
                                            Log.i("fc : maxnode3", fc+" "+Float.toString(maxnode3));
                                            if(fc>maxnode3){
                                                numcel3.setTextColor(Color.parseColor("#FF0000"));
                                                numfer3.setTextColor(Color.parseColor("#FF0000"));
                                                Toast.makeText(getApplicationContext(), "ขณะนี้เซนเซอร์ที่3อุณหภูมิมากกว่า "+maxnode3+" C", Toast.LENGTH_SHORT).show();
                                                createNotification("ขณะนี้เซนเซอร์ที่3อุณหภูมิมากกว่า " + maxnode3+" C");
                                            }
                                            if(fc<=minnode3){
                                                numcel3.setTextColor(Color.parseColor("#FFFF66"));
                                                numfer3.setTextColor(Color.parseColor("#FFFF66"));
                                                Toast.makeText(getApplicationContext(), "ขณะนี้เซนเซอร์ที่3อุณหภูมิต่ำกว่า "+minnode3+" C", Toast.LENGTH_SHORT).show();
                                                createNotification("ขณะนี้เซนเซอร์ที่3อุณหภูมิต่ำกว่า " + minnode3+" C");
                                            }
                                        }
                                        //Toast.makeText(Terminal.this, hallostring, Toast.LENGTH_SHORT).show();
                                    }
                                }
                                if (hallostring.length() < 27) {
                                    String node = hallostring.substring(0, 1);
                                    if (node.equals("X"))
                                        numx.setText("X :  " + hallostring.substring(2, hallostring.length()) + "  rad/s");

                                    if (node.equals("Y"))
                                        numy.setText("Y :  " + hallostring.substring(2, hallostring.length()) + "  rad/s");

                                    if (node.equals("Z"))
                                        numz.setText("Z :  " + hallostring.substring(2, hallostring.length()) + "  rad/s");
                                }
                            }

                        });
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        Log.e("check", "local Thread error", e);
                    }
                }
            }
        };
        thread.start();

    }
    public String getHttpGet(String url) {
        StringBuilder str = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);

        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) { // Status OK
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    str.append(line);
                }
            } else {
                Log.e("Log", "Failed to download result..");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str.toString();
    }

    public void insertTOserver(String dt, double temp,String node){
        try {
            ArrayList<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("isAdd","true"));
            nameValuePairs.add(new BasicNameValuePair("time",dt));
            nameValuePairs.add(new BasicNameValuePair("tem",Double.toString(temp)));
            nameValuePairs.add(new BasicNameValuePair("node",node));

            HttpClient httpclient =new DefaultHttpClient();
            HttpPost httppost=new HttpPost("http://tqfsmart.info/addDATA.php");
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
            httpclient.execute(httppost);
        }
        catch (Exception e){
            Log.d("log_err","Error in http connection"+e.toString());
        }
    }
    public void createNotification(String m) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Notification notification = new Notification(android.R.drawable.stat_notify_error,
                "New notification", System.currentTimeMillis());

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        String Title = "Warning";
        String Message = m;

        Intent intent = new Intent(this, ReceiverActivity.class);
        PendingIntent activity = PendingIntent.getActivity(this, 0, intent, 0);
        notification.setLatestEventInfo(this, Title, Message, activity);

        notification.number += 1;

        notification.defaults = Notification.DEFAULT_ALL; // Sound Vibrate Light

        notificationManager.notify(1, notification);

    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if(resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                //Log.d("onActivityResult", "WTF");
                //setup();
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    public void onclickGraph(View view){
        /*
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH.mm");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH.mm");
        Date date = new Date();
        timeStop= Double.parseDouble(dateFormat.format(date));
        timeStop2= dateFormat2.format(date);
        Log.v("TimeStart",timeStop.toString());
        Intent intent = new Intent(this,Graph.class);
        intent.putExtra("Page","Terminal");
        intent.putExtra("TimeStart", timeStart);
        intent.putExtra("TimeStart2", timeStart2);
        intent.putExtra("TimeStop", timeStop);
        intent.putExtra("TimeStop2", timeStop2);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        bt.disconnect();
        startActivity(intent);*/

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        tStop= dateFormat.format(date);
        timeStop2= dateFormat2.format(date);
        Intent intent = new Intent(this,GoogleGraph.class);
        intent.putExtra("Page","Terminal");
        intent.putExtra("tStart", tStart);
        intent.putExtra("TimeStart2", timeStart2);
        intent.putExtra("tStop", tStop);
        intent.putExtra("TimeStop2", timeStop2);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        bt.disconnect();
        startActivity(intent);
    }
    public void onclickShowTemp(View view){
        Intent intent=new Intent(this,SetTemp.class);
        startActivity(intent);
    }
    public void onclickRealTime(View view){
        Intent intent=new Intent(this,Temp.class);
        startActivity(intent);
    }
    private void HideAct() {
        l=(RelativeLayout) findViewById(R.id.layout);
        l.setVisibility(View.INVISIBLE);

    }
    private void ShowAct() {
        l=(RelativeLayout) findViewById(R.id.layout);
        l.setVisibility(View.VISIBLE);

    }

}
