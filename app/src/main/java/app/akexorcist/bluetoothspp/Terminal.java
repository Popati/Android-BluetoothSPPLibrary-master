package app.akexorcist.bluetoothspp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Sensor;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.ubidots.ApiClient;
import com.ubidots.Variable;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;

import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class Terminal extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

//    BluetoothSPP bt;
    TextView textStatus, numcel, numfer,temp,numcel2,numfer2,numcel3,numfer3,txtResult;
    private Timer timer;
    Menu menu;
    TextView numx, numy, numz;
	Sensor sensor;
	RelativeLayout l;
    LinearLayout temp1,temp2,temp3,temp4,temp5,temp6,x,y,z;
    ProgressBar p;
    ListView list;
    private IntentFilter mIntentFilter;

    // flag for Internet connection status
    Boolean isInternetPresent = false;
    // Connection detector class
    ConnectionDetector cd;

    private Thread thread;
    private Handler handler = new Handler();
    String timeStart2,timeStop2,page,tStart,tStop;
    private GoogleApiClient googleApiClient=null;

    int nointernet=0;
    ArrayList<Double> Valuenode1 = new ArrayList<>();
    ArrayList<Double> Valuenode2 = new ArrayList<>();
    ArrayList<String> TValuenode1 = new ArrayList<>();
    ArrayList<String> TValuenode2 = new ArrayList<>();

    ArrayList<String> Tgyro = new ArrayList<>();
    ArrayList<String> OfflineX = new ArrayList<>();
    ArrayList<String> OfflineY = new ArrayList<>();
    ArrayList<String> OfflineZ = new ArrayList<>();

    public static final String mBroadcastStringAction = "app.akexorcist.broadcast.string";
    public static final String mBroadcastIntegerAction = "app.akexorcist.broadcast.integer";
    public static final String mBroadcastArrayListAction = "app.akexorcist.broadcast.arraylist";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal);

//        HideAct();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(mBroadcastStringAction);
        mIntentFilter.addAction(mBroadcastIntegerAction);
        mIntentFilter.addAction(mBroadcastArrayListAction);

        // Create Google API Client instance
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        Log.i("CON", "onCreate");

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
        numcel2 = (TextView)findViewById(R.id.numcel2);
        txtResult = (TextView)findViewById(R.id.gyro);
        textStatus = (TextView)findViewById(R.id.textStatus);

        populateListView();
        final ListView list=(ListView) findViewById(R.id.listView2);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String itemValue = (String) list.getItemAtPosition(position);
                Log.d("ListItem",itemValue);
                if(itemValue.equals("หยุดการบันทึก")){
//                    bt.disconnect();
                    stopService(new Intent(Terminal.this,RSSPullService.class));
                }
                else if(itemValue.equals("กราฟปัจจุบัน")){
                    Intent intent=new Intent(Terminal.this,RealTime.class);
                    startActivity(intent);
                }
                else if(itemValue.equals("ตั้งค่าแจ้งเตือน")){
                    Intent intent=new Intent(Terminal.this,SetTemp.class);
                    startActivity(intent);
                }
            }
        });
        //Choose item select
        p=(ProgressBar) findViewById(R.id.progressBar);
        p.setVisibility(View.INVISIBLE);
//
//        bt.setDeviceTarget(BluetoothState.DEVICE_OTHER);
//
//        if(bt.getServiceState() == BluetoothState.STATE_CONNECTED)
//            bt.disconnect();

        Intent intent = new Intent(getApplicationContext(), DeviceList.class);
        startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if(resultCode == Activity.RESULT_OK) {
                String address = data.getExtras().getString(BluetoothState.EXTRA_DEVICE_ADDRESS);
                Intent service=new Intent(this, RSSPullService.class);
                service.putExtra("address", address);
                startService(service);

//                bt.connect("30:14:09:23:05:19");
                Log.v("data", address);
            }
        }
        else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_OK) {
//                bt.setupService();
//                bt.startService(BluetoothState.DEVICE_OTHER);

//                bt.send("reset", true);

                Log.d("onActivityResult", "setup");
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    float Dcel1 = (float) 0.00;
    float Dcel2 = (float) 0.00;
    private BroadcastReceiver mReceiver=new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {

            cd = new ConnectionDetector(getApplicationContext());
            isInternetPresent = cd.isConnectingToInternet();

            count = count + 1;

            final Toast toast = Toast.makeText(getApplicationContext(),"No Network", Toast.LENGTH_SHORT);

            if (intent.getAction().equals(mBroadcastStringAction)) {
                if(intent.getStringExtra("Status") !=null) {
                    String status=(intent.getStringExtra("Status"));
                    Log.v("Status Page",status);
                    Log.v("Status context",getApplicationContext().toString());

                    if(status.equals("onDeviceDisconnected")){
//                        /stopService(new Intent(Terminal.this,RSSPullService.class));
                        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                        SharedPreferences sp = getSharedPreferences("TimeHistory", Context.MODE_PRIVATE);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
                        Date date = new Date();
                        tStop = dateFormat.format(date);
                        timeStop2 = dateFormat2.format(date);

                        SharedPreferences.Editor editor = sp.edit();
                        editor.clear();
                        editor.putString("Page", "Terminal");
                        editor.putString("tStart", tStart);
                        editor.putString("TimeStart2", timeStart2);
                        editor.putString("tStop", tStop);
                        editor.putString("TimeStop2", timeStop2);
                        editor.putString("imei",telephonyManager.getDeviceId());
                        Log.v("shared", tStart + " " + timeStart2 + "  " + tStop + " " + timeStop2);
                        editor.commit();

                        Intent in = new Intent(Terminal.this, MainHistory.class);
                        in.putExtra("Page", "Terminal");
                        in.putExtra("tStart", tStart);
                        in.putExtra("TimeStart2", timeStart2);
                        in.putExtra("tStop", tStop);
                        in.putExtra("TimeStop2", timeStop2);
                        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        bt.disconnect();
                        Log.v("onDeviceDisconnected","onDeviceDisconnected1");
                        startActivity(in);
                    }
                    if(status.equals("onDeviceConnectFailed")){
                        textStatus.setText("Status : Connection failed");
                        stopService(new Intent(Terminal.this,RSSPullService.class));
                        Toast.makeText(getApplicationContext(), "Device Connection Failed Go to back page", Toast.LENGTH_LONG).show();

                        Intent in = new Intent(Terminal.this, Home.class);
                        Log.v("onDeviceConnectFailed","onDeviceConnectFailed HOme");
                        startActivity(in);
                    }
                    if(status.equals("onDeviceConnect")){
                        if(intent.getStringExtra("StatusName") !=null) {
                            textStatus.setText("Status : Connected to " + intent.getStringExtra("StatusName"));
                            menu.clear();
                            getMenuInflater().inflate(R.menu.menu_disconnection, menu);

                            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                            SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
                            Date date = new Date();
                            tStart = dateFormat.format(date);
                            timeStart2 = dateFormat2.format(date);
                            Log.v("TimeStartRss", tStart.toString() + " | " + timeStart2.toString());
                        }
                    }
                }
            }

            Log.i("Count Terminal", String.valueOf(count));

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = new Date();

            toast.cancel();

            SharedPreferences sp = getSharedPreferences("TempNotification", Context.MODE_PRIVATE);
            float maxnode1 = sp.getFloat("MaxNode1", 25);
            float minnode1 = sp.getFloat("MinNode1", 20);
            float maxnode2 = sp.getFloat("MaxNode2", 25);
            float minnode2 = sp.getFloat("MinNode2", 20);

            if (intent.getAction().equals(mBroadcastStringAction)) {
                if(intent.getStringExtra("numcel") !=null) {
                    numcel.setText(intent.getStringExtra("numcel") + " C ํ");
                    Dcel1 = Float.parseFloat(intent.getStringExtra("numcel"));
                }
            }
            if (intent.getAction().equals(mBroadcastStringAction)) {
                if(intent.getStringExtra("numcel2") !=null) {
                    numcel2.setText(intent.getStringExtra("numcel2") + " C ํ");
                    Dcel2 = Float.parseFloat(intent.getStringExtra("numcel2"));
                }
            }
            numcel.setTextColor(Color.parseColor("#050505"));

            Log.i("Dcel1 : maxnode1", Dcel1 + " " + Float.toString(maxnode1));
            if (Dcel1 > maxnode1) {
                numcel.setTextColor(Color.parseColor("#FF0000"));
            }
            if (Dcel1 <= minnode1) {
                numcel.setTextColor(Color.parseColor("#c4c403"));
            }

            numcel2.setTextColor(Color.parseColor("#050505"));

            Log.i("Dcel2 : maxnode2", Dcel2 + " " + Float.toString(maxnode2));
            if (Dcel2 > maxnode2) {
                numcel2.setTextColor(Color.parseColor("#FF0000"));
            }
            if (Dcel2 <= minnode2) {
                numcel2.setTextColor(Color.parseColor("#c4c403"));
            }
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String x="";
            String y="";
            String z="";
            double result=0.0;
            if (intent.getAction().equals(mBroadcastStringAction)) {
                if(intent.getStringExtra("txtResult")!=null && intent.getStringExtra("x")!=null
                        && intent.getStringExtra("y")!=null && intent.getStringExtra("z")!=null ) {
                    txtResult.setText(intent.getStringExtra("txtResult"));
                    x = intent.getStringExtra("x");
                    y = intent.getStringExtra("y");
                    z = intent.getStringExtra("z");

                    result = CalResultVator(Integer.parseInt(x), Integer.parseInt(y), Integer.parseInt(z));
                    txtResult.setText(String.valueOf(result));
                }
            }
        }
    };

    int overRange=0;
    private void setup() {
        //bt.autoConnect("HC-05");
        //bt.send("reset",true);
    }

    @Override
    public void onConnected(Bundle bundle) {

//        Toast.makeText(this,"connect location api",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this,"onConnectionSuspended",Toast.LENGTH_LONG).show();
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this,"onConnectionFailed",Toast.LENGTH_LONG).show();
    }

    float lat= (float) 0.00;
    float lng= (float) 0.00;
    @Override
    public void onLocationChanged(Location location) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        lat=(float)location.getLatitude();
        lng=(float)location.getLongitude();
//        insertTOserverLocation(dateFormat.format(date), location.getLatitude(), location.getLongitude());
    }

    public class ApiUbidotsG extends AsyncTask<Double, Void, Void> {
        private final String API_KEY = "ce3bdae0671276a0a8f26f8655def30839f5a894";
        private final String VARIABLE_ID = "55a738317625422a17882088";

        @Override
        protected Void doInBackground(Double... params) {
            try {
                ApiClient apiClient = new ApiClient(API_KEY);
                Variable TempLevel = apiClient.getVariable(VARIABLE_ID);

                TempLevel.saveValue(params[0]);
            }
            catch (Exception ex){
                Log.v("Exception UbidotsG",ex.toString());
            }
            return null;
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_connection, menu);
        return true;
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d("CON", "in onDestroy");

        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        stopService(new Intent(Terminal.this, RSSPullService.class));
        //Log.v("CON",cooo+"");
    }

    public void onStart() {
        super.onStart();
        Log.d("onStart", "in onStart");

        googleApiClient.connect();
    }
    public void onResume() {
        super.onResume();
        Log.d("CON", "in onResume");

        registerReceiver(mReceiver, mIntentFilter);

        SharedPreferences sp = getSharedPreferences("TimeHistory", Context.MODE_PRIVATE);
        page = sp.getString("Page", "");

        Log.d("Page", page);
        if(page.equals("MainHistory")) {
            Intent intent=new Intent(Terminal.this,Home.class);
            startActivity(intent);
        }
//        stopService(new Intent(this, RSSPullService.class));
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mReceiver);
        super.onPause();
//        startService(new Intent(this, RSSPullService.class));
        Log.d("CON", "onPause");
    }

    public void onStop() {
		super.onStop();

	}

    int count=0;
    private Double CalResultVator(int x,int y,int z){
        double result=0;
        double resultVactor=0;
        result=((x*x)+(y*y)+(z*z));
        resultVactor=Math.sqrt(result);
        Log.v("CalResultVator", String.valueOf(resultVactor));
        return resultVactor;
    }

    private void populateListView() {
        String[] itemname ={
                "หยุดการบันทึก",
                "กราฟปัจจุบัน",
                "ตั้งค่าแจ้งเตือน"
        };

        Integer[] imgid={
                R.drawable.i_stop,
                R.drawable.i_graph,
                R.drawable.i_setting,
        };
        CustomListAdapter adapter=new CustomListAdapter(this, itemname, imgid);

        ListView list=(ListView)findViewById(R.id.listView2);
        list.setAdapter(adapter);
    }
    private void HideAct() {
        textStatus=(TextView) findViewById(R.id.textStatus);
        textStatus.setVisibility(View.INVISIBLE);
        temp=(TextView) findViewById(R.id.temp);
        temp.setVisibility(View.INVISIBLE);
        temp=(TextView) findViewById(R.id.gyro);
        temp.setVisibility(View.INVISIBLE);

        temp1=(LinearLayout) findViewById(R.id.temp1);
        temp1.setVisibility(View.INVISIBLE);

        p=(ProgressBar) findViewById(R.id.progressBar);
        p.setVisibility(View.INVISIBLE);
        list=(ListView) findViewById(R.id.listView2);
        list.setVisibility(View.INVISIBLE);
        //l=(RelativeLayout) findViewById(R.id.layout);
        //l.setVisibility(View.INVISIBLE);

    }
    private void ShowAct() {
        //l=(RelativeLayout) findViewById(R.id.layout);
        //l.setVisibility(View.VISIBLE);

        textStatus=(TextView) findViewById(R.id.textStatus);
        textStatus.setVisibility(View.VISIBLE);
        temp=(TextView) findViewById(R.id.temp);
        temp.setVisibility(View.VISIBLE);
        temp=(TextView) findViewById(R.id.gyro);
        temp.setVisibility(View.VISIBLE);

        temp1=(LinearLayout) findViewById(R.id.temp1);
        temp1.setVisibility(View.VISIBLE);

        p=(ProgressBar) findViewById(R.id.progressBar);
        p.setVisibility(View.INVISIBLE);
        list=(ListView) findViewById(R.id.listView2);
        list.setVisibility(View.VISIBLE);
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