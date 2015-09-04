package app.akexorcist.bluetoothspp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.akexorcist.bluetoothspp.dbTracking.sHistory;

public class History extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		
		populateListView();
		registerClickCallback();
	}

	
	private void registerClickCallback() {
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
				
				String first=textView.getText().toString().substring(0, 10);
				String last=textView.getText().toString().substring(19, 29);
				String sfirst=textView.getText().toString().substring(11, 16);
				String slast=textView.getText().toString().substring(30, 35);
				//Toast.makeText(History.this, slast, Toast.LENGTH_LONG).show();
				Log.v("onclickGraph", first + " " + last + " " + sfirst + " " + slast);
				onclickGraph(first, last, sfirst, slast);
			}
			
		}); 
		
	}

	private void populateListView() {
		dbTracking myDb = new dbTracking(this);
		myDb.getWritableDatabase(); // First method
		ArrayList<String> t = new ArrayList<String>();
	
      	List<sHistory> MebmerList = myDb.SelectAllDataHistory();   
      	if(MebmerList == null)
      	{
            	 Toast.makeText(History.this,"Not found Data!",
        			 	Toast.LENGTH_LONG).show(); 
      	}
      	else
      	{	
      		for (sHistory mem : MebmerList) {
      			t.add(mem.gT1()+" - "+mem.gT2());
      		}
      	}
		String[] myItems = new String[t.size()];
		for (int i = 0; i < t.size(); i++) {
			myItems[i] = new String(t.get(i));
            Log.v("myItems", t.get(i));
        }
		//List<sHistory> myItems2 = myDb.SelectAllDataHistory();
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(
				this,
				R.layout.da_item,
				myItems);
		
		ListView list=(ListView)findViewById(R.id.listView1);
		list.setAdapter(adapter);
	}

	Double timeStart,timeStop;
    String timeStart2,timeStop2;
	public void onclickGraph(String t1,String t2,String st1,String st2){
 		/*timeStart= Double.parseDouble(st1);
 		timeStart2= t1;
 		timeStop= Double.parseDouble(st2);
 		timeStop2= t2;
    	//Log.v("TimeStart",timeStop.toString());
    	/*Intent intent = new Intent(this,Graph.class);
    	intent.putExtra("TimeStart", timeStart);
    	intent.putExtra("TimeStart2", timeStart2);
    	intent.putExtra("TimeStop", timeStop);
    	intent.putExtra("TimeStop2", timeStop2);
    	intent.putExtra("Page", "History");
        startActivity(intent);*/

		Intent intent = new Intent(this,GoogleGraph.class);
		intent.putExtra("Page","History");
		intent.putExtra("tStart", st1);
		intent.putExtra("TimeStart2", t1);
		intent.putExtra("tStop", st2);
		intent.putExtra("TimeStop2", t2);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.history, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
