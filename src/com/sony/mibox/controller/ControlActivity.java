package com.sony.mibox.controller;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import org.pixmob.httpclient.HttpClient;
import org.pixmob.httpclient.HttpClientException;
import org.pixmob.httpclient.HttpResponse;

import java.io.IOException;

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
        mPackageName = getIntent().getStringExtra("package");

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

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpClient hc = new HttpClient(ControlActivity.this);
                try {
                    while(!mStop) {
                        HttpResponse response = hc.get(String.format("http://%s:8080/checkrunningstatus?package=%s", mIP, mPackageName)).execute();
                        StringBuilder buffer = new StringBuilder();
                        response.read(buffer);
                        if (buffer.toString().equals("exited")){
                            mHandler.sendEmptyMessage(0);
                            break;
                        }
                        Thread.sleep(500);
                    }
                } catch (HttpClientException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mStop = true;
    }

    private class InjectKeyCodeTask extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... keycode) {
            HttpClient hc = new HttpClient(ControlActivity.this);
            try {
                final HttpResponse response = hc.get(String.format("http://%s:8080/injectkey?keycode=%d", mIP, keycode[0])).execute();
            } catch (HttpClientException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            finish();
        }
    };

    private boolean mStop = false;
    private String mIP;
    private String mPackageName;
    private Button mBtnUp;
    private Button mBtnDown;
    private Button mBtnLeft;
    private Button mBtnRight;
    private Button mBtnEnter;
    private Button mBtnBack;
}
