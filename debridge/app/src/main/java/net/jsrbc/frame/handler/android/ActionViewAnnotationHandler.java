package net.jsrbc.frame.handler.android;

import android.app.Activity;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import net.jsrbc.frame.annotation.element.AndroidActionView;
import net.jsrbc.frame.annotation.event.AndroidEvent;
import net.jsrbc.frame.annotation.event.OnClick;
import net.jsrbc.frame.annotation.event.OnClose;
import net.jsrbc.frame.annotation.event.OnNavigationItemSelected;
import net.jsrbc.frame.annotation.event.OnQueryText;
import net.jsrbc.frame.annotation.event.OnSearchClick;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Created by ZZZ on 2017-12-02.
 */
public final class ActionViewAnnotationHandler {

    private Activity activity;

    private Menu menu;

    /** 准备好 */
    public static ActionViewAnnotationHandler ready(Activity activity, Menu menu) {
        return new ActionViewAnnotationHandler(activity, menu);
    }

    /** 绑定ActionView */
    public ActionViewAnnotationHandler bindActionView() {
        for (Field field : activity.getClass().getDeclaredFields()) {
            try {
                if (!field.isAnnotationPresent(AndroidActionView.class)) continue;
                field.setAccessible(true);
                if (MenuItem.class.isAssignableFrom(field.getType())) {
                    field.set(activity, menu.findItem(field.getAnnotation(AndroidActionView.class).value()));
                } else {
                    field.set(activity, menu.findItem(field.getAnnotation(AndroidActionView.class).value()).getActionView());
                }
            }catch (IllegalAccessException e) {
                Log.e(activity.getClass().getName(), String.format("%s auto wired failed", field.getName()), e);
                throw new RuntimeException(e);
            }
        }
        return this;
    }

    /** 绑定事件 */
    public ActionViewAnnotationHandler bindEvent() {
        for (Field field : activity.getClass().getDeclaredFields()) {
            for (Annotation annotation : field.getDeclaredAnnotations()) {
                if (!annotation.annotationType().isAnnotationPresent(AndroidEvent.class)) continue;
                field.setAccessible(true);
                try {
                    View view = (View) field.get(activity);
                    if (annotation.annotationType() == OnQueryText.class) {
                        if (SearchView.OnQueryTextListener.class.isAssignableFrom(activity.getClass()) && view instanceof SearchView) {
                            ((SearchView) view).setOnQueryTextListener((SearchView.OnQueryTextListener) activity);
                        } else {
                            Log.e(activity.getClass().getName(), String.format("%s is annotated by @OnQueryText, but %s is not implement SearchView.OnQueryTextListener, or view is not instance of SearchView", field.getName(), activity.getClass().getName()));
                        }
                    } else if (annotation.annotationType() == OnSearchClick.class) {
                        if (View.OnClickListener.class.isAssignableFrom(activity.getClass()) && view instanceof SearchView) {
                            ((SearchView) view).setOnSearchClickListener((View.OnClickListener) activity);
                        } else {
                            Log.e(activity.getClass().getName(), String.format("%s is annotated by @OnSearchClick, but %s is not implement View.OnClickListener, or view is not instance of SearchView", field.getName(), activity.getClass().getName()));
                        }
                    } else if (annotation.annotationType() == OnClose.class) {
                        if (SearchView.OnCloseListener.class.isAssignableFrom(activity.getClass()) && view instanceof SearchView) {
                            ((SearchView) view).setOnCloseListener((SearchView.OnCloseListener) activity);
                        } else {
                            Log.e(activity.getClass().getName(), String.format("%s is annotated by @OnClose, but %s is not implement SearchView.OnCloseListener, or view is not instance of SearchView", field.getName(), activity.getClass().getName()));
                        }
                    }
                }catch (IllegalAccessException e) {
                    Log.e(activity.getClass().getName(), String.format("%s is not available", field.getName()), e);
                    throw new RuntimeException(e);
                }
            }
        }
        return this;
    }

    private ActionViewAnnotationHandler(Activity activity, Menu menu) {
        this.activity = activity;
        this.menu = menu;
    }

}
