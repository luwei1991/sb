package com.product.sampling.agore;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmClientListener;
import io.agora.rtm.RtmMessage;

import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.product.sampling.R;
import com.product.sampling.manager.AccountManager;
import com.product.sampling.ui.BaseActivity;
import com.product.sampling.ui.MainActivity;
import com.product.sampling.utils.VibratorHelper;
import com.product.sampling.utils.VibratorUtil;

public class VideoMainActivity extends BaseActivity {

    private ImageView btn;
    private TextView textView;
    Vibrator vibrator;
    RtmClient mRtmClient;
    private  Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn=findViewById(R.id.btn);
        textView= findViewById(R.id.conname);
        Bundle bundle = this.getIntent().getExtras();
        Bundle bundle1 = new Bundle();
 /*       VibratorUtil vu=new VibratorUtil();
        vibrator=vu.getVibratorUtil();*/
        String type=bundle.getString("type");
        vibrator=VibratorHelper.getInstance(this).getVib();


/*        vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);*/
        if("1".equals(type)){
            textView= findViewById(R.id.conname);
            textView.setText("对方已挂断请退出");
            vibrator.cancel();
  /*          VideoMainActivity.this.finish();*/
        }else{
             String channel=bundle.getString("channel");
            String token=bundle.getString("token");
            String name=bundle.getString("contactname");
            bundle1.putString("channel",channel);
            bundle1.putString("token",token);
            long[] patter = {1000, 1000, 1000, 1000, 1000};
            vibrator.vibrate(patter, 0);
            textView.setText(name+"正在呼叫您");
        }
   /*      init();
        String rtcid=AccountManager.getInstance().getRtcid();
         mRtmClient.login(null, rtcid, new ResultCallback<Void>() {
            Boolean loginStatus=false;
            @Override
            public void onSuccess(Void responseInfo) {
                loginStatus = true;
                Log.d("loginStatus", "login success!");
            }
            @Override
            public void onFailure(ErrorInfo errorInfo) {
                loginStatus = false;
                Log.d("loginStatus", "login failure!");
            }
        });*/

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrator.cancel();

                startActivity(new Intent(VideoMainActivity.this,VideoChatActivity.class).putExtras(bundle1));
            }
        });
    }
    public void onEncCallClicked(View view) {
        vibrator.cancel();
        VideoMainActivity.this.finish();
    }
    public void init() {
        try {
            mRtmClient = RtmClient.createInstance(this, getString(R.string.agora_app_id),
                    new RtmClientListener() {
                        @Override
                        public void onConnectionStateChanged(int state, int reason) {
                            Log.d("videoReason", "Connection state changes to "
                                    + state + " reason: " + reason);
                        }

                        @Override
                        public void onMessageReceived(RtmMessage rtmMessage, String peerId) {
                            String msg = rtmMessage.getText();
                            Log.d("videoReason", "Message received " + " from " + peerId + msg);
                            String[]data=msg.split(",");
                            String type=data[0];
                            if("1".equals(type)){
                                textView= findViewById(R.id.conname);
                                textView.setText("对方已挂断请退出");
                                vibrator.cancel();
                                VideoMainActivity.this.finish();
                              /*  startActivity(new Intent(VideoMainActivity.this,MainActivity.class));*/
                            }
                        }
                        @Override
                        public  void onTokenExpired(){

                        }
                    });
        } catch (Exception e) {
            Log.d("videoReason", "RTM SDK init fatal error!");
            throw new RuntimeException("You need to check the RTM init process.");
        }
    }

    @Override
    public void onBackPressed() {


        vibrator.cancel();
        super.onBackPressed();


            }

}


