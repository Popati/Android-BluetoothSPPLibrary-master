package app.akexorcist.bluetoothspp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class SelectDevice extends Activity{

    private String jsonResult;
    private String url = "http://168.63.175.28/ListDevice.php";
    private ListView listView;
    ProgressBar p;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    ArrayList device_id=new ArrayList();
    ArrayList name_id=new ArrayList();
    int posi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_device);

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
                    String name = textView.getText().toString();
//                    Toast.makeText(SelectDevice.this, (String) device_id.get(position), Toast.LENGTH_LONG).show();
                    onclickGraph(position);
                }

            });
            for (int i=0;i<name_id.size();i++){
                Log.v("dev",(String) device_id.get(i));
            }
        }
        else {
            Toast.makeText(getApplicationContext(),"No Network", Toast.LENGTH_SHORT).show();
            finish();
        }

    }
    public void onclickGraph(int t1){
        SharedPreferences sp = getSharedPreferences("TimeHistory", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.putString("Page", "History");
        editor.putString("device", (String) device_id.get(t1));
        editor.commit();
//        Toast.makeText(SelectDevice.this, (String) device_id.get(t1), Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this,History.class);
        intent.putExtra("Page","History");
        editor.putString("device", (String) device_id.get(t1));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


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
            Log.v("ListDrwaer","ListDrwaer");
        }
    }// end async task

    public void accessWebService() {
        JsonReadTask task = new JsonReadTask();
        // passes values for the urls string array
        task.execute(new String[] { url });
        p=(ProgressBar) findViewById(R.id.progressBar);
        p.setVisibility(View.INVISIBLE);
    }

    public void ListDrwaer() {
        try {
            JSONObject jsonResponse = new JSONObject(jsonResult);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("detail_history");

            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                String device = jsonChildNode.getString("device_id");
                String name = jsonChildNode.getString("device_des");
                String outPut = device+" - "+name;

                device_id.add(device);
                name_id.add(name);
            }
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Error" + e.toString(),
                    Toast.LENGTH_SHORT).show();
        }

        String[] myItems = new String[name_id.size()];
        for (int i = 0; i < name_id.size(); i++) {
            myItems[i] = new String((String) name_id.get(i));
//            Log.v("myItems", (String) device_id.get(i));
        }

        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(
                SelectDevice.this,
                R.layout.da_item,
                myItems);

        listView.setAdapter(adapter);
    }
}
