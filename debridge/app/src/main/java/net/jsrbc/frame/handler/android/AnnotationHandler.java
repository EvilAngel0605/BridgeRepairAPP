package net.jsrbc.frame.handler.android;

import android.support.design.widget.NavigationView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import net.jsrbc.frame.annotation.event.AndroidEvent;
import net.jsrbc.frame.annotation.event.OnCheckedChange;
import net.jsrbc.frame.annotation.event.OnClick;
import net.jsrbc.frame.annotation.event.OnClose;
import net.jsrbc.frame.annotation.event.OnItemSelected;
import net.jsrbc.frame.annotation.event.OnNavigationItemSelected;
import net.jsrbc.frame.annotation.event.OnQueryText;
import net.jsrbc.frame.annotation.event.OnSearchClick;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Created by zhang on 2017-11-28.
 */
class AnnotationHandler<T> {

    /** 绑定事件 */
    final void bindEvent(T context) {
        for (Field field : context.getClass().getDeclaredFields()) {
            for (Annotation annotation : field.getDeclaredAnnotations()) {
                if (!annotation.annotationType().isAnnotationPresent(AndroidEvent.class)) continue;
                field.setAccessible(true);
                try {
                    View view = (View) field.get(context);
                    if (annotation.annotationType() == OnClick.class) {
                        if (View.OnClickListener.class.isAssignableFrom(context.getClass())) view.setOnClickListener((View.OnClickListener) context);
                        else Log.e(context.getClass().getName(), String.format("%s is annotated by @OnClick, but %s is not implement View.OnClickListener, do you forget?", field.getName(), context.getClass().getName()));
                    } else if (annotation.annotationType() == OnNavigationItemSelected.class) {
                        if (NavigationView.OnNavigationItemSelectedListener.class.isAssignableFrom(context.getClass()) && view instanceof NavigationView) {
                            ((NavigationView)view).setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) context);
                        } else {
                            Log.e(context.getClass().getName(), String.format("%s is annotated by @OnNavigationItemSelected, but %s is not implement NavigationView.OnNavigationItemSelectedListener, or view is not instance of NavigationView", field.getName(), context.getClass().getName()));
                        }
                    } else if (annotation.annotationType() == OnCheckedChange.class) {
                        if (CompoundButton.OnCheckedChangeListener.class.isAssignableFrom(context.getClass()) && view instanceof CompoundButton) {
                            ((CompoundButton)view).setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener)context);
                        } else if (RadioGroup.OnCheckedChangeListener.class.isAssignableFrom(context.getClass()) && view instanceof RadioGroup) {
                            ((RadioGroup)view).setOnCheckedChangeListener((RadioGroup.OnCheckedChangeListener) context);
                        } else {
                            Log.e(context.getClass().getName(), String.format("%s is annotated by @OnCheckedChange, but %s is not implement CompoundButton.OnCheckedChangeListener or RadioGroup.OnCheckedChangeListener, or view is not instance of CompoundButton or RadioGroup", field.getName(), context.getClass().getName()));
                        }
                    } else if (annotation.annotationType() == OnItemSelected.class) {
                        if (AdapterView.OnItemSelectedListener.class.isAssignableFrom(context.getClass()) && view instanceof AdapterView) {
                            ((AdapterView)view).setOnItemSelectedListener((AdapterView.OnItemSelectedListener) context);
                        } else {
                            Log.e(context.getClass().getName(), String.format("%s is annotated by @OnItemSelected, but %s is not implement AdapterView.OnItemSelectedListener, do you forget?", field.getName(), context.getClass().getName()));
                        }
                    }
                }catch (IllegalAccessException e) {
                    Log.e(context.getClass().getName(), String.format("%s is not available", field.getName()), e);
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
