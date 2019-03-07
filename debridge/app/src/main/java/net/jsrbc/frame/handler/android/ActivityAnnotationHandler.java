package net.jsrbc.frame.handler.android;

import android.app.Activity;
import android.util.Log;

import net.jsrbc.frame.annotation.element.AndroidActivity;
import net.jsrbc.frame.annotation.element.AndroidView;

import java.lang.reflect.Field;

/**
 * Created by ZZZ on 2017-11-27.
 */
public final class ActivityAnnotationHandler extends AnnotationHandler<Activity> {

    /** 活动对象 */
    private Activity activity;

    /** 处理器就绪 */
    public static ActivityAnnotationHandler ready(Activity activity) {
        //判断传入的对象是否包含@AndroidActivity注解，不包含则报运行时错误
        if(!activity.getClass().isAnnotationPresent(AndroidActivity.class)) {
            String msg = String.format("%s is not declared by @AndroidActivity", activity.getClass().getName());
            Log.e(activity.getClass().getName(), msg);
            throw new RuntimeException(msg);
        }
        //初始化
        return new ActivityAnnotationHandler(activity);
    }

    /** 设置布局文件，就绪后应该第一步执行这个 */
    public ActivityAnnotationHandler setContentView() {
        activity.setContentView(activity.getClass().getAnnotation(AndroidActivity.class).value());
        return this;
    }

    /** 绑定视图 */
    public ActivityAnnotationHandler bindView() {
        for (Field field : activity.getClass().getDeclaredFields()) {
            try {
                if (!field.isAnnotationPresent(AndroidView.class)) continue;
                field.setAccessible(true);
                field.set(activity, activity.findViewById(field.getAnnotation(AndroidView.class).value()));
            }catch (IllegalAccessException e) {
                Log.e(activity.getClass().getName(), String.format("%s auto wired failed", field.getName()), e);
                throw new RuntimeException(e);
            }
        }
        return this;
    }

    /** 绑定事件 */
    public ActivityAnnotationHandler bindEvent() {
        super.bindEvent(activity);
        return this;
    }

    /** 构造函数私有化 */
    private ActivityAnnotationHandler(Activity activity) {
        this.activity = activity;
    }
}
