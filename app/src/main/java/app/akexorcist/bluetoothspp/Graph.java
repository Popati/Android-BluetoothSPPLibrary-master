package app.akexorcist.bluetoothspp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import app.akexorcist.bluetoothspp.dbTracking.sMembers;

public class Graph extends Activity {

	double timeStart,timeStop;
	String timeStart2,timeStop2,page;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maingraph);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //≈ÁÕ§ÀπÈ“®Õ‰¡Ë„ÀÈÀ¡ÿπ


		Bundle bundle = getIntent().getExtras();
		timeStart = bundle.getDouble("TimeStart");
		timeStop = bundle.getDouble("TimeStop");
		
		timeStart2 = bundle.getString("TimeStart2");
		timeStop2 = bundle.getString("TimeStop2");
		
		page = bundle.getString("Page");

		java.text.DecimalFormat df = new java.text.DecimalFormat("#.##");

		timeStart = Double.valueOf(df.format(timeStart));
		timeStop = Double.valueOf(df.format(timeStop));
		//Toast.makeText(Graph.this, timeStart+" - "+timeStop, Toast.LENGTH_LONG).show();

		Log.v("timeStart",timeStart2);
		Log.v("timeStop2",timeStop2);
		Log.v("page",page);

		showTemp(timeStart2,timeStop2);

		if(page.equals("Terminal")){
			final dbTracking myDb = new dbTracking(this);
			myDb.getWritableDatabase(); // First method
	    	myDb.InsertHistory(timeStart2, timeStop2);
			insertTOserver(timeStart2, timeStop2);
			//Toast.makeText(Graph.this, page, Toast.LENGTH_LONG).show();
		}

		getIntent().removeExtra("TimeStart");
		getIntent().removeExtra("TimeStart2");
		getIntent().removeExtra("TimeStop");
		getIntent().removeExtra("TimeStop2");
		getIntent().removeExtra("Page");
	}

	public void insertTOserver(String tf, String tl){
		try {
			ArrayList<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("isAdd","true"));
			nameValuePairs.add(new BasicNameValuePair("timestart",tf));
			nameValuePairs.add(new BasicNameValuePair("timestop",tl));

			HttpClient httpclient =new DefaultHttpClient();
			HttpPost httppost=new HttpPost("http://tqfsmart.info/addHistory.php");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
			httpclient.execute(httppost);
		}
		catch (Exception e){
			Log.d("log_err","Error in http connection"+e.toString());
		}
	}
	public void showTemp(String timestart,String timestop) {
    	final dbTracking myDb = new dbTracking(this);
		myDb.getWritableDatabase(); // First method
		
    	// import com.myapp.myDBClass.sMembers;
    	List<sMembers> MebmerList = myDb.SelectAllDataTemp(timestart,timestop,"node1");
		List<sMembers> MebmerList2 = myDb.SelectAllDataTemp(timestart,timestop,"node2");
		List<sMembers> MebmerListg = myDb.SelectAllDataTemp(timestart,timestop,"nodeg");
    	if(MebmerList == null)
    	{
          	 Toast.makeText(this,"Not found Data!",
      			 	Toast.LENGTH_LONG).show(); 
    	}
    	else
    	{
    		ArrayList<Double> t = new ArrayList<Double>();
    		ArrayList<Double> v = new ArrayList<Double>();

			ArrayList<Double> t2 = new ArrayList<Double>();
			ArrayList<Double> v2 = new ArrayList<Double>();

			ArrayList<Double> tg = new ArrayList<Double>();
			ArrayList<Double> vg = new ArrayList<Double>();

			for (sMembers mem : MebmerList) {
    			Log.v("showTempGraph1", mem.gMemberID()+ ","  + mem.gName()+","+MebmerList.indexOf(mem));
    			double value = Double.parseDouble(mem.gName());
    			String str = mem.gMemberID();
				String node = mem.gTel();
    			//Toast.makeText(this,str,Toast.LENGTH_LONG).show();

				String timee = str.substring(10, 16);
				double time = Double.parseDouble(timee);

				t.add(time);
				v.add(value);
				//Toast.makeText(this,timee,Toast.LENGTH_LONG).show();

    		}
    		GraphView graph = (GraphView) findViewById(R.id.graph);

    		DataPoint[] points = new DataPoint[t.size()];
	        for (int i = 0; i < t.size(); i++) {
	            points[i] = new DataPoint(t.get(i), v.get(i));
	            Log.v("showPoints1", t.get(i)+ ","  + v.get(i));
	        }

			for (sMembers mem : MebmerList2) {
				Log.v("showTempGraph2", mem.gMemberID()+ ","  + mem.gName()+","+MebmerList.indexOf(mem));
				double value = Double.parseDouble(mem.gName());
				String str = mem.gMemberID();
				String node = mem.gTel();
				//Toast.makeText(this,str,Toast.LENGTH_LONG).show();

				String timee = str.substring(10, 16);
				double time = Double.parseDouble(timee);

				t2.add(time);
				v2.add(value);
				//Toast.makeText(this,timee,Toast.LENGTH_LONG).show();
			}
			DataPoint[] points2 = new DataPoint[t2.size()];
			for (int i = 0; i < t2.size(); i++) {
				points2[i] = new DataPoint(t2.get(i), v2.get(i));
				Log.v("showPoints2", t2.get(i)+ ","  + v2.get(i));
			}

			for (sMembers mem : MebmerListg) {
				Log.v("showTempGraph2", mem.gMemberID()+ ","  + mem.gName()+","+MebmerList.indexOf(mem));
				double value = Double.parseDouble(mem.gName());
				String str = mem.gMemberID();
				String node = mem.gTel();
				//Toast.makeText(this,str,Toast.LENGTH_LONG).show();

				String timee = str.substring(10, 16);
				double time = Double.parseDouble(timee);

				tg.add(time);
				vg.add(value);
				//Toast.makeText(this,timee,Toast.LENGTH_LONG).show();
			}
			DataPoint[] points3 = new DataPoint[tg.size()];
			for (int i = 0; i < tg.size(); i++) {
				points3[i] = new DataPoint(tg.get(i), vg.get(i));
				Log.v("showPoints2", tg.get(i)+ ","  + vg.get(i));
			}

			LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(points);
			graph.addSeries(series);
			LineGraphSeries<DataPoint> series2 = new LineGraphSeries<DataPoint>(points2);
			graph.addSeries(series2);
			LineGraphSeries<DataPoint> series3 = new LineGraphSeries<DataPoint>(points3);
			graph.addSeries(series3);

			series.setTitle("node1");
			series2.setTitle("node2");
			series3.setTitle("nodeg");

			series.setColor(Color.WHITE);
			series2.setColor(Color.RED);

			java.text.DecimalFormat df = new java.text.DecimalFormat("#.##");

			timeStart = Double.valueOf(df.format(timeStart));
			timeStop = Double.valueOf(df.format(timeStop));

	        // set manual Y bounds
	        graph.getViewport().setYAxisBoundsManual(true);
	        graph.getViewport().setMinY(0);
	        graph.getViewport().setMaxY(40);

	        // set manual X bounds
	        graph.getViewport().setXAxisBoundsManual(true);
	        graph.getViewport().setMinX(timeStart);//timeStart
	        graph.getViewport().setMaxX(timeStop);//timeStop
	        graph.onDataChanged(false, false);

	        // enable scrolling
	        graph.getViewport().setScrollable(true);
    	}
    }           

	public void onclickHome(View view){
    	Intent intent = new Intent(this,Home.class);
		intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
    }
	public void onclickNode1(View view){
		ShowSingle(timeStart2, timeStop2, "node1");
	}
	public void onclickNode2(View view){
		ShowSingle(timeStart2,timeStop2,"node2");
	}
	public void onclickNode3(View view){
		ShowSingle(timeStart2,timeStop2,"nodeg");
	}
	public void onclickNodeAll(View view){
		showTemp(timeStart2, timeStop2);
	}
	public void ShowSingle(String timestart,String timestop,String n){
		final dbTracking myDb = new dbTracking(this);
		myDb.getWritableDatabase(); // First method

		// import com.myapp.myDBClass.sMembers;
		List<sMembers> MebmerList = myDb.SelectAllDataTemp(timestart,timestop,n);
		if(MebmerList == null)
		{
			Toast.makeText(this,"Not found Data!",
					Toast.LENGTH_LONG).show();
		}
		else {
			ArrayList<Double> t = new ArrayList<Double>();
			ArrayList<Double> v = new ArrayList<Double>();

			for (sMembers mem : MebmerList) {
				Log.v("showTempGraph1", mem.gMemberID() + "," + mem.gName() + "," + MebmerList.indexOf(mem));
				double value = Double.parseDouble(mem.gName());
				String str = mem.gMemberID();
				String node = mem.gTel();
				//Toast.makeText(this,str,Toast.LENGTH_LONG).show();

				String timee = str.substring(10, 16);
				double time = Double.parseDouble(timee);

				t.add(time);
				v.add(value);
				//Toast.makeText(this,timee,Toast.LENGTH_LONG).show();

			}
			GraphView graph = (GraphView) findViewById(R.id.graph);

			DataPoint[] points = new DataPoint[t.size()];
			for (int i = 0; i < t.size(); i++) {
				points[i] = new DataPoint(t.get(i), v.get(i));
				Log.v("showPoints1", t.get(i) + "," + v.get(i));
			}

			LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(points);
			graph.removeAllSeries();
			graph.addSeries(series);

			series.setTitle("node1");

			if(n.equals("node1"))
				series.setColor(Color.WHITE);
			if(n.equals("node2"))
				series.setColor(Color.RED);
			if(n.equals("nodeg"))
				series.setColor(Color.BLUE);

			java.text.DecimalFormat df = new java.text.DecimalFormat("#.##");

			timeStart = Double.valueOf(df.format(timeStart));
			timeStop = Double.valueOf(df.format(timeStop));

			// set manual Y bounds
			graph.getViewport().setYAxisBoundsManual(true);
			graph.getViewport().setMinY(0);
			graph.getViewport().setMaxY(40);

			// set manual X bounds
			graph.getViewport().setXAxisBoundsManual(true);
			graph.getViewport().setMinX(timeStart);//timeStart
			graph.getViewport().setMaxX(timeStop);//timeStop
			graph.onDataChanged(false, false);

			// enable scrolling
			graph.getViewport().setScrollable(true);
		}
	}
}
