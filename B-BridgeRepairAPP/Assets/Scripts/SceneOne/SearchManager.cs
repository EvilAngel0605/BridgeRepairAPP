using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
public class SearchManager : MonoBehaviour
{
    /// <summary>
    /// 部件信息下拉框
    /// </summary>
    [SerializeField]
    private Dropdown partInfoDropDown;
    /// <summary>
    /// 孔号信息下拉框
    /// </summary>
    [SerializeField]
    private Dropdown holeNumDropDown;
    /// <summary>
    ///构件信息下拉框
    /// </summary>
    [SerializeField]
    private Dropdown componentTypeDropDown;
    /// <summary>
    /// 构件编号下拉框 
    /// </summary>
    [SerializeField]
    private Dropdown componentNumDropDown;
    /// <summary>
    /// 点击查找按钮
    /// </summary>
    public void SearchBtnOnclick()
    {
        if(componentTypeDropDown.value>0)
        {
            ClearBtnOnClick();
            ComponentAttributes[] componentAttributes = GameObject.FindObjectsOfType<ComponentAttributes>();
            foreach (var item in componentAttributes)
            {
                if(((int)item.componentType).Equals(componentTypeDropDown.value))
                {
                    HighlighterInteractive highlighterInteractive = item.GetComponent<HighlighterInteractive>();
                    highlighterInteractive.h.ConstantOnImmediate(Color.green);
                }
                else
                {
                    MeshRenderer meshRenderer = item.GetComponent<MeshRenderer>();
                    if (meshRenderer) meshRenderer.enabled = false;
                    Collider collider = item.GetComponent<Collider>();
                    if (collider) collider.enabled = false;
                }
            }
           
        }
    }
    /// <summary>
    /// 点击清空按钮
    /// </summary>
    public void ClearBtnOnClick()
    {
        //partInfoDropDown.value =holeNumDropDown.value=componentTypeDropDown.value=componentNumDropDown.value= 0;
        HighlighterInteractive[] highlighterInteractives = GameObject.FindObjectsOfType<HighlighterInteractive>();
        foreach (var item in highlighterInteractives)
        {
            MeshRenderer meshRenderer = item.GetComponent<MeshRenderer>();
            if (meshRenderer) meshRenderer.enabled = true;
            Collider collider = item.GetComponent<Collider>();
            if (collider) collider.enabled = true;
            item.h.ConstantOffImmediate();
        }

    }
}
