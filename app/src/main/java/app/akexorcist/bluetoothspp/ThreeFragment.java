package app.akexorcist.bluetoothspp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class ThreeFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_one, container, false);

        WebView webview;
        String html="<iframe width=\"400\" height=\"260\" frameborder=\"0\" src=\"http://app.ubidots.com/ubi/getchart/m5JdLOcl7EBayMj1U8nK4J-Nwkw\"></iframe>";
        webview = (WebView) rootView.findViewById(R.id.webView);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadData(html, "text/html", null);

//        webview.setWebViewClient(new WebViewClient() {
//
//            public void onPageFinished(WebView view, String url) {
//                ProgressBar p = (ProgressBar) getView().findViewById(R.id.progressBar);
//                p.setVisibility(View.INVISIBLE);
//            }
//        });
        return rootView;
    }
}
