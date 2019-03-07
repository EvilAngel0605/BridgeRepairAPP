package net.jsrbc.activity.system;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import net.jsrbc.R;
import net.jsrbc.client.BaseDataClient;
import net.jsrbc.client.DownloadClient;
import net.jsrbc.constant.AndroidConstant;
import net.jsrbc.entity.TblSysMobileVersion;
import net.jsrbc.enumeration.PermissionRequestCode;
import net.jsrbc.frame.annotation.client.HttpClient;
import net.jsrbc.frame.annotation.database.Mapper;
import net.jsrbc.frame.annotation.element.AndroidActivity;
import net.jsrbc.frame.base.BaseActivity;
import net.jsrbc.frame.exception.NonAuthoritativeException;
import net.jsrbc.mapper.BaseDataMapper;
import net.jsrbc.pojo.Token;
import net.jsrbc.service.UpdateService;
import net.jsrbc.utils.DataUtils;
import net.jsrbc.utils.PermissionUtils;
import net.jsrbc.utils.SystemUtils;

import java.io.IOException;

/**
 * Created by ZZZ on 2017-12-01.
 */
@AndroidActivity(R.layout.activity_splash)
public class SplashActivity extends BaseActivity {

    @HttpClient
    private BaseDataClient baseDataClient;

    @HttpClient
    private DownloadClient downloadClient;

    @Mapper
    private BaseDataMapper baseDataMapper;

    @Override
    protected void created() {
        //先获取授权信息
        String[] permissions = PermissionUtils.getMapRequiredPermissions(this);
        if (permissions.length > 0) {
            ActivityCompat.requestPermissions(this, permissions, PermissionRequestCode.PERMISSION_REQUEST.getCode());
        } else {
            checkUser();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (PermissionRequestCode.of(requestCode)) {
            case PERMISSION_REQUEST:
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        SystemUtils.promptWithAction(this, "必须同意所有权限才能使用本程序",
                                (v, i)->ActivityCompat.requestPermissions(this, PermissionUtils.getMapRequiredPermissions(this), PermissionRequestCode.PERMISSION_REQUEST.getCode()));
                        return;
                    }
                }
                checkUser();
                break;
        }
    }

    /** 检查账号登录信息 */
    private void checkUser() {
        //获取账号授权信息
        Token token = DataUtils.getCurrentToken(this);
        if (token.isEmpty()) {
            new Thread(()->{
                SystemUtils.waitSeconds(3);
                SystemUtils.toLogin(SplashActivity.this);
            }).start();
        } else {
            new Thread(()->{
                //如果有网络连接，则更新基础数据，如果没有，则进入首页
                if (SystemUtils.isNetworkConnect(this)) {
                    try {
                        //检查当前是否有版本更新
                        TblSysMobileVersion mobileVersion = downloadClient.getLatestVersion();
                        if (mobileVersion != null && mobileVersion.getVersionCode() > getCurrentVersionCode()) {
                            AndroidConstant.HANDLER.post(()-> SystemUtils.confirm(this, "发现新版本，请确定是否更新：\n" + mobileVersion.getVersionDesc(), (d, i)->{
                                Intent intent = new Intent(SplashActivity.this, UpdateService.class);
                                intent.putExtra(UpdateService.MOBILE_VERSION, mobileVersion);
                                startService(intent);
                                SystemUtils.toMain(SplashActivity.this);
                            }, (d, i)->SystemUtils.toMain(SplashActivity.this)));
                            return;
                        }else {
                            //更新基础数据
                            SystemUtils.downloadBaseData(baseDataMapper, baseDataClient, this);
                        }
                    } catch (NonAuthoritativeException e) {
                        SystemUtils.toLogin(this);
                        return;
                    } catch (IOException e) {
                        AndroidConstant.HANDLER.post(() -> SystemUtils.longToast(this, "网络连接失败，基础数据暂未更新"));
                    }
                } else {
                    SystemUtils.waitSeconds(3);
                }
                SystemUtils.toMain(SplashActivity.this);
            }).start();
        }
    }

    /** 获取最新版本 */
    private int getCurrentVersionCode() {
        try {
            PackageManager packageManager = getPackageManager();
            PackageInfo pi = packageManager.getPackageInfo(getPackageName(), 0);
            return pi.versionCode;
        }catch (PackageManager.NameNotFoundException e) {
            Log.e(getClass().getName(), "版本号查询失败", e);
        }
        return 0;
    }
}
