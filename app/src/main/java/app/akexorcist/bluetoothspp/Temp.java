package app.akexorcist.bluetoothspp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;


public class Temp extends FragmentActivity {

    BluetoothSPP bt;
    TextView textStatus, numcel, numfer,temp;
    private Timer timer;
    Menu menu;
    TextView numx, numy, numz;


    private Thread thread;
    private Handler handler = new Handler();
    Double timeStart,timeStop;
    String timeStart2,timeStop2,page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Lock Screen

        numcel = (TextView)findViewById(R.id.numcel);
        numfer = (TextView)findViewById(R.id.numfer);

        //start bluetooth//
        bt = new BluetoothSPP(this);
        if(!bt.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }

        //start event button fragment//
        Button btn_one = (Button)findViewById(R.id.btn_one);
        btn_one.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                OneFragment oneFragment = new OneFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, oneFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        Button btn_two = (Button)findViewById(R.id.btn_two);
        btn_two.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TwoFragment twoFragment = new TwoFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, twoFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        Button btn_three = (Button)findViewById(R.id.btn_three);
        btn_three.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ThreeFragment threeFragment = new ThreeFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, threeFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        //stop event button fragment//
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_temp, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings) {
            //Intent intent=new Intent(this,Terminal.class);
            //startActivity(intent);

            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onDestroy() {
        super.onDestroy();
        bt.stopService();
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
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if(resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                Log.d("onActivityResult", "WTF");
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
        startActivity(intent);
    }
}
