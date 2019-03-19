package net.jsrbc.UserData;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.Toast;

import com.unity3d.player.UnityPlayer;

import net.jsrbc.activity.bridge.BridgeActivity;

public class ScanActivity extends UnityPlayerActivity {
  //  private static final android.widget.Toast Toast = ;
    public static Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        UnityPlayer.UnitySendMessage("AndroidJavaCall","AndroidSendMessageToUnity","jsonToDo");
    }
    //unity 调用Android 的方法
    public void UnityCallAndroidFuc(){
        Intent intent=new Intent(ScanActivity.this,BridgeActivity.class);
        startActivity(intent);
    }
    //AndroidSendMessageToUnity
}
