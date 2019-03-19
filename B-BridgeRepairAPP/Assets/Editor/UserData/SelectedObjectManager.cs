using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEditor;
public class SelectedObjectManager : Editor
{
    private static List<Transform> objList = new List<Transform>();
    [MenuItem("Custom/AddHighLightAndProperty")]
   public static void AddHighLightAndProperty()
    {
        GameObject obj = Selection.activeGameObject;
        if(obj)
        {
            GetChild(obj.transform);
        }
        foreach (var item in objList)
        {
            MeshRenderer meshRenderer = item.GetComponent<MeshRenderer>();
            if(meshRenderer)
            {
                ComponentAttributes componentAttributes = item.GetComponent<ComponentAttributes>();
                if (!componentAttributes) componentAttributes = item.gameObject.AddComponent<ComponentAttributes>();
                HighlighterInteractive highlighterInteractive = item.GetComponent<HighlighterInteractive>();
                if (!highlighterInteractive) highlighterInteractive = item.gameObject.AddComponent<HighlighterInteractive>();
                Collider collidr = item.GetComponent<BoxCollider>();
                if (!collidr)
                {
                    item.gameObject.AddComponent<BoxCollider>();
                }
            }
        }
    }
    [MenuItem("Custom/DeleteHighLightAndProperty")]
    public static void DeleteHighLightAndProperty()
    {
        GameObject obj = Selection.activeGameObject;
        if (obj)
        {
            GetChild(obj.transform);
        }
        foreach (var item in objList)
        {
            ComponentAttributes componentAttributes = item.GetComponent<ComponentAttributes>();
            if (componentAttributes) DestroyImmediate(componentAttributes);
            HighlighterInteractive highlighterInteractive = item.GetComponent<HighlighterInteractive>();
            if (highlighterInteractive) DestroyImmediate(highlighterInteractive);
            HighlighterSpectrum highlighterSpectrum = item.GetComponent<HighlighterSpectrum>();
            if (highlighterSpectrum) DestroyImmediate(highlighterSpectrum);
            Collider collidr = item.GetComponent<BoxCollider>();
            if (collidr)
            {
                DestroyImmediate(collidr);
            }

        }
    }

	[MenuItem("MyMenu/Test")]
    static void Test()
    {
        Transform parent = Selection.activeGameObject.transform;
        Vector3 postion = parent.position;
        Quaternion rotation = parent.rotation;
        Vector3 scale = parent.localScale;
        parent.position = Vector3.zero;
        parent.rotation = Quaternion.Euler(Vector3.zero);
        parent.localScale = Vector3.one;


        Vector3 center = Vector3.zero;
        Renderer[] renders = parent.GetComponentsInChildren<Renderer>();
        foreach (Renderer child in renders)
        {
            center += child.bounds.center;
        }
        center /= parent.GetComponentsInChildren<Transform>().Length;
        Bounds bounds = new Bounds(center, Vector3.zero);
        foreach (Renderer child in renders)
        {
            bounds.Encapsulate(child.bounds);
        }

        parent.position = postion;
        parent.rotation = rotation;
        parent.localScale = scale;

        foreach (Transform t in parent)
        {
            t.position = t.position - bounds.center;
        }
        parent.transform.position = bounds.center + parent.position;
    }

    private static void GetChild(Transform tran)
    {
        int num = tran.childCount;
        if(num>0)
        {
            foreach (Transform item in tran)
            {
                GetChild(item);
            }
        }
        else
        {
            if (!objList.Contains(tran)) objList.Add(tran);
        }
    }
    [MenuItem("MyMenu/Test1")]
    public static void CloneGameObj() {
        GameObject parentTrans = new GameObject();
        Transform parent = Selection.activeGameObject.transform;
        for (int i = 0; i < parent.childCount; i++)
        {
            if (parent.GetChild(i).GetComponent<MeshRenderer>())
            {
                GameObject CLOEN = Instantiate(parent.GetChild(i).gameObject, parentTrans.transform);
                CLOEN.transform.localPosition = Vector3.zero;
                CLOEN.transform.eulerAngles = Vector3.zero;
                Debug.Log("执行了");
            }
        }
    }
}
