package app.akexorcist.bluetoothspp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class TwoFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_one, container, false);

        WebView webview;
        String html="<iframe width=\"430\" height=\"280\" frameborder=\"0\" src=\"http://app.ubidots.com/ubi/getchart/KzRaQSxaIgCv3HcfXk9sSOsfpjU\"></iframe>";
        webview = (WebView) rootView.findViewById(R.id.webView);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadData(html, "text/html", null);

        return rootView;
    }
}