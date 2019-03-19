using UnityEngine;
using System.Collections;

public class TouchControl : MonoBehaviour
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
    /// <summary>
    /// 桥梁单独组件相机
    /// </summary>
    public Camera componentCamera;
    /// <summary>
    /// 初始化方法
    /// </summary>
    private void Awake()
    {
        //访问Android对应的数据
        
    }
    //初始化游戏信息设置
    void Start()
    {
        Input.multiTouchEnabled = true;
        Vector3 angles = target.eulerAngles;
        x = angles.y;
        y = angles.x;

        // Make the rigid body not change rotation
        Rigidbody rigidbody = this.GetComponent<Rigidbody>();
        if (rigidbody)
            rigidbody.freezeRotation = true;
    }

    void Update()
    {
        //判断触摸数量为单点触摸
        if (Input.touchCount == 1)
        {
            //触摸类型为移动触摸
            if (Input.GetTouch(0).phase == TouchPhase.Moved)
            {
                //根据触摸点计算X与Y位置
                //x += Input.GetAxis("Mouse X") * xSpeed * 0.02f;
                //y -= Input.GetAxis("Mouse Y") * ySpeed * 0.02f;
                //target.eulerAngles = new Vector3(y,x,transform.eulerAngles.z);
                //Debug.Log("当前正在执行"+x+"_________"+y);
                Debug.Log("当前正在执行" + xSpeed + "_________" + ySpeed);
                //    Debug.Log("当前正在执行" + Input.GetAxis("Mouse X") + "_________" + Input.GetAxis("Mouse Y"));
                Vector2 touchDeltaPosition = Input.GetTouch(0).deltaPosition;
                x += touchDeltaPosition.x * xSpeed * 0.0002f;
                y -= touchDeltaPosition.y * ySpeed * 0.0002f;
                transform.eulerAngles = new Vector3(y, x, transform.eulerAngles.z);
                //this.transform.Rotate(touchDeltaPosition.y * 0.02f, touchDeltaPosition.x*0.02f,0,Space.Self);
              
            }
            else if(Input.GetTouch(0).phase == TouchPhase.Stationary){
                RaycastHit hit;
                
                    if (Physics.Raycast(Camera.main.ScreenPointToRay(Input.mousePosition), out hit, 1000f))
                    {
                        //HighlighterInteractive h = hit.transform.GetComponent<HighlighterInteractive>();
                        //if (h)
                        //{
                        //    h.h.ConstantOnImmediate(Color.red);                            
                        //}
                        if (hit.transform.GetComponent<ComponentAttributes>())
                        {
                            Transform setPosition = componentCamera.transform.Find("SetPosition");
                            if (setPosition.childCount != 0)
                            {
                                for (int i = 0; i < setPosition.childCount; i++)
                                {
                                    Destroy(setPosition.GetChild(i).gameObject);
                                }
                            }
                            GameObject clone = Instantiate(hit.transform.gameObject, setPosition);
                            clone.transform.rotation = hit.transform.rotation;
                            Vector3 cloneEuler = clone.transform.localEulerAngles;
                            clone.transform.localEulerAngles = new Vector3(cloneEuler.x, 90, cloneEuler.z);
                            clone.transform.localPosition = Vector3.zero;
                            clone.layer = LayerMask.NameToLayer("BridgeComponent");
                            componentCamera.gameObject.SetActive(true);
                            componentCamera.fieldOfView = 60f;
                            gameObject.SetActive(false);
                        }
                    }                
            }
        }
        //判断触摸数量为多点触摸
        if (Input.touchCount > 1)
        {

            //前两只手指触摸类型都为移动触摸
            if (Input.GetTouch(0).phase == TouchPhase.Moved||Input.GetTouch(1).phase == TouchPhase.Moved)
    	{
                //计算出当前两点触摸点的位置
                Vector3 tempPosition1 = Input.GetTouch(0).position;
                Vector3 tempPosition2 = Input.GetTouch(1).position;
                //函数返回真为放大，返回假为缩小
                if (isEnlarge(oldPosition1, oldPosition2, tempPosition1, tempPosition2))
                {
                    //放大系数超过3以后不允许继续放大
                    //这里的数据是根据我项目中的模型而调节的，大家可以自己任意修改
                    if (distance > 0)
                    {
                        distance -= 3f;
                        Debug.Log("当前正在执行放大");
                    }
                }
                else
                {
                    //缩小洗漱返回18.5f后不允许继续缩小
                    //这里的数据是根据我项目中的模型而调节的，大家可以自己任意修改
                    if (distance < 40)
                    {
                        distance += 3f;
                        Debug.Log("当前正在执行缩小");
                    }
                }
                //备份上一次触摸点的位置，用于对比
                oldPosition1 = tempPosition1;
                oldPosition2 = tempPosition2;
            }
        }
        //RaycastHit hit;
        //if (Input.GetMouseButtonDown(0))
        //{
        //    if (Physics.Raycast(Camera.main.ScreenPointToRay(Input.mousePosition),out hit,1000f))
        //    {
        //        //HighlighterInteractive h = hit.transform.GetComponent<HighlighterInteractive>();
        //        //if (h)
        //        //{
        //        //    h.h.ConstantOnImmediate(Color.red);                            
        //        //}
        //        if (hit.transform.GetComponent<ComponentAttributes>())
        //        {
        //            Transform setPosition = componentCamera.transform.Find("SetPosition");
        //            if (setPosition.childCount!=0)
        //            {
        //                for (int i = 0; i < setPosition.childCount; i++)
        //                {
        //                    Destroy(setPosition.GetChild(i).gameObject);
        //                }
        //            }
        //            GameObject clone = Instantiate(hit.transform.gameObject,setPosition);
        //            clone.transform.rotation = hit.transform.rotation;
        //            Vector3 cloneEuler = clone.transform.localEulerAngles;
        //            clone.transform.localEulerAngles = new Vector3(cloneEuler.x, 90,cloneEuler.z);     
        //            clone.transform.localPosition = Vector3.zero;                                        
        //            clone.layer = LayerMask.NameToLayer("BridgeComponent");
        //            componentCamera.gameObject.SetActive(true);
        //            gameObject.SetActive(false);
        //        }
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
        //target为我们绑定的箱子变量，缩放旋转的参照物
        if (target)
        {

            //重置摄像机的位置
            y = ClampAngle(y, yMinLimit, yMaxLimit);
            Quaternion rotation = Quaternion.Euler(y, x, 0);
            Vector3 position = rotation * new Vector3(0.0f, 0.0f, -distance) + target.position;

            transform.rotation = rotation;
            transform.position = position;
         //   transform.eulerAngles = new Vector3(ClampAngle(transform.eulerAngles.x, -10f, 10f), ClampAngle(transform.eulerAngles.y, -30f, 30f), transform.rotation.z);
        }
        if(Input.GetKeyDown(KeyCode.Space))
        {

        }
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