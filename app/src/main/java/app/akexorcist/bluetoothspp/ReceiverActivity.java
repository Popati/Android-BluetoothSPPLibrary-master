package app.akexorcist.bluetoothspp;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;


public class ReceiverActivity extends Activity {

    int num = 0,notificationID = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

}