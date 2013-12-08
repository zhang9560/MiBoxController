package com.sony.mibox.controller;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import org.json.JSONException;
import org.json.JSONObject;
import org.pixmob.httpclient.HttpClient;
import org.pixmob.httpclient.HttpClientException;
import org.pixmob.httpclient.HttpResponse;

import java.io.IOException;
import java.util.ArrayList;

public class MyActivity extends Activity implements AdapterView.OnItemClickListener {
    public static final String TAG = "MyActivity";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mAppListView = (GridView)findViewById(R.id.applist);
        mAppListView.setOnItemClickListener(this);

        mIP = getIntent().getStringExtra("ip");
        if (mIP != null && mIP.length() > 0) {
            new GetAppListTask().execute();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int postion, long id) {
        AppInfo appInfo = (AppInfo)parent.getAdapter().getItem(postion);
        Log.d(TAG,  appInfo.packageName + "/" + appInfo.className);
        new RunApplicationTask().execute(appInfo);
    }

    private class GetAppListTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(MyActivity.this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setMessage("Wait while loading the applications...");
            mProgressDialog.show();

            mAdapter = new AppListAdapter(MyActivity.this, mAppList);
            mAppListView.setAdapter(mAdapter);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpClient hc = new HttpClient(MyActivity.this);
            try {
                HttpResponse response = hc.get(String.format("http://%s:8080/appnumber", mIP)).execute();
                StringBuilder buf = new StringBuilder();
                response.read(buf);
                mAppNumber = Integer.valueOf(buf.toString());

                for (int i = 0; i < mAppNumber; i++) {
                    HttpResponse appInfoResponse = hc.get(String.format("http://%s:8080/appinfo?index=%s", mIP, i)).execute();
                    StringBuilder strAppInfo = new StringBuilder();
                    appInfoResponse.read(strAppInfo);
                    JSONObject jsonAppInfo = new JSONObject(strAppInfo.toString());

                    AppInfo appInfo = new AppInfo();
                    appInfo.name = jsonAppInfo.getString("name");
                    appInfo.packageName = jsonAppInfo.getString("package");
                    appInfo.className = jsonAppInfo.getString("class");

                    JSONObject icon = jsonAppInfo.getJSONObject("icon");
                    appInfo.icon = Utils.string2Image(icon.getString("content"), icon.getInt("width"), icon.getInt("height"));

                    mAppList.add(appInfo);
                    publishProgress(i);
                }
            } catch (HttpClientException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            mAdapter.notifyDataSetChanged();
            mProgressDialog.setMessage(String.format("Loading applications %d / %d", values[0] + 1, mAppNumber));
        }

        @Override
        protected void onPostExecute(Void result) {
            mProgressDialog.dismiss();
        }

        private int mAppNumber = 0;
        private AppListAdapter mAdapter;
        private ArrayList<AppInfo> mAppList = new ArrayList<AppInfo>();
        private ProgressDialog mProgressDialog;
    }

    private class RunApplicationTask extends AsyncTask<AppInfo, Void, String> {

        @Override
        protected String doInBackground(AppInfo... appInfos) {
            HttpClient hc = new HttpClient(MyActivity.this);
            try {
                final HttpResponse response = hc.get(String.format("http://%s:8080/run?package=%s&class=%s", mIP, appInfos[0].packageName, appInfos[0].className)).execute();
            } catch (HttpClientException e) {
                e.printStackTrace();
            }
            return appInfos[0].packageName;
        }

        @Override
        protected void onPostExecute(String packageName) {
            Intent intent = new Intent(MyActivity.this, ControlActivity.class);
            intent.putExtra("ip", mIP);
            intent.putExtra("package", packageName);
            startActivity(intent);
        }
    }

    private String mIP;
    private GridView mAppListView;
}
