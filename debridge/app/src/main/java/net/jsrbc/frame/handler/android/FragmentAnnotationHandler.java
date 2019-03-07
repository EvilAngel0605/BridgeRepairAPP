package net.jsrbc.frame.handler.android;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import net.jsrbc.frame.annotation.element.AndroidView;

import java.lang.reflect.Field;

/**
 * Created by zhang on 2017-11-28.
 */

public class FragmentAnnotationHandler extends AnnotationHandler<Fragment> {

    private Fragment fragment;

    private View view;

    /** 准备初始化 */
    public static FragmentAnnotationHandler ready(Fragment fragment, View view) {
        return new FragmentAnnotationHandler(fragment, view);
    }

    /** 绑定视图 */
    public FragmentAnnotationHandler bindView() {
        for (Field field : fragment.getClass().getDeclaredFields()) {
            try {
                if (!field.isAnnotationPresent(AndroidView.class)) continue;
                field.setAccessible(true);
                field.set(fragment, view.findViewById(field.getAnnotation(AndroidView.class).value()));
            }catch (IllegalAccessException e) {
                Log.e(fragment.getClass().getName(), String.format("%s auto wired failed", field.getName()), e);
                throw new RuntimeException(e);
            }
        }
        return this;
    }

    /** 绑定事件 */
    public FragmentAnnotationHandler bindEvent() {
        super.bindEvent(fragment);
        return this;
    }

    private FragmentAnnotationHandler(Fragment fragment, View view) {
        this.fragment = fragment;
        this.view = view;
    }
}
