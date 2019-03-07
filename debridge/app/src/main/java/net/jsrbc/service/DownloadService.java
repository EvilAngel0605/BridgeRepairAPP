package net.jsrbc.service;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import net.jsrbc.R;
import net.jsrbc.activity.system.LoginActivity;
import net.jsrbc.activity.task.MyTaskActivity;
import net.jsrbc.activity.task.TaskDownloadActivity;
import net.jsrbc.client.DownloadClient;
import net.jsrbc.entity.TblBridgeMember;
import net.jsrbc.entity.TblBridgeParts;
import net.jsrbc.entity.TblTask;
import net.jsrbc.enumeration.ConnectionStatus;
import net.jsrbc.frame.annotation.client.HttpClient;
import net.jsrbc.frame.annotation.database.Mapper;
import net.jsrbc.frame.base.BaseService;
import net.jsrbc.frame.exception.NonAuthoritativeException;
import net.jsrbc.mapper.BridgeMapper;
import net.jsrbc.mapper.BridgeMemberMapper;
import net.jsrbc.mapper.BridgePartsMapper;
import net.jsrbc.mapper.BridgeSideMapper;
import net.jsrbc.mapper.BridgeSiteMapper;
import net.jsrbc.mapper.TaskMapper;
import net.jsrbc.pojo.BridgePartsForm;
import net.jsrbc.utils.JsonUtils;
import net.jsrbc.utils.MemberUtils;
import net.jsrbc.utils.SystemUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by ZZZ on 2017-12-03.
 */
public class DownloadService extends BaseService {

    /** 下载状态 */
    public final static String DOWNLOAD_STATUS = UUID.randomUUID().toString();

    /** 下载进度 */
    public final static String DOWNLOAD_PROGRESS = UUID.randomUUID().toString();

    /** 当前正在下载的任务单 */
    public final static String CURRENT_DOWNLOAD_TASK = UUID.randomUUID().toString();

    /** 下载任务是否正在执行 */
    private static boolean isExecuting = false;

    @HttpClient
    private DownloadClient downloadClient;

    @Mapper
    private BridgeMapper bridgeMapper;

    @Mapper
    private BridgeSideMapper bridgeSideMapper;

    @Mapper
    private BridgeSiteMapper bridgeSiteMapper;

    @Mapper
    private TaskMapper taskMapper;

    @Mapper
    private BridgePartsMapper bridgePartsMapper;

    @Mapper
    private BridgeMemberMapper bridgeMemberMapper;

    private LocalBroadcastManager localBroadcastManager;

    /** 当前任务单是否正在执行中 */
    public static boolean isExecuting() {
        return isExecuting;
    }

    @Override
    protected void created() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        isExecuting = true;
        List<TblTask> taskList = JsonUtils.toList(intent.getStringExtra(TaskDownloadActivity.DOWNLOAD_TASK), TblTask.class);
        try {
            for (TblTask task : taskList) {
                //清空
                SystemUtils.deleteTaskCascade(taskMapper, task.getId());
                //下载桥梁
                bridgeMapper.addOrReplaceBridgeList(downloadClient.getTaskBridgeList(task.getId()));
                sendUpdateProgressBroadcast(task);
                //下载分幅
                bridgeSideMapper.addOrReplaceBridgeSideList(downloadClient.getTaskBridgeSideList(task.getId()));
                sendUpdateProgressBroadcast(task);
                //下载孔跨
                bridgeSiteMapper.addOrReplaceBridgeSiteList(downloadClient.getTaskBridgeSiteList(task.getId()));
                sendUpdateProgressBroadcast(task);
                //下载部构件
                List<TblBridgeParts> bridgeParts = downloadClient.getTaskBridgePartsList(task.getId());
                bridgePartsMapper.addOrReplaceBridgePartsList(bridgeParts);
                //生成构件
                List<TblBridgeMember> bridgeMemberList = new ArrayList<>();
                bridgeParts.forEach(p-> bridgeMemberList.addAll(MemberUtils.generateBridgeMembers(p)));
                int limit = 1000;
                for (int i=0; i<=bridgeMemberList.size(); i += limit) {
                    bridgeMemberMapper.addOrReplaceBridgeMemberList(bridgeMemberList.subList(i, (i + limit) > bridgeMemberList.size() ? bridgeMemberList.size() : (i + limit)));
                }
                sendUpdateProgressBroadcast(task);
            }
            SystemUtils.showNotification(this, "任务单下载完成", MyTaskActivity.class);
        }catch (IOException e) {
            sendDownloadFailedBroadcast();
            SystemUtils.showNotification(this, "下载失败，请检查网络后重试", MyTaskActivity.class);
        }catch (NonAuthoritativeException e) {
            sendNonAuthBroadcast();
            SystemUtils.showNotification(this, "账号信息异常，请重新登录", LoginActivity.class);
        }finally {
            isExecuting = false;
        }
    }

    /** 发送更新进度的广播 */
    private void sendUpdateProgressBroadcast(TblTask task) {
        task.setProgress(task.getProgress() + 1);
        taskMapper.updateTaskProgress(task);
        Intent intent = new Intent(DOWNLOAD_PROGRESS);
        intent.putExtra(DOWNLOAD_STATUS, ConnectionStatus.LINKED);
        intent.putExtra(CURRENT_DOWNLOAD_TASK, JsonUtils.toJson(task));
        localBroadcastManager.sendBroadcast(intent);
    }

    /** 发送下载失败的广播 */
    private void sendDownloadFailedBroadcast() {
        Intent intent = new Intent(DOWNLOAD_PROGRESS);
        intent.putExtra(DOWNLOAD_STATUS, ConnectionStatus.DISCONNECTED);
        localBroadcastManager.sendBroadcast(intent);
    }

    /** 发送下载失败的广播 */
    private void sendNonAuthBroadcast() {
        Intent intent = new Intent(DOWNLOAD_PROGRESS);
        intent.putExtra(DOWNLOAD_STATUS, ConnectionStatus.NON_AUTH);
        localBroadcastManager.sendBroadcast(intent);
    }

    public DownloadService() {
        super("DownloadService");
    }
}
