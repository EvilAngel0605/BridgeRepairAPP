package net.jsrbc.service;

import android.content.Intent;

import net.jsrbc.activity.system.LoginActivity;
import net.jsrbc.activity.task.MyTaskActivity;
import net.jsrbc.client.DownloadClient;
import net.jsrbc.entity.TblSysMobileVersion;
import net.jsrbc.frame.annotation.client.HttpClient;
import net.jsrbc.frame.base.BaseService;
import net.jsrbc.frame.exception.NonAuthoritativeException;
import net.jsrbc.utils.SystemUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by ZZZ on 2018-01-14.
 */
public class UpdateService extends BaseService {

    public final static String MOBILE_VERSION = "MOBILE_VERSION";

    @HttpClient
    private DownloadClient downloadClient;

    public UpdateService() {
        super("UpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            SystemUtils.showNotification(this, "后台开始下载更新，下载完成后会提示更新...", null);
            TblSysMobileVersion version = (TblSysMobileVersion) intent.getSerializableExtra(MOBILE_VERSION);
            File file = downloadClient.downloadInstallFile(version.getId());
            if (file != null) SystemUtils.installApk(this, file);
        }catch (IOException e) {
            SystemUtils.showNotification(this, "更新文件下载失败，请检查网络后重试", MyTaskActivity.class);
        }catch (NonAuthoritativeException e) {
            SystemUtils.showNotification(this, "账号信息异常，请重新登录后再更新", LoginActivity.class);
        }
    }
}
