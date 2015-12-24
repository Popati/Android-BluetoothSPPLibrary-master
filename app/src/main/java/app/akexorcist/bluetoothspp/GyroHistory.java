package app.akexorcist.bluetoothspp;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * Created by Popati on 23/9/58.
 */
public class GyroHistory extends Fragment {

    String timeStart2,timeStop2,page,tStart,tStop,imei;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_google_graph, container, false);

        SharedPreferences sp = this.getActivity().getSharedPreferences("TimeHistory", Context.MODE_PRIVATE);
        timeStart2=sp.getString("TimeStart2", null);
        timeStart2 = sp.getString("TimeStart2", null);
        timeStop2 = sp.getString("TimeStop2", null);
        tStart = sp.getString("tStart", null);
        tStop = sp.getString("tStop", null);
        page = sp.getString("Page", null);
        imei = sp.getString("imei",null);

        WebView myWebView = (WebView) rootView.findViewById(R.id.webview1);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        String url="http://168.63.175.28/AppSelectGyro.php?fi=" + timeStart2 + "%20"
                + tStart + ":00&ff=" + timeStop2.trim() + "%20" + tStop.trim() + ":00&imei="+imei.trim();
        Log.v("url", url);

        myWebView.loadUrl(url);
        //myWebView.loadUrl("http://www.tqfsmart.info/AppSelect.php?fi=2015-08-30%2011:53:00&ff=2015-08-30%2012:02:00");

        final ProgressBar mProgress = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        myWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {

                mProgress.setVisibility(View.INVISIBLE);
            }
        });
        return rootView;
    }

}
