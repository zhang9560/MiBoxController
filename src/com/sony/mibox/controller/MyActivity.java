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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pixmob.httpclient.HttpClient;
import org.pixmob.httpclient.HttpClientException;
import org.pixmob.httpclient.HttpResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyActivity extends Activity implements AdapterView.OnItemClickListener {
    public static final String TAG = "MyActivity";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mAppList = (GridView)findViewById(R.id.applist);
        mAppList.setOnItemClickListener(this);

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

        Intent intent = new Intent(this, ControlActivity.class);
        intent.putExtra("ip", mIP);
        startActivity(intent);
    }

    private class GetAppListTask extends AsyncTask<Void, Void, List<AppInfo>> {

        @Override
        protected void onPreExecute() {
            if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialog(MyActivity.this);
            }
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setMessage("Wait while loading the applications...");
            mProgressDialog.show();
        }

        @Override
        protected List<AppInfo> doInBackground(Void... voids) {
            ArrayList<AppInfo> appList = new ArrayList<AppInfo>();

            HttpClient hc = new HttpClient(MyActivity.this);
            try {
                final HttpResponse response = hc.get(String.format("http://%s:8080/applist", mIP)).execute();
                final StringBuilder buf = new StringBuilder();
                response.read(buf);

                JSONObject allApps = new JSONObject(buf.toString());
                JSONArray jsonArray = allApps.getJSONArray("applications");
                for (int i = 0; i < jsonArray.length(); i++) {
                    AppInfo appInfo = new AppInfo();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    appInfo.name = jsonObject.getString("name");
                    appInfo.packageName = jsonObject.getString("package");
                    appInfo.className = jsonObject.getString("class");

                    JSONObject icon = jsonObject.getJSONObject("icon");
                    appInfo.icon = Utils.string2Image(icon.getString("content"), icon.getInt("width"), icon.getInt("height"));

                    appList.add(appInfo);
                }
            } catch (HttpClientException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return appList;
        }

        @Override
        protected void onPostExecute(List<AppInfo> appList) {
            mAppList.setAdapter(new AppListAdapter(MyActivity.this, appList));
            mProgressDialog.dismiss();
        }

        private ProgressDialog mProgressDialog;
    }

    private class RunApplicationTask extends AsyncTask<AppInfo, Void, Void> {

        @Override
        protected Void doInBackground(AppInfo... appInfos) {
            HttpClient hc = new HttpClient(MyActivity.this);
            try {
                final HttpResponse response = hc.get(String.format("http://%s:8080/run?package=%s&class=%s", mIP, appInfos[0].packageName, appInfos[0].className)).execute();
            } catch (HttpClientException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private String mIP;
    private GridView mAppList;
}
