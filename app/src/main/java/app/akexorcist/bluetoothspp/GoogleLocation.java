package app.akexorcist.bluetoothspp;

import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GoogleLocation extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient googleApiClient;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_location);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        textView = (TextView) findViewById(R.id.text_view);

        // Create Google API Client instance
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // Connect to Google API Client
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (googleApiClient != null && googleApiClient.isConnected()) {
            // Disconnect Google API Client if available and connected
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Do something when connected with Google API Client
        /*
        LocationAvailability locationAvailability = LocationServices.FusedLocationApi.getLocationAvailability(googleApiClient);
        if (locationAvailability.isLocationAvailable()) {
            // Call Location Services
            LocationRequest locationRequest = new LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        } else {
            // Do something when Location Provider not available
        }*/
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Do something when Google API Client connection was suspended

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Do something when Google API Client connection failed

    }

    @Override
    public void onLocationChanged(Location location) {
        // Do something when got new current location
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        textView.setText("Latitude : " + location.getLatitude() + "\n" +
                "Longitude : " + location.getLongitude());

        insertTOserver(dateFormat.format(date),location.getLatitude(),location.getLongitude());
    }

    public void Start(View view){
        LocationAvailability locationAvailability = LocationServices.FusedLocationApi.getLocationAvailability(googleApiClient);
        if (locationAvailability.isLocationAvailable()) {
            // Call Location Services
            LocationRequest locationRequest = new LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        } else {
            // Do something when Location Provider not available
        }
    }
    public void Stop(View view){
        LocationAvailability locationAvailability = LocationServices.FusedLocationApi.getLocationAvailability(googleApiClient);
        if (locationAvailability.isLocationAvailable()) {
            // Call Location Services
            LocationRequest locationRequest = new LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        } else {
            // Do something when Location Provider not available
        }
    }
    public void insertTOserver(String dt, Double lati,Double longti){
        try {
            ArrayList<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("isAdd","true"));
            nameValuePairs.add(new BasicNameValuePair("time",dt));
            nameValuePairs.add(new BasicNameValuePair("la",Double.toString(lati)));
            nameValuePairs.add(new BasicNameValuePair("lo",Double.toString(longti)));

            HttpClient httpclient =new DefaultHttpClient();
            HttpPost httppost=new HttpPost("http://tqfsmart.info/addLocation.php");
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            Toast.makeText(getApplicationContext(), "Time:"+dt+"Latitude:" + lati + " Longitude:" +
                    longti, Toast.LENGTH_LONG).show();

        }
        catch (Exception e){
            Log.d("log_err", "Error in http connection" + e.toString());
        }

    }
}
