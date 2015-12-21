package app.akexorcist.bluetoothspp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SetTemp extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_temp);

        EditText mEdit11   = (EditText)findViewById(R.id.edit11);
        EditText mEdit12   = (EditText)findViewById(R.id.edit12);
        EditText mEdit21   = (EditText)findViewById(R.id.edit21);
        EditText mEdit22   = (EditText)findViewById(R.id.edit22);
        EditText mEdit31   = (EditText)findViewById(R.id.edit31);
        EditText mEdit32   = (EditText)findViewById(R.id.edit32);

        SharedPreferences sp = getSharedPreferences("TempNotification", Context.MODE_PRIVATE);
        float maxnode1=sp.getFloat("MaxNode1", 25);
        float minnode1=sp.getFloat("MinNode1", 20);
        float maxnode2=sp.getFloat("MaxNode2", 25);
        float minnode2=sp.getFloat("MinNode2", 20);
        float maxnode3=sp.getFloat("MaxNode3", 25);
        float minnode3=sp.getFloat("MinNode3", 20);

        mEdit11.setText(Float.toString(maxnode1));
        mEdit12.setText(Float.toString(minnode1));
        mEdit21.setText(Float.toString(maxnode2));
        mEdit22.setText(Float.toString(minnode2));
        mEdit31.setText(Float.toString(maxnode3));
        mEdit32.setText(Float.toString(minnode3));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_set_temp, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onclickSubmit(View view){
        EditText mEdit11n   = (EditText)findViewById(R.id.edit11);
        EditText mEdit12n   = (EditText)findViewById(R.id.edit12);
        EditText mEdit21n   = (EditText)findViewById(R.id.edit21);
        EditText mEdit22n   = (EditText)findViewById(R.id.edit22);
        EditText mEdit31n   = (EditText)findViewById(R.id.edit31);
        EditText mEdit32n   = (EditText)findViewById(R.id.edit32);

        //Toast.makeText(this,mEdit11n.getText().toString(),Toast.LENGTH_SHORT).show();

        if(mEdit11n.getText().toString().matches("") || mEdit12n.getText().toString().matches("")
                ||mEdit21n.getText().toString().matches("") || mEdit22n.getText().toString().matches("")
                ||mEdit31n.getText().toString().matches("") || mEdit32n.getText().toString().matches("") ){
            Toast.makeText(this,"กรุณาใส่ข้อมูลให้ครบทุกช่อง",Toast.LENGTH_SHORT).show();
        }
        if((Double.parseDouble(mEdit11n.getText().toString())) <= (Double.parseDouble(mEdit12n.getText().toString())) ||
                (Double.parseDouble(mEdit21n.getText().toString())) <= (Double.parseDouble(mEdit22n.getText().toString())) ||
                (Double.parseDouble(mEdit31n.getText().toString())) <= (Double.parseDouble(mEdit32n.getText().toString())))
        {
            Toast.makeText(this,"กรุณาใส่ข้อมูลให้ถูกต้อง",Toast.LENGTH_SHORT).show();
        }
        else {
            float maxnode11 = Float.parseFloat(mEdit11n.getText().toString());
            float minnode11 = Float.parseFloat(mEdit12n.getText().toString());
            float maxnode22 = Float.parseFloat(mEdit21n.getText().toString());
            float minnode22 = Float.parseFloat(mEdit22n.getText().toString());
            float maxnode33 = Float.parseFloat(mEdit31n.getText().toString());
            float minnode33 = Float.parseFloat(mEdit32n.getText().toString());

            SharedPreferences sp = getSharedPreferences("TempNotification", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.clear();
            editor.putFloat("MaxNode1", maxnode11);
            editor.putFloat("MinNode1", minnode11);
            editor.putFloat("MaxNode2", maxnode22);
            editor.putFloat("MinNode2", minnode22);
            editor.putFloat("MaxNode3", maxnode33);
            editor.putFloat("MinNode3", minnode33);
            editor.commit();

            Toast.makeText(getApplicationContext(), "บันทึกข้อมูลเรียบร้อย", Toast.LENGTH_SHORT).show();
            finish();
        }
        //Intent intent=new Intent(this,Home.class);
        //startActivity(intent);
    }
}
