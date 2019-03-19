using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class BridgeComCtrl : MonoBehaviour
{
    //用于绑定参照物对象
    [SerializeField]
    Transform target;
    //缩放系数
    float distance = 10.0f;
    //左右滑动移动速度
    float xSpeed = 250.0f;
    float ySpeed = 120.0f;
    //缩放限制系数
    float yMinLimit = -20;
    float yMaxLimit = 80;
    //摄像头的位置
    float x = 0.0f;
    float y = 0.0f;
    //记录上一次手机触摸位置判断用户是在左放大还是缩小手势
    private Vector2 oldPosition1;
    private Vector2 oldPosition2;
    public GameObject thisCanvas;
    private void OnEnable()
    {
        if (!thisCanvas.activeSelf)
        {
            thisCanvas.SetActive(true);
        }
    }
    //初始化游戏信息设置
    void Start()
    {
        Input.multiTouchEnabled = true;
        if (target!=null)
        {

        Vector3 angles = target.eulerAngles;
        x = angles.y;
        y = angles.x;
        }

        // Make the rigid body not change rotation
        Rigidbody rigidbody = this.GetComponent<Rigidbody>();
        if (rigidbody)
            rigidbody.freezeRotation = true;
    }

    void Update()
    {
        target = transform.GetChild(0);
        if (!target)
        {
            return;
        }
        //判断触摸数量为单点触摸
        if (Input.touchCount == 1)
        {
            //触摸类型为移动触摸
            if (Input.GetTouch(0).phase == TouchPhase.Moved)
            {
                
                Vector2 touchDeltaPosition = Input.GetTouch(0).deltaPosition;
                x += touchDeltaPosition.x * xSpeed * 0.0002f;
                y -= touchDeltaPosition.y * ySpeed * 0.0002f;                
                target.eulerAngles = new Vector3(y, x, transform.eulerAngles.z);
                Debug.Log("当前正在执行" + x + "_________" + y);
                //this.transform.Rotate(touchDeltaPosition.y * 0.02f, touchDeltaPosition.x*0.02f,0,Space.Self);                
            }
            else if (Input.GetTouch(0).phase ==TouchPhase.Stationary)
            {
                RaycastHit hit;
                if (Physics.Raycast(GetComponent<Camera>().ScreenPointToRay(Input.mousePosition),out hit,1000f))
                {
                    if (hit.transform.gameObject.layer == 9 )
                    {
                        AndroidJavaCall.Instance.GetAndroidFunc();
                    }
                }
            }
        }
        //判断触摸数量为多点触摸
        //if (Input.touchCount > 1)
        //{

        //    //前两只手指触摸类型都为移动触摸
        //    if (Input.GetTouch(0).phase == TouchPhase.Moved || Input.GetTouch(1).phase == TouchPhase.Moved)
        //    {
        //        //计算出当前两点触摸点的位置
        //        Vector3 tempPosition1 = Input.GetTouch(0).position;
        //        Vector3 tempPosition2 = Input.GetTouch(1).position;
        //        //函数返回真为放大，返回假为缩小
        //        if (isEnlarge(oldPosition1, oldPosition2, tempPosition1, tempPosition2))
        //        {
        //            //放大系数超过3以后不允许继续放大
        //            //这里的数据是根据我项目中的模型而调节的，大家可以自己任意修改
        //            if (distance > 0)
        //            {
        //                distance -= 3f;
        //                Debug.Log("当前正在执行放大");
        //            }
        //        }
        //        else
        //        {
        //            //缩小洗漱返回18.5f后不允许继续缩小
        //            //这里的数据是根据我项目中的模型而调节的，大家可以自己任意修改
        //            if (distance < 40)
        //            {
        //                distance += 3f;
        //                Debug.Log("当前正在执行缩小");
        //            }
        //        }
        //        //备份上一次触摸点的位置，用于对比
        //        oldPosition1 = tempPosition1;
        //        oldPosition2 = tempPosition2;
        //    }
        //}
        //RaycastHit hit;
        //if (Physics.Raycast(Camera.main.ScreenPointToRay(Input.mousePosition), out hit, 1000f))
        //{            
        //    if (hit.transform.GetComponent<ComponentAttributes>())
        //    {
        //        //TODO 进入到对应的Android 界面                

        //    }
        //}
    }

    //函数返回真为放大，返回假为缩小
    bool isEnlarge(Vector2 oP1, Vector2 oP2, Vector2 nP1, Vector2 nP2)
    {
        //函数传入上一次触摸两点的位置与本次触摸两点的位置计算出用户的手势
        float leng1 = Mathf.Sqrt((oP1.x - oP2.x) * (oP1.x - oP2.x) + (oP1.y - oP2.y) * (oP1.y - oP2.y));
        float leng2 = Mathf.Sqrt((nP1.x - nP2.x) * (nP1.x - nP2.x) + (nP1.y - nP2.y) * (nP1.y - nP2.y));
        if (leng1 < leng2)
        {
            //放大手势
            return true;
        }
        else
        {
            //缩小手势
            return false;
        }
    }

    //Update方法一旦调用结束以后进入这里算出重置摄像机的位置
    void LateUpdate()
    {
        if (Input.touchCount > 1)
        {

            //前两只手指触摸类型都为移动触摸
            if (Input.GetTouch(0).phase == TouchPhase.Moved || Input.GetTouch(1).phase == TouchPhase.Moved)
            {
                //计算出当前两点触摸点的位置
                Vector3 tempPosition1 = Input.GetTouch(0).position;
                Vector3 tempPosition2 = Input.GetTouch(1).position;
                //函数返回真为放大，返回假为缩小
                Camera comCamera =  transform.GetComponent<Camera>();
                if (isEnlarge(oldPosition1, oldPosition2, tempPosition1, tempPosition2))
                {
                    comCamera.fieldOfView = Mathf.Clamp(comCamera.fieldOfView - 5,20f,100f);
                }
                else
                {
                    comCamera.fieldOfView = Mathf.Clamp(comCamera.fieldOfView + 5, 20f, 100f);                    
                }
                //备份上一次触摸点的位置，用于对比
                oldPosition1 = tempPosition1;
                oldPosition2 = tempPosition2;
            }
        }
        //target为我们绑定的箱子变量，缩放旋转的参照物
        //if (target)
        //{

        //    //重置摄像机的位置
        //    y = ClampAngle(y, yMinLimit, yMaxLimit);
        //    Quaternion rotation = Quaternion.Euler(y, x, 0);
        //    Vector3 position = rotation * new Vector3(0.0f, 0.0f, -distance) + target.position;

        //    transform.rotation = rotation;
        //    transform.position = position;
        //    //   transform.eulerAngles = new Vector3(ClampAngle(transform.eulerAngles.x, -10f, 10f), ClampAngle(transform.eulerAngles.y, -30f, 30f), transform.rotation.z);
        //}
        //if (Input.GetKeyDown(KeyCode.Space))
        //{
            
        //}
    }

    static float ClampAngle(float angle, float min, float max)
    {
        if (angle < -360)
            angle += 360;
        if (angle > 360)
            angle -= 360;
        return Mathf.Clamp(angle, min, max);
    }

}
