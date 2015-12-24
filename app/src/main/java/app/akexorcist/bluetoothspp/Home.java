package app.akexorcist.bluetoothspp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class Home extends AppCompatActivity {

	BluetoothSPP bt;
    // flag for Internet connection status
    Boolean isInternetPresent = false;
    // Connection detector class
    ConnectionDetector cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        bt = new BluetoothSPP(this);

        if (!bt.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext(), "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }
        if(bt.getServiceState() == BluetoothState.STATE_CONNECTED)
            bt.disconnect();

        stopService(new Intent(Home.this,RSSPullService.class));
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

        SharedPreferences sp = getSharedPreferences("TimeHistory", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.putString("imei", telephonyManager.getDeviceId());
        Log.v("IMEI", telephonyManager.getDeviceId());
        editor.commit();

//        final dbTracking myDb = new dbTracking(this);
//        myDb.getWritableDatabase(); // First method

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //myDb.DropTable();

//        List<dbTracking.sMembers> MebmerList = myDb.SelectAllDataTemp();
//        if (MebmerList == null) {
//            //Toast.makeText(this, "Not found Data!",Toast.LENGTH_LONG).show();
//        } else {
//            for (dbTracking.sMembers mem : MebmerList) {
//                double value = Double.parseDouble(mem.gName());
//                String str = mem.gMemberID();
//                String node = mem.gTel();
//                //Log.v("showTempGraph", str + "," + value + "," + node + "," + MebmerList.indexOf(mem));
//
//                //Toast.makeText(this,str,Toast.LENGTH_LONG).show();
//            }
//        }
        populateListView();

        final ListView list=(ListView) findViewById(R.id.listView2);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // ListView Clicked item index
                int itemPosition = position;
                // ListView Clicked item value
                String itemValue = (String) list.getItemAtPosition(position);
                Log.d("ListItem",itemValue);
                if(itemValue.equals("เริ่มบันทึกข้อมูล")){
                    onclickconnect();
                }
                else if(itemValue.equals("ประวัติการบันทึก")){
                    onclickHistory();
                }
                else if(itemValue.equals("ตั้งค่าแจ้งเตือน")){
                    onclickSetTemp();
                }
            }
        });
    }

    private void populateListView() {
//        ArrayList<String> t = new ArrayList<String>();
//
//        t.add("เริ่มบันทึก");
//        t.add("ประวัติการบันทึก");
//        t.add("ตั้งค่าแจ้งเตือนอุณหภูมิ");
//
//
//        String[] myItems = new String[t.size()];
//        for (int i = 0; i < t.size(); i++) {
//            myItems[i] = new String(t.get(i));
//            Log.v("myItems", t.get(i));
//        }
        String[] itemname ={
                "เริ่มบันทึกข้อมูล",
                "ประวัติการบันทึก",
                "ตั้งค่าแจ้งเตือน"
        };

        Integer[] imgid={
                R.drawable.i_play,
                R.drawable.i_data,
                R.drawable.i_setting,
        };
//        ArrayAdapter<String> adapter=new ArrayAdapter<String>(
//                Home.this,
//                R.layout.home_item,R.id.Itemname,
//                itemname);

        CustomListAdapter adapter=new CustomListAdapter(this, itemname, imgid);

        ListView list=(ListView)findViewById(R.id.listView2);
        list.setAdapter(adapter);
    }
    public void onStart() {
        super.onStart();
        if(!bt.isBluetoothEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            bt.setupService();
            bt.startService(BluetoothState.DEVICE_OTHER);
            //setup();
        }
    }
    
    public void setup(){
    	Intent intent = new Intent(getApplicationContext(), DeviceList.class);
    	startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE); 
    	}
    
    public void onDestroy() {
        super.onDestroy();
        bt.stopService();
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if(resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                setup();
            } else {
                Toast.makeText(getApplicationContext(), "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    public void onclickconnect(){
        // creating connection detector class instance
        cd = new ConnectionDetector(getApplicationContext());

        // get Internet status
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (isInternetPresent) {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

            SharedPreferences sp = getSharedPreferences("TimeHistory", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.clear();
            editor.putString("Page", "Home");
            editor.putString("imei",telephonyManager.getDeviceId());
            editor.commit();

            Intent intent = new Intent(Home.this,Terminal.class);
            startActivity(intent);
        } else {
            // Internet connection is not present
            // Ask user to connect to Internet
            Toast.makeText(this, "Please Connect Internet.",
                    Toast.LENGTH_LONG).show();
        }
    }
    public void onclickHistory(){
        Intent intent = new Intent(this,SelectDevice.class);
    	startActivity(intent);
    }
    public void onclickSetTemp(){
        Intent intent = new Intent(this,SetTemp.class);
        startActivity(intent);
    }
}
