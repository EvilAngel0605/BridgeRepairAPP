using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
public class AndroidJavaCall : MonoBehaviour
{
    private static AndroidJavaCall _instance;
    public static AndroidJavaCall Instance
    {
        get
        {
            if (!_instance)
            {
                return GameObject.Find("AndroidJavaCall").GetComponent<AndroidJavaCall>();
            }
            else
            {
                return _instance;
            }
        }
    }

    /// <summary>
    /// 启动Unity 时Android 调用文件
    /// Android端传递给unity端的数据文件
    /// </summary>
    public AndroidSendToUnityMessage androidMsgData = null;
    /// <summary>
    /// 获取的Android 信息
    /// </summary>
    public  Text androidMesage;
    /// <summary>
    /// 启动Unity 时调用方法
    /// Android启动时给unity传递消息,判断是否成功
    /// </summary>
    public bool AndroidSendMessageToUnity(string msgJson)
    {
        try
        {
            //androidMsgData = JsonUtility.FromJson<AndroidSendToUnityMessage>(msgJson);

            return true;
        }
        catch 
        {
            Debug.Log("androidMsgData字段" + "解析错误");
            return false;
        }
    }

    /// <summary>
    /// unity 结束调用Android 方法
    /// </summary>
    public void GetAndroidFunc()
    {
        //AndroidJavaClass AndroidJava = new AndroidJavaClass("net.jsrbc.UserData.ScanActivity");
        //AndroidJavaObject androidObj = AndroidJava.GetStatic<AndroidJavaObject>("mActivity");
        //androidObj.Call<string>("UnityCallAndroidMehhod");
        AndroidJavaObject obj = new AndroidJavaClass("net.jsrbc.UserData.ScanActivity").GetStatic<AndroidJavaObject>("mActivity");
        obj.Call("UnityCallAndroidFuc");//method 方法
    }   

    /// <summary>
    /// 调用Android 方法
    /// </summary>
    public void CallAndroidFuc()
    {
        //调用Android 方法完善要求
        AndroidJavaObject obj = new AndroidJavaClass("net.jsrbc.UserData.ScanActivity").GetStatic<AndroidJavaObject>("mActivity");
        obj.Call("");
    }
    
}
public class AndroidSendToUnityMessage
{
    public string bridgeMessageInformation;
    public string bridgeMessageInformation1;
    public AndroidSendToUnityMessage()
    {

    }

}