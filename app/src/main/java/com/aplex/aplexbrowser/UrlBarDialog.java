package com.aplex.aplexbrowser;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by chengmz on 2016/11/8.
 */
public class UrlBarDialog extends Dialog {
    @InjectView(R.id.turn_btn)
    public ImageView mTurnBtn;
    @InjectView(R.id.addr_bar)
    public EditText mUrlEdt;
    @InjectView(R.id.clock)
    public TextView mClock;
    @InjectView(R.id.wifi_icon)
    public ImageView mWifi;
    @InjectView(R.id.battery_icon)
    public ImageView mBattery;

    private BroadcastReceiver batteryLevelRcvr;
    private IntentFilter batteryLevelFilter;

    private boolean isWifiShow = true;
    private WifiInfo wifiInfo = null;       //获得的Wifi信息
    private WifiManager wifiManager = null; //Wifi管理器
    private int level;                      //信号强度值

    private Handler mHanlder =  new Handler();

    SimpleDateFormat simpleDateFormat;
    String time;

    public UrlBarDialog(Context context) {
        super(context, R.style.UrlDialogTheme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //给dialog设置布局
        setContentView(R.layout.ui_urlbar);
        ButterKnife.inject(this);
        //通过window设置获取dialog参数
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();

        //获取屏幕的宽高
        WindowManager manager = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;
        //设置dialog的宽
        params.width = width;
        //设置dialog在屏幕中的位置
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        //设置dialog属性
        window.setAttributes(params);


        initStatebar();
    }

    @Override
    public void dismiss() {
        getContext().unregisterReceiver(batteryLevelRcvr);
        isWifiShow = false;
        super.dismiss();
    }

    private void initStatebar() {
        initBattery();
        initWifiAndClock();
    }

    private void initWifiAndClock() {
        wifiManager = (WifiManager)getContext().getSystemService(getContext().WIFI_SERVICE);
        simpleDateFormat = new SimpleDateFormat("HH:mm");
        new Thread(){
            @Override
            public void run() {
                while (isWifiShow){
                    //clock
                    time = simpleDateFormat.format(new Date());
                    //wifi
                    wifiInfo = wifiManager.getConnectionInfo();
                    //获得信号强度值
                    level = wifiInfo.getRssi();
                    mHanlder.post(new Runnable() {
                        @Override
                        public void run() {
                            //设置事件
                            mClock.setText(time);
                            //设置wifi
                            if (level <= 0 && level >= -50) {
                               mWifi.setImageResource(R.drawable.wifi_4);
                            } else if (level < -50 && level >= -70) {
                                mWifi.setImageResource(R.drawable.wifi_3);
                            } else if (level < -70 && level >= -80) {
                                mWifi.setImageResource(R.drawable.wifi_2);
                            } else if (level < -80 && level >= -100){
                                mWifi.setImageResource(R.drawable.wifi_1);
                            }else {
                                mWifi.setImageResource(R.drawable.wifi_0);
                            }
                        }
                    });
                    SystemClock.sleep(5000);
                }
            }
        }.start();
    }

    private void initBattery() {
        batteryLevelRcvr = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                //判断它是否是为电量变化的Broadcast Action
                if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                    //获取当前电量
                    int level = intent.getIntExtra("level", 0);
                    //电量的总刻度
                    int scale = intent.getIntExtra("scale", 100);
                    //把它转成百分比
                    int percent = (level * 100) / scale;
                    if (percent < 25) {
                        mBattery.setImageResource(R.drawable.battery_1);
                    } else if (percent < 50) {
                        mBattery.setImageResource(R.drawable.battery_2);
                    } else if (percent < 75) {
                        mBattery.setImageResource(R.drawable.battery_3);
                    } else {
                        mBattery.setImageResource(R.drawable.battery_4);
                    }
                }
            }
        };
        batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        getContext().registerReceiver(batteryLevelRcvr, batteryLevelFilter);
    }


    public UrlBarRequetListener mListener;

    interface UrlBarRequetListener {
        void request(String url);
    }

    public void setOnUrlBarRequestListener(UrlBarRequetListener listener) {
        mListener = listener;
    }


    @OnClick(R.id.turn_btn)
    public void ClickTurnBtn(View view) {
        if (mListener != null) {
            String url = mUrlEdt.getText().toString();
            if (url.indexOf("http://") != 0 && url.indexOf("https://") != 0) {
                url = "http://" + url;
            }
            mListener.request(url);
            dismiss();
        }
    }

    private String getBattery() {

        return null;
    }
}
