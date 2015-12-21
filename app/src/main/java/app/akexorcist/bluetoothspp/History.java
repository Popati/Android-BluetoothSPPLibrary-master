package app.akexorcist.bluetoothspp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class History extends Activity {

	private String jsonResult;
	private String url = "http://www.tqfsmart.info/ListHistory.php";
	private ListView listView;
	ProgressBar p;

	// flag for Internet connection status
	Boolean isInternetPresent = false;
	// Connection detector class
	ConnectionDetector cd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);

		cd = new ConnectionDetector(getApplicationContext());
		// get Internet status
		isInternetPresent = cd.isConnectingToInternet();
		if(isInternetPresent) {

			//populateListView();

			listView = (ListView) findViewById(R.id.listView1);
			accessWebService();
			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
										int position, long id) {
					// TODO Auto-generated method stub
					TextView textView = (TextView) view;
					String message = "You clicked #" + position
							+ ", whick is string: " + textView.getText().toString();

					String first = textView.getText().toString().substring(0, 10);
					String last = textView.getText().toString().substring(19, 30);
					String sfirst = textView.getText().toString().substring(11, 16);
					String slast = textView.getText().toString().substring(29, 35);
					//Toast.makeText(History.this, slast, Toast.LENGTH_LONG).show();
					Log.v("onclickGraph", first + " " + last + " " + sfirst + " " + slast);
					onclickGraph(first, last, sfirst, slast);
				}

			});
		}
		else {
			Toast.makeText(getApplicationContext(),"No Network", Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	Double timeStart,timeStop;
    String timeStart2,timeStop2;
	public void onclickGraph(String t1,String t2,String st1,String st2){
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		SharedPreferences sp = getSharedPreferences("TimeHistory", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.clear();
		editor.putString("Page", "History");
		editor.putString("tStart", st1);
		editor.putString("TimeStart2", t1);
		editor.putString("tStop", st2);
		editor.putString("TimeStop2", t2);
		editor.putString("imei",telephonyManager.getDeviceId());
		editor.commit();

		Intent intent = new Intent(this,MainHistory.class);
		intent.putExtra("Page","History");
		intent.putExtra("tStart", st1);
		intent.putExtra("TimeStart2", t1);
		intent.putExtra("tStop", st2);
		intent.putExtra("TimeStop2", t2);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
    }

	// Async Task to access the web
	private class JsonReadTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(params[0]);
			try {
				HttpResponse response = httpclient.execute(httppost);
				jsonResult = inputStreamToString(
						response.getEntity().getContent()).toString();
			}

			catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		private StringBuilder inputStreamToString(InputStream is) {
			String rLine = "";
			StringBuilder answer = new StringBuilder();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));

			try {
				while ((rLine = rd.readLine()) != null) {
					answer.append(rLine);
				}
			}

			catch (IOException e) {
				// e.printStackTrace();
				Toast.makeText(getApplicationContext(),
						"Error..." + e.toString(), Toast.LENGTH_LONG).show();
			}
			return answer;
		}

		@Override
		protected void onPostExecute(String result) {
			ListDrwaer();
		}
	}// end async task

	public void accessWebService() {
		JsonReadTask task = new JsonReadTask();
		// passes values for the urls string array
		task.execute(new String[] { url });
		p=(ProgressBar) findViewById(R.id.progressBar);
		p.setVisibility(View.INVISIBLE);
	}

	// build hash set for list view
	public void ListDrwaer() {
		List<Map<String, String>> employeeList = new ArrayList<Map<String, String>>();

		try {
			JSONObject jsonResponse = new JSONObject(jsonResult);
			JSONArray jsonMainNode = jsonResponse.optJSONArray("detail_history");

			for (int i = 0; i < jsonMainNode.length(); i++) {
				JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
				String dateStart = jsonChildNode.getString("DateStart");
				String time1 = jsonChildNode.getString("TimeStart");
				String dateStop = jsonChildNode.getString("DateStop");
				String time2 = jsonChildNode.getString("TimeStop");
				String outPut = dateStart+" "+time1.substring(0,5)+ " - " + dateStop+" "+time2.substring(0,5);
//				System.out.print(outPut);
				Log.v("myItems", outPut);
				employeeList.add(createEmployee("employees", outPut));
			}
		} catch (JSONException e) {
			Toast.makeText(getApplicationContext(), "Error" + e.toString(),
					Toast.LENGTH_SHORT).show();
		}

		/*SimpleAdapter simpleAdapter = new SimpleAdapter(this, employeeList,
				android.R.layout.simple_list_item_1,
				new String[] { "employees" }, new int[] { android.R.id.text1 });*/

		String[] myItems = new String[employeeList.size()];
		for (int i = 0; i < employeeList.size(); i++) {
			myItems[i] = new String(String.valueOf(employeeList.get(i)).substring(11, 46));
//			Log.v("myItems", String.valueOf(employeeList.get(i)).substring(11,46));
		}

		ArrayAdapter<String> adapter;
		adapter = new ArrayAdapter<String>(
				History.this,
				R.layout.da_item,
				myItems);

		listView.setAdapter(adapter);
	}

	private HashMap<String, String> createEmployee(String name, String number) {
		HashMap<String, String> employeeNameNo = new HashMap<String, String>();
		employeeNameNo.put(name, number);
		return employeeNameNo;
	}
}
