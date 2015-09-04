package app.akexorcist.bluetoothspp;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class OneFragment extends Fragment{

    Menu menu;
    String timeNow;
    Double timeNows;
    private Thread thread;
    private final Handler mHandler = new Handler();
    private Runnable mTimer1;
    private Runnable mTimer2;
    private LineGraphSeries<DataPoint> mSeries1;
    private LineGraphSeries<DataPoint> mSeries2;
    private double graph2LastXValue = 5d;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_one, container, false);

        WebView webview;

        String html="<iframe width=\"430\" height=\"280\" frameborder=\"0\" src=\"http://app.ubidots.com/ubi/getchart/BGaMJQMCWgALfdXqK1Doldv-0BQ\"></iframe>";
        webview = (WebView) rootView.findViewById(R.id.webView);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadData(html, "text/html", null);

        return rootView;
    }

}