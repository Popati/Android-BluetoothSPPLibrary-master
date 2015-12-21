package app.akexorcist.bluetoothspp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class MainHistory extends AppCompatActivity implements ActionBar.TabListener {
    private ViewPager viewPager;
    private TapsHistory mAdapter;
    private ActionBar actionBar;
    private RequestQueue requestQueue;
    String timeStart2,timeStop2,page,tStart,tStop,imei;
    // Tab titles
    private String[] tabs = { "Temperature", "Location", "Count Bounce" };


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_history);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getSupportActionBar();
        mAdapter = new TapsHistory(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        SharedPreferences sp = getSharedPreferences("TimeHistory", Context.MODE_PRIVATE);
        timeStart2 = sp.getString("TimeStart2", null);
        timeStop2 = sp.getString("TimeStop2", null);
        tStart = sp.getString("tStart", null);
        tStop = sp.getString("tStop", null);
        page = sp.getString("Page", null);
        imei = sp.getString("imei",null);

        Log.v("sharedM",tStart+" "+timeStart2+"  "+tStop+" "+timeStop2);

        Log.d("page",page);
        if(page.equals("Terminal")){
//            final dbTracking myDb = new dbTracking(this);
//            myDb.getWritableDatabase(); // First method
//            myDb.InsertHistory(timeStart2 + " " + tStart, timeStop2 + " " + tStop);
            insertTOserver(timeStart2 ,tStart, timeStop2, tStop);
            Log.v("insertToserver",timeStart2 + " ," + tStart +" ,"+ timeStop2 + " ," + tStop);
            //Toast.makeText(MainHistory.this, timeStart2 + " " + tStart, timeStop2 + " " + tStop, Toast.LENGTH_LONG).show();
        }
    }

    public void insertTOserver(final String dt, final String tt, final String ds, final String ts){
        try {
            final String url = "http://tqfsmart.info/addHistory.php";

            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    Log.v("HTTP POST @ URL : " ,url +response+ " Success.");
                    Toast.makeText(getApplicationContext(), "เก็บประวัติแล้ว", Toast.LENGTH_LONG).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    VolleyLog.e("Error: ", error.getMessage());
                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

                    final HashMap<String, String> params = new HashMap<>();
                    params.put("isAdd", "true");
                    params.put("imei", telephonyManager.getDeviceId());
                    params.put("datestart", dt);
                    params.put("timestart", tt);
                    params.put("datestop", ds);
                    params.put("timestop", ts);
                    return params;
                }
            };
            Log.v("rep", request.toString());
//            requestQueue.add(request);
            Volley.newRequestQueue(this).add(request);
        }
        catch (Exception ex){
            Log.v("ex", ex.toString());
        }
    }
    public static String createQueryString(HashMap<String, String> queryParameters)
    {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (String key : queryParameters.keySet())
        {
            String value = queryParameters.get(key).toString();

            try
            {
                String encodedValue = URLEncoder.encode(value, "UTF-8");
//                System.out.println("Value: "+encodedValue);

                if(first)
                {
                    builder.append("?");
                    first = false;
                }
                else
                {
                    builder.append("&");
                }

                builder.append(key).append("=").append(encodedValue);
            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
        }

        return builder.toString();
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }
}
