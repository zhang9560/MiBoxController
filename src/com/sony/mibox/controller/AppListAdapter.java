package com.sony.mibox.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class AppListAdapter extends BaseAdapter {
    public AppListAdapter(Context context, List<AppInfo> applist) {
        mContext = context;
        mAppList = applist;
    }

    @Override
    public int getCount() {
        return mAppList.size();
    }

    @Override
    public Object getItem(int position) {
        return mAppList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.appitem, null);
            holder = new ViewHolder();
            holder.icon = (ImageView)convertView.findViewById(R.id.icon);
            holder.label = (TextView)convertView.findViewById(R.id.label);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        AppInfo info = mAppList.get(position);
        holder.icon.setImageBitmap(info.icon);
        holder.label.setText(info.name);

        return convertView;
    }

    private class ViewHolder {
        public ImageView icon;
        public TextView label;
    }

    private Context mContext;
    private List<AppInfo> mAppList;
}
