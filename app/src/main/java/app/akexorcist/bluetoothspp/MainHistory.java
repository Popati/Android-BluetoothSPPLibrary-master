package app.akexorcist.bluetoothspp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

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

        stopService(new Intent(MainHistory.this, RSSPullService.class));
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
            final String SITE_URL = "http://168.63.175.28/addHistory.php";

            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            RequestBody formBody = new FormEncodingBuilder()
                    .add("isAdd", "true")
                    .add("imei", telephonyManager.getDeviceId())
                    .add("datestart", dt)
                    .add("timestart", tt)
                    .add("datestop", ds)
                    .add("timestop", ts)
                    .build();

            PostForm p= new PostForm(formBody,SITE_URL);
            final Toast toast = Toast.makeText(getApplicationContext(), "เก็บประวัติแล้ว", Toast.LENGTH_SHORT);
            toast.show();
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
