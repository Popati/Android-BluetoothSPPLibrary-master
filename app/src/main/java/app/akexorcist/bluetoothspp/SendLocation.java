package app.akexorcist.bluetoothspp;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Popati on 21/11/58.
 */
public class SendLocation extends IntentService implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleApiClient googleApiClient;

    private RequestQueue requestQueue;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public SendLocation(String name) {
        super(name);
    }


    @Override
    public void onCreate() {
        super.onCreate();

        // Create Google API Client instance
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationAvailability locationAvailability = LocationServices.FusedLocationApi.getLocationAvailability(googleApiClient);
        if (locationAvailability.isLocationAvailable()) {
            // Call Location Services
            LocationRequest locationRequest = new LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(5000);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        } else {
            // Do something when Location Provider not available
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    @Override
    public void onLocationChanged(Location location) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        insertTOserverLocation(dateFormat.format(date), location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
    public void insertTOserverLocation(final String dt, final Double lati, final Double longti){
        try {
            TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            final HashMap<String, String> params = new HashMap<>();
            params.put("isAdd", "true");
            params.put("imei",telephonyManager.getDeviceId());
            params.put("time", dt);
            params.put("la", Double.toString(lati));
            params.put("lo", Double.toString(longti));

            String queryString = createQueryString(params);
            final String url = "http://tqfsmart.info/addLocation.php" + queryString;

            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    System.out.println("HTTP POST @ URL : " + url + " Success.");
                    Toast.makeText(getApplicationContext(), "เก็บตำแหน่งปัจจุบันแล้ว", Toast.LENGTH_LONG).show();
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
                    return params;
                }
            };


            requestQueue.add(request);
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
}
