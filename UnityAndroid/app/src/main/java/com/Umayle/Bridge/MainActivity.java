package com.Umayle.Bridge;

//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.unity3d.player.UnityPlayerActivity;
import android.app.AlertDialog;
import  android.widget.Toast;
import  android.os.Vibrator;
import com.unity3d.player.UnityPlayer;
public class MainActivity extends UnityPlayerActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
    }
    //调用弹出Android一个窗口
    public String ShowDialog(final String _title,final String _content)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(_title).setMessage(_content).setPositiveButton("Down",null);
                builder.show();
            }
        });
        return "Java return";
    }
    //定义一个显示Toast的方法，在unity中调用
    public void ShowToast(final String mStr2Show)
    {
        //同样需要在UI线程下执行
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),mStr2Show,Toast.LENGTH_LONG).show();
            }
        });
    }
    //定义一个手机震动的方法，在unity中调用
    public void SetVibrator()
    {
        Vibrator mVibrator=(Vibrator)getSystemService(VIBRATOR_SERVICE);
        mVibrator.vibrate(new long[]{200, 200, 200, 200, 2000, 2000}, -1);
    }
    // arg1：unity中的对象名字，记住是对象名字，不是脚本类名
    // arg2: 函数名
    // arg3: 传给函数的参数，目前只看到一个参   数，并且是string的，自己传进去转吧
    public void callUnityFunc(String _objName,String _funcStr,String _content)
    {
        UnityPlayer.UnitySendMessage(_objName, _funcStr, _content);
    }
}
