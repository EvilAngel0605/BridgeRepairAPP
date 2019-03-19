using System.Collections;
using System.Collections.Generic;
using UnityEngine;

/// <summary>
/// 控制第一个场景,单击桥梁组件部分展示界面后,UI控制界面                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              
/// </summary>
public class CanvasBridgeCom : MonoBehaviour
{
    public Camera mainCamera;
    public Camera comCamera;
    void Start()
    {
        
    }

    // Update is called once per frame
    void Update()
    {
        
    }
    /// <summary>
    /// 切换相机
    /// </summary>
    public void ChangeCamera()
    {
        mainCamera.gameObject.SetActive(true);
        comCamera.gameObject.SetActive(false);
        gameObject.SetActive(false);

        HighlighterInteractive[] highlighterInteractives = GameObject.FindObjectsOfType<HighlighterInteractive>();
        foreach (var item in highlighterInteractives)
        {
            item.h.ConstantOffImmediate();
        }
    }
}
