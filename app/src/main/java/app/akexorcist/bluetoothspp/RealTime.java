package app.akexorcist.bluetoothspp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;


public class RealTime extends AppCompatActivity implements ActionBar.TabListener {

    BluetoothSPP bt;
    TextView textStatus, numcel, numfer,temp;
    private Timer timer;
    Menu menu;
    TextView numx, numy, numz;

    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    // Tab titles
    private String[] tabs = { "Node1", "Node2" };

    private Thread thread;
    private Handler handler = new Handler();
    Double timeStart,timeStop;
    String timeStart2,timeStop2,page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tab);

//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Lock Screen
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getSupportActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }
        /**
         * on swiping the viewpager make respective tab selected
         * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        //populateListView();
        //registerClickCallback();

        //numcel = (TextView)findViewById(R.id.numcel);
        //numfer = (TextView)findViewById(R.id.numfer);


        //start bluetooth//
        bt = new BluetoothSPP(this);
        if(!bt.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /*private void registerClickCallback() {
        // TODO Auto-generated method stub
        ListView list=(ListView) findViewById(R.id.listView1);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                TextView textView=(TextView)view;
                String message="You clicked #"+position
                        +", whick is string: "+textView.getText().toString();
                Log.v("listnode", message);
                if(textView.getText().toString()=="NODE 1")
                {
                    OneFragment oneFragment = new OneFragment();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, oneFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
                if(textView.getText().toString()=="NODE 2")
                {
                    TwoFragment twoFragment = new TwoFragment();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, twoFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
                if(textView.getText().toString()=="NODE GYRO")
                {
                    ThreeFragment threeFragment = new ThreeFragment();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, threeFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        });
    }*/

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
        Intent intent = new Intent(this,GoogleGraph.class);
        intent.putExtra("Page","Terminal");
        intent.putExtra("TimeStart", timeStart);
        intent.putExtra("TimeStart2", timeStart2);
        intent.putExtra("TimeStop", timeStop);
        intent.putExtra("TimeStop2", timeStop2);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        bt.disconnect();
        startActivity(intent);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }
}