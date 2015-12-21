package app.akexorcist.bluetoothspp;

import android.content.Context;
import android.content.Intent;
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

public class GoogleGraph extends Fragment {

    String timeStart2,timeStop2,page,tStart,tStop,imei;

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences sp = this.getActivity().getSharedPreferences("TimeHistory", Context.MODE_PRIVATE);

        page = sp.getString("Page", null);
        Log.d("GoogleGraph Destroy", page);

        if(page.equals("Terminal")) {
            Intent intent = new Intent(getActivity(), Home.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_google_graph, container, false);

        Log.v("GoogleGraph","1");
        SharedPreferences sp = this.getActivity().getSharedPreferences("TimeHistory", Context.MODE_PRIVATE);
        timeStart2=sp.getString("TimeStart2", null);
        timeStart2 = sp.getString("TimeStart2", null);
        timeStop2 = sp.getString("TimeStop2", null);
        tStart = sp.getString("tStart", null);
        tStop = sp.getString("tStop", null);
        page = sp.getString("Page", null);
        imei = sp.getString("imei",null);
        /*
        Bundle bundle = new Bundle();
        timeStart2 = bundle.getString("TimeStart2");
        timeStop2 = bundle.getString("TimeStop2");
        tStart = bundle.getString("tStart");
        tStop = bundle.getString("tStop");

        page = bundle.getString("Page");*/
/*
        Log.v("tStart",tStart);

        if(page.equals("Terminal")){
            final dbTracking myDb = new dbTracking(this);
            myDb.getWritableDatabase(); // First method
            myDb.InsertHistory(timeStart2+" "+tStart, timeStop2+" "+tStop);
            insertTOserver(timeStart2+" "+tStart, timeStop2+" "+tStop);
            //Toast.makeText(Graph.this, page, Toast.LENGTH_LONG).show();
        }*/


        WebView myWebView = (WebView) rootView.findViewById(R.id.webview1);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        String url="http://www.tqfsmart.info/AppSelect.php?fi="+timeStart2+"%20"
                +tStart+":00&ff="+timeStop2.trim()+"%20"+tStop.trim()+":00&imei="+imei;
        Log.v("url",url);

        myWebView.loadUrl(url);
        //myWebView.loadUrl("http://www.tqfsmart.info/AppSelect.php?fi=2015-08-30%2011:53:00&ff=2015-08-30%2012:02:00");
        Log.v("GoogleGraph","2");
        final ProgressBar mProgress = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        myWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {

                mProgress.setVisibility(View.INVISIBLE);
            }
        });
        Log.v("GoogleGraph", "3");
        return rootView;
    }

}
