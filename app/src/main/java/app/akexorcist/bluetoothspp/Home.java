package app.akexorcist.bluetoothspp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class Home extends Activity {

	BluetoothSPP bt;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //≈ÁÕ§ÀπÈ“®Õ‰¡Ë„ÀÈÀ¡ÿπ
		
        bt = new BluetoothSPP(this);
        
        if(!bt.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext(), "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }

        final dbTracking myDb = new dbTracking(this);
        myDb.getWritableDatabase(); // First method

        /*myDb.DeleteData();//delete all data
        myDb.InsertHistory("2015-08-18 23:15", "2015-08-18 23:26");
        myDb.InsertHistory("2015-08-18 23:26", "2015-08-18 23:29");
        myDb.InsertHistory("2015-08-29 19:31", "2015-08-29 19:44");
        myDb.InsertHistory("2015-08-30 00:58", "2015-08-30 01:00");
        myDb.InsertHistory("2015-08-30 11:49", "2015-08-30 12:03");
        myDb.InsertHistory("2015-08-30 22:19", "2015-08-30 22:31");
        myDb.InsertHistory("2015-08-30 23:38", "2015-08-30 23:46");
        myDb.InsertHistory("2015-08-31 00:00", "2015-08-31 00:00");
        myDb.InsertHistory("2015-08-31 00:24", "2015-08-31 00:28");
        myDb.InsertHistory("2015-08-31 00:58", "2015-08-31 00:00");
        myDb.InsertHistory("2015-08-31 00:59", "2015-08-31 01:02");
        myDb.InsertHistory("2015-08-31 01:16", "2015-08-31 01:20");
        myDb.InsertHistory("2015-08-31 01:20", "2015-08-31 01:27");
        myDb.InsertHistory("2015-08-31 01:37", "2015-08-31 01:47");
        myDb.InsertHistory("2015-08-31 13:58", "2015-08-31 14:16");
        myDb.InsertHistory("2015-09-03 10:50", "2015-09-03 10:57");
        myDb.InsertHistory("2015-09-03 10:58", "2015-09-03 11:05");
        myDb.InsertHistory("2015-09-05 13:09", "2015-09-05 13:14");
        */
        //myDb.DropTable();

        List<dbTracking.sMembers> MebmerList = myDb.SelectAllDataTemp();
        if(MebmerList == null)
        {
            Toast.makeText(this,"Not found Data!",
                    Toast.LENGTH_LONG).show();
        }
        else {
            for (dbTracking.sMembers mem : MebmerList) {
                double value = Double.parseDouble(mem.gName());
                String str = mem.gMemberID();
                String node = mem.gTel();
                Log.v("showTempGraph", str + "," + value + "," + node + "," + MebmerList.indexOf(mem));

                //Toast.makeText(this,str,Toast.LENGTH_LONG).show();
            }
        }

        //Intent intent = new Intent(this,TemperatureHigh.class);
       // startActivity(intent);
        /*bt.setOnDataReceivedListener(new OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
        */
        /*
        bt.setBluetoothConnectionListener(new BluetoothConnectionListener() {
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext(), "Connected to " + name + "\n" + address
                        , Toast.LENGTH_SHORT).show();
                connected();
            }

            public void onDeviceDisconnected() {
                Toast.makeText(getApplicationContext(), "Connection lost"
                        , Toast.LENGTH_SHORT).show();
            }

            public void onDeviceConnectionFailed() {
                Toast.makeText(getApplicationContext(), "Unable to connect"
                        , Toast.LENGTH_SHORT).show();
                
            }
        });
        
        /*Button btnConnect = (Button)findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                if(bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    //bt.disconnect();
                } else {
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE); 
                }
            }
        }); */
        
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
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    */
    public void onclickconnect(View view){
    	Intent intent = new Intent(this,Terminal.class);
        startActivity(intent);
    }
    public void onclickHistory(View view){
    	Intent intent = new Intent(this,History.class);
    	startActivity(intent);
    }
    public void onclickLocal(View view){
        Intent intent = new Intent(this,MapsActivity.class);
        startActivity(intent);
    }
    public void onclickSetTemp(View view){
        Intent intent = new Intent(this,SetTemp.class);
        startActivity(intent);
    }
    public void onclickCSV(View view){
        Intent intent = new Intent(this,GoogleChart.class);
        startActivity(intent);
        //exporttocsv();
    }
    public void exporttocsv(){

    }

}
