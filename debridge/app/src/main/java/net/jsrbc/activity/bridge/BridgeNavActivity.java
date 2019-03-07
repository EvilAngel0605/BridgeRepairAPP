package net.jsrbc.activity.bridge;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;

import com.baidu.navisdk.adapter.BNRouteGuideManager;

import net.jsrbc.utils.SystemUtils;

/**
 * Created by ZZZ on 2018-03-07.
 */
public class BridgeNavActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(BNRouteGuideManager.getInstance().onCreate(this, navigationListener));
    }

    @Override
    protected void onStart() {
        super.onStart();
        BNRouteGuideManager.getInstance().onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BNRouteGuideManager.getInstance().onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        BNRouteGuideManager.getInstance().onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        BNRouteGuideManager.getInstance().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BNRouteGuideManager.getInstance().onDestroy();
    }

    @Override
    public void onBackPressed() {
        BNRouteGuideManager.getInstance().onBackPressed(false);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        BNRouteGuideManager.getInstance().onConfigurationChanged(newConfig);
    }

    private BNRouteGuideManager.OnNavigationListener navigationListener = new BNRouteGuideManager.OnNavigationListener() {
        @Override
        public void onNaviGuideEnd() {
            finish();
        }

        @Override
        public void notifyOtherAction(int actionType, int arg1, int arg2, Object o) {
            if (actionType == 0) SystemUtils.shortToast(BridgeNavActivity.this, "导航结束！");
        }
    };
}
