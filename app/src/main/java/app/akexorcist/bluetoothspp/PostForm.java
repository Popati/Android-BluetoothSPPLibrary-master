package app.akexorcist.bluetoothspp;


import android.os.Handler;
import android.util.Log;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;

import java.io.IOException;

/**
 * Created by Popati on 21/12/58.
 */
public class PostForm {
    private Handler handler;
    private final OkHttpClient client = new OkHttpClient();

    public PostForm(RequestBody formBody,String SITE_URL) throws Exception {
        try {
            Log.d("okhttp", SITE_URL);
            handler = new Handler();

            com.squareup.okhttp.Request.Builder builder = new com.squareup.okhttp.Request.Builder();
            builder.post(formBody)
                    .url(SITE_URL);

            builder.build();

            com.squareup.okhttp.Request request = builder.build();

            OkHttpClient client = new OkHttpClient();
            Call call = client.newCall(request);

            call.enqueue(new Callback() {
                @Override
                public void onFailure(com.squareup.okhttp.Request request, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(com.squareup.okhttp.Response response) throws IOException {
                    final String json = response.body().string();

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.v("checkrun", json);
                        }
                    });

                }
            });
        }
        catch (Exception ex){
            Log.v("Exception",ex.toString());
        }
    }

    public PostForm(String SITE_URL) {
        try {
            Log.d("okhttp", SITE_URL);
            handler = new Handler();

            com.squareup.okhttp.Request.Builder builder = new com.squareup.okhttp.Request.Builder();
            builder.url(SITE_URL);

            builder.build();

            com.squareup.okhttp.Request request = builder.build();

            OkHttpClient client = new OkHttpClient();
            Call call = client.newCall(request);

            call.enqueue(new Callback() {
                @Override
                public void onFailure(com.squareup.okhttp.Request request, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(com.squareup.okhttp.Response response) throws IOException {
                    final String json = response.body().string();

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.v("checkrun", json);
                        }
                    });

                }
            });
        }
        catch (Exception ex){
            Log.v("Exception",ex.toString());
        }
    }
}
