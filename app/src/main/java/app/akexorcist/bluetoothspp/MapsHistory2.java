package app.akexorcist.bluetoothspp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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

public class MapsHistory2 extends Fragment implements OnMapReadyCallback {

    String timeStart2,timeStop2,page,tStart,tStop,imei;
    private String jsonResult;
    private String url = "";
    private GoogleMap mMap;
    private Marker marker;
    private Button button;

    SupportMapFragment fragment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_maps, container, false);
        Log.v("MapsHistory2","1");
        FragmentManager fm = getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);

        SharedPreferences sp = this.getActivity().getSharedPreferences("TimeHistory", Context.MODE_PRIVATE);
        timeStart2 = sp.getString("TimeStart2", null);
        timeStop2 = sp.getString("TimeStop2", null);
        tStart = sp.getString("tStart", null);
        tStop = sp.getString("tStop", null);
        page = sp.getString("Page", null);
        imei = sp.getString("imei",null);
        Log.v("shared maps",tStart+" "+timeStart2+"  "+tStop+" "+timeStop2+" "+page);

        if(page.equals("Terminal")) {
            url = "http://www.tqfsmart.info/ListLocationHistory.php?fi=" + timeStart2.trim() + "%20"
                    + tStart.trim() + ":00&ff=" + timeStop2.trim() + "%20" + tStop.trim()
                    + ":00&imei="+imei;

            Log.v("url",url);
            accessWebService();
            Log.v("MapsHistory2", "2");
        }
//        fragment.getMapAsync(this);

//        WebView myWebView = (WebView) rootView.findViewById(R.id.webview1);
//        WebSettings webSettings = myWebView.getSettings();
//        webSettings.setJavaScriptEnabled(true);
        //myWebView.loadUrl("http://www.tqfsmart.info/AppSelectLocation.php?fi=" + timeStart2 + "%20" + tStart + ":00&ff=" + timeStop2 + "%20" + tStop + ":00");
        //myWebView.loadUrl("http://www.tqfsmart.info/AppSelect.php?fi=2015-08-30%2011:53:00&ff=2015-08-30%2012:02:00");
//
//        final ProgressBar mProgress = (ProgressBar) rootView.findViewById(R.id.progress_bar);
//        myWebView.setWebViewClient(new WebViewClient() {
//            public void onPageFinished(WebView view, String url) {
//                mProgress.setVisibility(View.INVISIBLE);
//            }
//        });
        return rootView;
    }

    public void accessWebService() {
        JsonReadTask task = new JsonReadTask();
        // passes values for the urls string array
        task.execute(new String[]{url});
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
                Log.d("Error...", e.toString());
            }
            return answer;
        }

        @Override
        protected void onPostExecute(String result) {
            ListDrwaer();
        }
    }// end async task

    // build hash set for list view
    ArrayList la=new ArrayList();
    ArrayList ln=new ArrayList();
    public void ListDrwaer() {

        try {
            JSONObject jsonResponse = new JSONObject(jsonResult);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("detail_history");

            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                la.add(jsonChildNode.getString("lati"));
                ln.add(jsonChildNode.getString("longti"));
                String outPut = la + " - " + ln;
                Log.v("output",outPut);
//                Log.v("MapsHistory2", outPut);
                try {
                    LatLng pointStart = new LatLng(Double.parseDouble(String.valueOf(la.get(0))),
                            Double.parseDouble(String.valueOf(ln.get(0))));
                    mMap.addMarker(new MarkerOptions()
                            .position(pointStart).title("Start")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.circle)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pointStart, 16));
                }
                catch (Exception ex){
                    Log.d("Exception",ex.toString());
                }

            }

            //remove previously placed Marker
            if (marker != null) {
                marker.remove();
            }

            for (int i = 0; i < ln.size(); i++) {
                Log.v("msg",la.get(i)+" - "+ln.get(i));
                try {
                    marker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(String.valueOf(la.get(i))), Double.parseDouble(String.valueOf(ln.get(i)))))
                            .title(String.valueOf(i))
                            .alpha(0.7f)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.circle)));
                }
                catch (Exception ex){
                    Log.d("Exception",ex.toString());
                }
            }

        } catch (JSONException e) {
            Log.d("Error...", e.toString());
        }

        Log.v("ln", String.valueOf(ln.size()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            Log.d("MyMap", "onActivityResult " + data.getStringExtra("result"));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("MyMap", "onResume");
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            Log.d("MyMap", "setUpMapIfNeeded");
            FragmentManager fm = getChildFragmentManager();
            ((SupportMapFragment) fm.findFragmentById(R.id.map))
                    .getMapAsync(this);
        }
    }
    @Override
    public void onMapReady(GoogleMap map) {
        Log.d("MyMap", "onMapReady");
        mMap = map;
        Log.v("MyMap", String.valueOf(ln.size()));

        setUpMap();
//        accessWebService();
//        Log.v("ln", String.valueOf(ln.size()));
//        for (int i = 0; i < ln.size(); i++) {
//            map.addMarker(new MarkerOptions()
//                    .position(new LatLng(Double.parseDouble(String.valueOf(la.get(i))), Double.parseDouble(String.valueOf(ln.get(i)))))
//                    .title(String.valueOf(i)));
////            LatLng sydney = new LatLng(Double.parseDouble(String.valueOf(la.get(i))), Double.parseDouble(String.valueOf(ln.get(i))));
////            map.addMarker(new MarkerOptions().position(sydney).title(String.valueOf(i)));
////            map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//            Log.v("myItems", String.valueOf(la.get(i)));
//        }
    }
    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
//                Log.d("MyMap", "MapClick");
//
//                //remove previously placed Marker
//                if (marker != null) {
//                    marker.remove();
//                }
//
//                for (int i = 0; i < ln.size(); i++) {
//                    marker = mMap.addMarker(new MarkerOptions()
//                            .position(new LatLng(Double.parseDouble(String.valueOf(la.get(i))), Double.parseDouble(String.valueOf(ln.get(i)))))
//                            .title(String.valueOf(i))
//                            .alpha(0.7f));
//                }
//                Log.d("MyMap", "MapClick After Add Marker");
            }
        });

    }
}