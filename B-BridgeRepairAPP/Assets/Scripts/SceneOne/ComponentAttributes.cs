using System.Collections;
using System.Collections.Generic;
using UnityEngine;
public enum ComponentType
{
    桥墩,桥柱,桥梁,桥面
}
public class ComponentAttributes : MonoBehaviour
{
    [Header("部件信息")]
    public string partInfo;
    [Header("孔号信息")]
    public string holeNumInfo;
    [Header("构件类型信息")]
    public ComponentType componentType;
    [Header("构件编号信息")]
    public string componentNum;
    private void Awake()
    {
        ComponentType[] componentTypes = System.Enum.GetValues(new ComponentType().GetType()) as ComponentType[];
        componentType = componentTypes[Random.Range(0, componentTypes.Length)];
    }
}
