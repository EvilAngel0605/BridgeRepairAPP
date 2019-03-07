package net.jsrbc.frame.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.jsrbc.frame.annotation.element.AndroidFragment;
import net.jsrbc.frame.handler.android.FragmentAnnotationHandler;

/**
 * Created by zhang on 2017-11-28.
 */
public class BaseFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (!this.getClass().isAnnotationPresent(AndroidFragment.class)) {
            String msg = String.format("%s is not annotated by @AndroidFragment, do you forget?", getClass().getName());
            Log.e(getClass().getName(), msg);
            throw new RuntimeException(msg);
        }
        View view = inflater.inflate(getClass().getAnnotation(AndroidFragment.class).value(), container, false);
        FragmentAnnotationHandler.ready(this, view)
                .bindView()
                .bindEvent();
        return view;
    }
}
