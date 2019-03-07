package net.jsrbc;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.navisdk.adapter.BNOuterLogUtil;

import net.jsrbc.constant.AndroidConstant;

/**
 * 全局初始化放在这里
 * Created by ZZZ on 2017-12-06.
 */
public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化百度地图
        SDKInitializer.initialize(this);
        //设置坐标类型
        SDKInitializer.setCoordType(CoordType.BD09LL);
        //关闭百度导航日志
        BNOuterLogUtil.setLogSwitcher(false);
        //初始化常量
        AndroidConstant.init();
    }
}
