package com.Umayle.Bridge;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.unity3d.player.UnityPlayer;

public class ScanActivity extends UnityPlayerActivity {
    public static ScanActivity mActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mActivity=this;
    }
    public  void   SendMessageToUnity(){
        UnityPlayer.UnitySendMessage("AndroidJavaCall","","");
    }
    //暴漏给unity的方法
    public void UnityCallAndroidMehhod()
    {
        Intent intent=new Intent(ScanActivity.this,MainActivity.class);
        startActivity(intent);

        /*new Handler().post(()->{
            ScanActivity.this.finish();
        });*/
    }

}
