package app.akexorcist.bluetoothspp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

public class GoogleGraph extends Activity {

    String timeStart2,timeStop2,page,tStart,tStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_graph);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        /*String GraphURL = "http://www.google.com";
        Log.v("GraphURL :", GraphURL);
        WebView webview = (WebView) findViewById(R.id.webView1);
        webview.loadUrl(GraphURL);*/

        Bundle bundle = getIntent().getExtras();

        timeStart2 = bundle.getString("TimeStart2");
        timeStop2 = bundle.getString("TimeStop2");
        tStart = bundle.getString("tStart");
        tStop = bundle.getString("tStop");

        page = bundle.getString("Page");

        Log.v("tStart",tStart);

        if(page.equals("Terminal")){
            final dbTracking myDb = new dbTracking(this);
            myDb.getWritableDatabase(); // First method
            myDb.InsertHistory(timeStart2+" "+tStart, timeStop2+" "+tStop);
            insertTOserver(timeStart2+" "+tStart, timeStop2+" "+tStop);
            //Toast.makeText(Graph.this, page, Toast.LENGTH_LONG).show();
        }

        WebView myWebView = (WebView) findViewById(R.id.webview1);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.loadUrl("http://www.tqfsmart.info/AppSelect.php?fi="+timeStart2+"%20"+tStart+":00&ff="+timeStop2+"%20"+tStop+":00");
        //myWebView.loadUrl("http://www.tqfsmart.info/AppSelect.php?fi=2015-08-30%2011:53:00&ff=2015-08-30%2012:02:00");
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
            Log.d("log_err", "Error in http connection" + e.toString());
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_google_graph, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.gohome) {
            Intent in=new Intent(this,Home.class);
            startActivity(in);
            //return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
