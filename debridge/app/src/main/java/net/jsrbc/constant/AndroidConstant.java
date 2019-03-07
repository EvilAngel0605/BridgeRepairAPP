package net.jsrbc.constant;

import android.os.Environment;
import android.os.Handler;
import android.util.Size;

import net.jsrbc.enumeration.InspectionDirection;
import net.jsrbc.utils.DataUtils;
import net.jsrbc.utils.FileUtils;

/**
 * Created by ZZZ on 2017-11-30.
 */
public final class AndroidConstant {

    /** 事件的Handler对象 */
    public final static Handler HANDLER = new Handler();

    /** 缓存的当前用户键值 */
    public final static String TOKEN = "TOKEN";

    /** 检测方向 */
    public final static String INSPECTION_DIRECTION = "INSPECTION_DIRECTION";

    /** 基础路径 */
    public final static String BASE_DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/ZZZ/";

    /** 临时文件路径 */
    public final static String TEMP_DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/ZZZ/TEMP/";

    /** 照片路径 */
    public final static String PHOTO_DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/ZZZ/DCIM/";

    /** 照片统一尺寸 */
    public final static Size PHOTO_SIZE = new Size(1920, 1080);

    /** 初始化 */
    public static void init() {
        //初始化文件路径
        FileUtils.createDirectory(BASE_DIRECTORY);
        FileUtils.createDirectory(TEMP_DIRECTORY);
        FileUtils.createDirectory(PHOTO_DIRECTORY);
    }

    /** 封闭构造函数 */
    private AndroidConstant() {}
}
