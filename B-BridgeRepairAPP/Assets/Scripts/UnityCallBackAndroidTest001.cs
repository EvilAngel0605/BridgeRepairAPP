using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class UnityCallBackAndroidTest001 : MonoBehaviour
{
    /// <summary>
    /// 接收java传来的参数
    /// </summary>
    private int index = 0;
    private string angle;
    public Text text;
    /// <summary>
    /// 旋转方块
    /// </summary>
    public GameObject cube;
    public void OnClick01()
    {
        AndroidJavaClass androidJavaClass = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
        AndroidJavaObject androidJavaObject = androidJavaClass.GetStatic<AndroidJavaObject>("currentActivity");
        /* jo.call | java对象的函数名字
        *   <>    | 函数返回值类型
        *  ("")   | 调用的方法名    */
        index = androidJavaObject.Call<int>("makePauseUnity");
        angle = androidJavaObject.Call<string>("makeRotateUnity");
        text.text = index.ToString();
        RotateCube(angle);
    }
    public void OnClick02()
    {
        print("OnClick01被执行到了");
        /* com.android.unityToandroid : Android工程定义的包名
        * UnityPlayerActivity :java方法所在的类名 */
        AndroidJavaClass androidJavaClass = new AndroidJavaClass("com.Umayle.Bridge.UnityPlayerActivity");
        //m_instance : UnityPlayerActivity类中自己声明 
        AndroidJavaObject androidJavaObject = androidJavaClass.GetStatic<AndroidJavaObject>("m_instance");
        //ShowMessage : java对应的方法 
        //Unity : 向这的代码传一个参数
        androidJavaObject.Call("ShowMessage", "Unity");
    }

    private void RotateCube(string angle)
    {
        cube.transform.Rotate(Vector3.one * float.Parse(angle));
    }
}