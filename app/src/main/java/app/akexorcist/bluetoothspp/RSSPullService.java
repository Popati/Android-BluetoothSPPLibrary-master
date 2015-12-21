package app.akexorcist.bluetoothspp;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;
import com.ubidots.ApiClient;
import com.ubidots.Variable;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;

/**
 * Created by Popati on 24/9/58.
 */
public class RSSPullService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    BluetoothSPP bt;
    TextView numcel, numfer,temp,numcel2,numfer2,numcel3,numfer3,txtResult;
    TextView numx, numy, numz;

    // flag for Internet connection status
    Boolean isInternetPresent = false;
    // Connection detector class
    ConnectionDetector cd;
    private Handler handler;
    String timeStart2,timeStop2,page,tStart,tStop;
    private GoogleApiClient googleApiClient=null;
    int overRange=0;


    int nointernet=0;
    ArrayList<Double> Valuenode1 = new ArrayList<>();
    ArrayList<Double> Valuenode2 = new ArrayList<>();
    ArrayList<String> TValuenode1 = new ArrayList<>();
    ArrayList<String> TValuenode2 = new ArrayList<>();

    ArrayList<String> Tgyro = new ArrayList<>();
    ArrayList<String> OfflineX = new ArrayList<>();
    ArrayList<String> OfflineY = new ArrayList<>();
    ArrayList<String> OfflineZ = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("RSS", "RSS onCreate");

        bt = new BluetoothSPP(this);

        if(!bt.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
//            finish();
        }
        else {
            bt.setupService();
            bt.startService(BluetoothState.DEVICE_OTHER);

        }
        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceDisconnected() {
//                layone.setVisibility(View.INVISIBLE);
                Log.d("onDeviceDisconnected", "onDeviceDisconnected2");

                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(Terminal.mBroadcastStringAction);
                broadcastIntent.putExtra("Status", "onDeviceDisconnected");
                sendBroadcast(broadcastIntent);
            }

            public void onDeviceConnectionFailed() {
//                textStatus.setText("Status : Connection failed");
                Log.d("onDeviceConnectFailed", "onDeviceConnectionFailed");

                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(Terminal.mBroadcastStringAction);
                broadcastIntent.putExtra("Status", "onDeviceConnectFailed");
                sendBroadcast(broadcastIntent);
            }

            public void onDeviceConnected(String name, String address) {
//                ShowAct();

                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(Terminal.mBroadcastStringAction);
                broadcastIntent.putExtra("Status", "onDeviceConnect");
                broadcastIntent.putExtra("StatusName", name);
                sendBroadcast(broadcastIntent);

                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                tStart = dateFormat.format(date);
                timeStart2 = dateFormat2.format(date);
                Log.v("TimeStartRss", tStart.toString() + " | " + address);

                callHttpGET("http://www.tqfsmart.info/delUbidots.php");
                callHttpGET("http://www.tqfsmart.info/delUbidots2.php");
//                callHttpGET("http://www.tqfsmart.info/delUbidotsG.php");
                bt.send("reset", true);
            }

        });

        // Create Google API Client instance
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        googleApiClient.connect();
        googleApiClient.isConnecting();
        Log.v("isConnect",googleApiClient.isConnecting()+"");

        callHttpGET("http://www.tqfsmart.info/delUbidots.php");
        callHttpGET("http://www.tqfsmart.info/delUbidots2.php");
//                callHttpGET("http://www.tqfsmart.info/delUbidotsG.php");
//
//        bt.setAutoConnectionListener(new BluetoothSPP.AutoConnectionListener() {
//            public void onNewConnection(String name, String address) {
//                Log.i("Check", "New Connection - " + name + " - " + address);
//            }
//
//            public void onAutoConnectionStarted() {
//                Log.i("Check", "Auto connection started");
//            }
//        });
//        startService(new Intent(this, SendLocation.class));

//        DataRecived();

    }

    @Override
    public void onDestroy() {
        bt.disconnect();
//        Intent broadcastIntent = new Intent();
//        broadcastIntent.setAction(Terminal.mBroadcastStringAction);
//        broadcastIntent.putExtra("Status", "onDeviceDisconnected");
//        sendBroadcast(broadcastIntent);

        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }

        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
        Log.v("service done","service done");
    }


    @Override
    public int onStartCommand(final Intent intent, int flags, int startId){
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String datas = extras.getString("address");
            if (datas != null) {
                bt.connect(datas);
            }
        }
        new Thread(new Runnable() {
            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            public void run() {
                Log.v("RSS", "RSS in");

                SendLocation();
                bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
                    public void onDataReceived(byte[] data, String message) {
                        Log.v("message",message);
                        // creating connection detector class instance
                        cd = new ConnectionDetector(getApplicationContext());
                        // get Internet status
                        isInternetPresent = cd.isConnectingToInternet();
                        final long startTime = System.currentTimeMillis();

                        count = count + 1;
                        String hallostring = message;
                        String cel1 = null;
                        String cel2 = null;

                        final Toast toast = Toast.makeText(getApplicationContext(), "No Network", Toast.LENGTH_SHORT);

                        Log.i("CountRss", String.valueOf(count));
                        //Log.i("Check", "Message : " + hallostring.length());
                        //Toast.makeText(Terminal.this, hallostring.length(), Toast.LENGTH_SHORT).show();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        Date date = new Date();
                        if (isInternetPresent) {
                            toast.cancel();
                            if (count >= 120) {//300
                                count = 1;
                                bt.send("reset", true);
                            }
                            if (count <= 2) {//6
                                if (hallostring.length() == 25) {
//                                    SendLocation();//send location
                                    DecimalFormat dfm = new DecimalFormat("0.00");

                                    cel1 = hallostring.substring(14, 19);
                                    float Dcel1 = Float.parseFloat(cel1);

                                    cel2 = hallostring.substring(20, 25);
                                    float Dcel2 = Float.parseFloat(cel2);

                                    Log.v("cel1", cel1);
                                    Log.v("cel2", cel2);

                                    SharedPreferences sp = getSharedPreferences("TempNotification", Context.MODE_PRIVATE);
                                    float maxnode1 = sp.getFloat("MaxNode1", 25);
                                    float minnode1 = sp.getFloat("MinNode1", 20);
                                    float maxnode2 = sp.getFloat("MaxNode2", 25);
                                    float minnode2 = sp.getFloat("MinNode2", 20);

                                    Intent broadcastIntent = new Intent();
                                    broadcastIntent.setAction(Terminal.mBroadcastStringAction);
                                    broadcastIntent.putExtra("numcel", cel1);
                                    sendBroadcast(broadcastIntent);

                                    new ApiUbidots().execute(Double.valueOf(Dcel1));//send to ubidots

                                    Log.i("Dcel1 : maxnode1", Dcel1 + " " + Float.toString(maxnode1));
                                    if (Dcel1 > maxnode1) {
                                        Toast.makeText(getApplicationContext(), "ขณะนี้เซนเซอร์ที่1อุณหภูมิมากกว่า " + maxnode1 + " C", Toast.LENGTH_SHORT).show();
                                        createNotificationH("ขณะนี้เซนเซอร์ที่1อุณหภูมิมากกว่า " + maxnode1 + " C");
                                    }
                                    if (Dcel1 <= minnode1) {
                                        Toast.makeText(getApplicationContext(), "ขณะนี้เซนเซอร์ที่1อุณหภูมิต่ำกว่า " + minnode1 + " C", Toast.LENGTH_SHORT).show();
                                        createNotificationL("ขณะนี้เซนเซอร์ที่1อุณหภูมิต่ำกว่า " + minnode1 + " C");
                                    }

                                    broadcastIntent.setAction(Terminal.mBroadcastStringAction);
                                    broadcastIntent.putExtra("numcel2", cel2);
                                    sendBroadcast(broadcastIntent);

                                    new ApiUbidots2().execute(Double.valueOf(Dcel2));//send to ubidots
                                    Log.i("Dcel2 : maxnode2", Dcel2 + " " + Float.toString(maxnode2));
                                    if (Dcel2 > maxnode2) {
                                        Toast.makeText(getApplicationContext(), "ขณะนี้เซนเซอร์ที่2อุณหภูมิมากกว่า " + maxnode2 + " C", Toast.LENGTH_SHORT).show();
                                        createNotificationH("ขณะนี้เซนเซอร์ที่2อุณหภูมิมากกว่า " + maxnode2 + " C");
                                    }
                                    if (Dcel2 <= minnode2) {
                                        numcel2.setTextColor(Color.parseColor("#FF0000"));
                                        Toast.makeText(getApplicationContext(), "ขณะนี้เซนเซอร์ที่2อุณหภูมิต่ำกว่า " + minnode2 + " C", Toast.LENGTH_SHORT).show();
                                        createNotificationL("ขณะนี้เซนเซอร์ที่2อุณหภูมิต่ำกว่า " + minnode2 + " C");
                                    }
                                    insertTOserverTemp(dateFormat.format(date), Double.valueOf(cel1), Double.valueOf(cel2));//insert temp to server
                                }
                            }
                            if (hallostring.length() < 25) {//Result Vector 1050
                                SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                                String node[] = hallostring.split("\\,");
                                if (node.length == 3) {
                                    double result = CalResultVator(Integer.parseInt(node[0]), Integer.parseInt(node[1]), Integer.parseInt(node[2]));

                                    Intent broadcastIntent = new Intent();
                                    broadcastIntent.setAction(Terminal.mBroadcastStringAction);
                                    broadcastIntent.putExtra("txtResult", String.valueOf(result));
                                    broadcastIntent.putExtra("x", node[0].toString());
                                    broadcastIntent.putExtra("y", node[1].toString());
                                    broadcastIntent.putExtra("z", node[2].toString());
                                    sendBroadcast(broadcastIntent);

                                    if (result > 1000 && overRange == 0) {
                                        insertGyroTOserver(dateFormat2.format(date), node[0].toString(), node[1].toString(), node[2].toString());
                                        overRange = 1;
                                    }
                                    if (overRange == 1) {
                                        insertGyroTOserver(dateFormat2.format(date), node[0].toString(), node[1].toString(), node[2].toString());
                                        if (result < 1000) {
                                            overRange = 0;
                                        }
                                    }
                                }
                            }
                            if (nointernet == 1) {//when have internet send old data
                                toast.cancel();
                                for (int i = 0; i < Valuenode1.size(); i++) {
                                    final double OfflineValue1 = Valuenode1.get(i);
                                    final double OfflineValue2 = Valuenode2.get(i);

                                    insertTOserverTemp(TValuenode1.get(i), OfflineValue1, OfflineValue2);//insert temp to server
                                }
                                for (int i = 0; i < Tgyro.size(); i++) {
                                    Log.v("Size gyro", String.valueOf(Tgyro.size()));
                                    insertGyroTOserver(Tgyro.get(i), OfflineX.get(i), OfflineY.get(i), OfflineZ.get(i));
                                }
                                nointernet = 0;
                            }
                        } else {//no internet
                            nointernet = 1;

                            if (count >= 120) {//300
                                for (int i = 0; i < Valuenode1.size(); i++) {
                                    final double v = Valuenode1.get(i);
                                    Log.v("Valuenode1 R" + i + " ", TValuenode1.get(i) + " " + String.valueOf(v));
                                }
                                for (int i = 0; i < Valuenode2.size(); i++) {
                                    final double v = Valuenode2.get(i);
                                    Log.v("Valuenode2 R" + i + " ", TValuenode2.get(i) + " " + String.valueOf(v));
                                }
                                count = 1;
                                bt.send("reset", true);
                            }
                            if (count <= 2) {
                                toast.show();
                                if (hallostring.length() == 25) {
                                    cel1 = hallostring.substring(14, 19);

                                    cel2 = hallostring.substring(20, 25);

                                    TValuenode1.add(dateFormat.format(date));
                                    Valuenode1.add(Double.valueOf(cel1));
                                    Log.v("Valuenode1", Valuenode1.toString());

                                    TValuenode2.add(dateFormat.format(date));
                                    Valuenode2.add(Double.valueOf(cel2));
                                    Log.v("Valuenode2", Valuenode2.toString());
                                }
                            }
                            if (hallostring.length() < 25) {//Result Vector 1050
                                SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                //Log.i("hallostring", hallostring);
                                String node[] = hallostring.split("\\,");
                                if (node.length == 3) {

                                    double result = CalResultVator(Integer.parseInt(node[0]), Integer.parseInt(node[1]), Integer.parseInt(node[2]));
                                    if (result > 1000) {
                                        Log.v("Report Gyro", "Have result > 1000");
                                        Tgyro.add(dateFormat2.format(date));
                                        OfflineX.add(node[0].toString());
                                        OfflineY.add(node[1].toString());
                                        OfflineZ.add(node[2].toString());
                                        //insertGyroTOserver(dateFormat2.format(date),node[0].toString(), node[1].toString(), node[2].toString());
                                    }
                                }
                            }
                        }
                    }

                });



//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                broadcastIntent.setAction(Terminal.mBroadcastIntegerAction);
//                broadcastIntent.putExtra("Data", 10);
//                sendBroadcast(broadcastIntent);

            }
        }).start();
        return START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    double lat= (double) 0.00;
    double lng= (double) 0.00;

    @Override
    public void onConnected(Bundle bundle) {
        SendLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        lat=(double)location.getLatitude();
        lng=(double)location.getLongitude();

        Log.v("onLocationChanged",lng+" "+lat);
        Log.v("onLocationChanged2",location.getLongitude()+" "+location.getLatitude());
        insertTOserverLocation(dateFormat.format(date), location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

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
            return null;
        }
    }

    int count=0;
    public Double CalResultVator(int x, int y, int z){
        double result=0;
        double resultVactor=0;
        result=((x*x)+(y*y)+(z*z));
        resultVactor=Math.sqrt(result);
        Log.v("CalResultVator", String.valueOf(resultVactor));
        return resultVactor;
    }

    public void callHttpGET(String url) {
        try {

            PostForm p=new PostForm(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    String json="";
    public void insertTOserverLocation(final String dt, final Double lati, final Double longti){
        try {
            Log.v("insertTOserverLocation", lati + " " + longti);
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            final String SITE_URL = "http://tqfsmart.info/addLocation.php";

            RequestBody formBody = new FormEncodingBuilder()
                    .add("isAdd", "true")
                    .add("imei", telephonyManager.getDeviceId())
                    .add("time", dt)
                    .add("la", Double.toString(lati))
                    .add("lo", Double.toString(longti))
                    .build();

            PostForm p= new PostForm(formBody,SITE_URL);
            final Toast toast = Toast.makeText(getApplicationContext(), "location Get", Toast.LENGTH_SHORT);
            toast.show();
        }
        catch (Exception ex){
            Log.v("ex",ex.toString());
        }
    }
    public void insertTOserverTemp(final String dt,final double temp1, final double temp2){
        try{
            Log.v("insertTOserverTemp",temp1+" / "+temp2);
            final String SITE_URL = "http://tqfsmart.info/addDATA.php";

            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            RequestBody formBody = new FormEncodingBuilder()
                    .add("isAdd", "true")
                    .add("imei", telephonyManager.getDeviceId())
                    .add("time", dt)
                    .add("node1", String.valueOf(temp1))
                    .add("node2", String.valueOf(temp2))
                    .build();

            PostForm p= new PostForm(formBody,SITE_URL);
            final Toast toast = Toast.makeText(getApplicationContext(), "Temp Get", Toast.LENGTH_SHORT);
            toast.show();
//            com.squareup.okhttp.Request.Builder builder=new com.squareup.okhttp.Request.Builder();
//            builder.post(formBody)
//                    .url(SITE_URL);
//
//            builder.build();
//
//            com.squareup.okhttp.Request request=builder.build();
//
//            OkHttpClient client=new OkHttpClient();
//            Call call=client.newCall(request);
//
//            call.enqueue(new Callback() {
//                @Override
//                public void onFailure(com.squareup.okhttp.Request request, IOException e) {
//                    e.printStackTrace();
//                }
//                @Override
//                public void onResponse(com.squareup.okhttp.Response response) throws IOException {
//                    json = response.body().string();
//
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            Log.v("checkrun", json);
//                        }
//                    });
//
//                }
//            });
        }
        catch (Exception ex){
            Log.v("ex",ex.toString());
        }
    }
    public void insertGyroTOserver(final String dt,final String x, final String y, final String z)
    {
        if(lat!=0 || lng!=0) {
            try {
                LocationAvailability locationAvailability = LocationServices.FusedLocationApi.getLocationAvailability(googleApiClient);
                if (locationAvailability.isLocationAvailable()) {
                    // Call Location Services
                    LocationRequest locationRequest = new LocationRequest()
                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
                }
//                String queryString = createQueryString(params);
                final String SITE_URL = "http://tqfsmart.info/addGyro.php";
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                RequestBody formBody = new FormEncodingBuilder()
                        .add("isAdd", "true")
                        .add("imei", telephonyManager.getDeviceId())
                        .add("time",dt)
                        .add("x", x)
                        .add("y", y)
                        .add("z", z)
                        .add("lat",String.valueOf(lat))
                        .add("lng",String.valueOf(lng))
                        .build();
                PostForm p= new PostForm(formBody,SITE_URL);
                final Toast toast = Toast.makeText(getApplicationContext(), "Gyro Get", Toast.LENGTH_SHORT);
                toast.show();
//                com.squareup.okhttp.Request.Builder builder=new com.squareup.okhttp.Request.Builder();
//                builder.post(formBody)
//                        .url(SITE_URL);
//
//                builder.build();
//
//                com.squareup.okhttp.Request request=builder.build();
//
//                OkHttpClient client=new OkHttpClient();
//                Call call=client.newCall(request);
//
//                call.enqueue(new Callback() {
//                    @Override
//                    public void onFailure(com.squareup.okhttp.Request request, IOException e) {
//                        e.printStackTrace();
//                    }
//                    @Override
//                    public void onResponse(com.squareup.okhttp.Response response) throws IOException {
//                        json = response.body().string();
//
//                        handler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                Log.v("checkrun", json);
//                            }
//                        });
//
//                    }
//                });
            }
            catch (Exception ex) {
                Log.v("ex", ex.toString());
            }
        }
    }
    public void createNotificationH(String m) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Notification notification = new Notification(android.R.drawable.stat_notify_error,
                "New notification", System.currentTimeMillis());

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        String Title = "Warning";
        String Message = m;

        Intent intent = new Intent(this, TemperatureHigh.class);
        PendingIntent activity = PendingIntent.getActivity(this, 0, intent, 0);
        notification.setLatestEventInfo(this, Title, Message, activity);
        notification.number += 1;
        notification.defaults = Notification.DEFAULT_ALL; // Sound Vibrate Light
        notificationManager.notify(1, notification);
    }
    public void createNotificationL(String m) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Notification notification = new Notification(android.R.drawable.stat_notify_error,
                "New notification", System.currentTimeMillis());

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        String Title = "Warning";
        String Message = m;

        Intent intent = new Intent(this, TemperatureLow.class);
        PendingIntent activity = PendingIntent.getActivity(this, 0, intent, 0);
        notification.setLatestEventInfo(this, Title, Message, activity);
        notification.number += 1;
        notification.defaults = Notification.DEFAULT_ALL; // Sound Vibrate Light
        notificationManager.notify(1, notification);
    }

    public void SendLocation(){
        try {
            LocationAvailability locationAvailability = LocationServices.FusedLocationApi.getLocationAvailability(googleApiClient);
            if (locationAvailability.isLocationAvailable()) {
                // Call Location Services
                LocationRequest locationRequest = new LocationRequest()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setInterval(20000)//1second
                        .setFastestInterval(10000);//5second
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
                Log.v("SendLocation",locationAvailability.toString());
            } else {
                // Do something when Location Provider not available
                Toast.makeText(getApplicationContext()
                        , "Please Open Location Provider"
                        , Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e){
            Log.v("SendLocation exception",e.toString());
        }
    }

    public static String createQueryString(HashMap<String, String> queryParameters)
    {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (String key : queryParameters.keySet())
        {
            String value = queryParameters.get(key).toString();

            try
            {
                String encodedValue = URLEncoder.encode(value, "UTF-8");
//                System.out.println("Value: "+encodedValue);

                if(first)
                {
                    builder.append("?");
                    first = false;
                }
                else
                {
                    builder.append("&");
                }

                builder.append(key).append("=").append(encodedValue);
            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
        }

        return builder.toString();
    }
}
