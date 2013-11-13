package com.sony.mibox.controller;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ControlActivity extends Activity implements View.OnClickListener {
    public static final int KEY_UP = 103;
    public static final int KEY_DOWN = 108;
    public static final int KEY_LEFT = 105;
    public static final int KEY_RIGHT = 106;
    public static final int KEY_ENTER = 28;
    public static final int KEY_BACK = 158;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.controller);

        mIP = getIntent().getStringExtra("ip");
        mBtnUp = (Button)findViewById(R.id.btn_up);
        mBtnDown = (Button)findViewById(R.id.btn_down);
        mBtnLeft = (Button)findViewById(R.id.btn_left);
        mBtnRight = (Button)findViewById(R.id.btn_right);
        mBtnEnter = (Button)findViewById(R.id.btn_enter);
        mBtnBack = (Button)findViewById(R.id.btn_back);

        mBtnUp.setOnClickListener(this);
        mBtnDown.setOnClickListener(this);
        mBtnLeft.setOnClickListener(this);
        mBtnRight.setOnClickListener(this);
        mBtnEnter.setOnClickListener(this);
        mBtnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == mBtnUp) {
            new InjectKeyCodeTask().execute(KEY_UP);
        } else if (view == mBtnDown) {
            new InjectKeyCodeTask().execute(KEY_DOWN);
        } else if (view == mBtnLeft) {
            new InjectKeyCodeTask().execute(KEY_LEFT);
        } else if (view == mBtnRight) {
            new InjectKeyCodeTask().execute(KEY_RIGHT);
        } else if (view == mBtnEnter) {
            new InjectKeyCodeTask().execute(KEY_ENTER);
        } else if (view == mBtnBack) {
            new InjectKeyCodeTask().execute(KEY_BACK);
        }
    }

    private class InjectKeyCodeTask extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... keycode) {
            RESTRequest.request(String.format("http://%s:8080/injectkey?keycode=%d", mIP, keycode[0]));
            return null;
        }
    }

    private String mIP;
    private Button mBtnUp;
    private Button mBtnDown;
    private Button mBtnLeft;
    private Button mBtnRight;
    private Button mBtnEnter;
    private Button mBtnBack;
}
