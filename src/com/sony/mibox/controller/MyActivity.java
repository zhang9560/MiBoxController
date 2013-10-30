package com.sony.mibox.controller;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    }

    private class GetAppListTask extends AsyncTask<Void, Void, List<AppInfo>> {

        @Override
        protected List<AppInfo> doInBackground(Void... voids) {
            ArrayList<AppInfo> appList = new ArrayList<AppInfo>();

            try {
                String response = RESTRequest.request(String.format("http://%s:8080/applist", mIP));
                JSONObject allApps = new JSONObject(response);
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
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return appList;
        }

        @Override
        protected void onPostExecute(List<AppInfo> appList) {
            mAppList.setAdapter(new AppListAdapter(MyActivity.this, appList));
        }
    }

    private class RunApplicationTask extends AsyncTask<AppInfo, Void, Void> {

        @Override
        protected Void doInBackground(AppInfo... appInfos) {
            RESTRequest.request(String.format("http://%s:8080/run?package=%s&class=%s", mIP, appInfos[0].packageName, appInfos[0].className));
            return null;
        }
    }

    private String mIP;
    private GridView mAppList;
}
