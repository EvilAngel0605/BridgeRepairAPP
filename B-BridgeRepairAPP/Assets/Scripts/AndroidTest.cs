using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
public class AndroidTest : MonoBehaviour
{
    [SerializeField]
    private Text _Label = null;
    // Start is called before the first frame update
    void Start()
    {
        
    }

    // Update is called once per frame
    void Update()
    {
        
    }

    public void myShowDialog()
    {
        print("myShowDialog_1");
        AndroidJavaClass javaClass = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
        AndroidJavaObject javaObject = javaClass.GetStatic<AndroidJavaObject>("currentActivity");
        string[] mObject = new string[2];
        mObject[0] = "小白菜";
        mObject[1] = "大香菜";
        string ret = javaObject.Call<string>("ShowDialog", mObject);
        setMessage(ref ret);
        print("myShowDialog_2");
    }
    public void myShowToast()
    {
        print("myShowToast_1");
        AndroidJavaClass javaClass = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
        AndroidJavaObject javaObject = javaClass.GetStatic<AndroidJavaObject>("currentActivity");
        javaObject.Call("ShowToast", "你是不是傻");
        print("myShowToast_2");
    }
    /// <summary>
	/// arg0: 调用android中的方法
    /// arg1: 对象名
    /// arg2: 函数名
    /// arg3: 参数1
    /// </summary>
    public void myInteraction()
    {
        print("myInteraction_1");
        AndroidJavaClass javaClass = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
        AndroidJavaObject javaObject = javaClass.GetStatic<AndroidJavaObject>("currentActivity");
        javaObject.Call("callUnityFunc","ReceiveObject","receiveMessage","此系统表示不喜欢你，并表示你长的丑......");
        print("myInteraction_2");
    }
    public void receiveMessage(string message)
    {
        print("receiveMessage:" + message);
        setMessage(ref message);
    }
    public void setMessage(ref string content)
    {
        print("setMessage:" + content);
        _Label.text = content;
    }
}
