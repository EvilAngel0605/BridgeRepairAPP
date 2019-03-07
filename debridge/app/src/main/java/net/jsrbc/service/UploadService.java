package net.jsrbc.service;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import net.jsrbc.activity.system.LoginActivity;
import net.jsrbc.activity.task.MyTaskActivity;
import net.jsrbc.client.UploadClient;
import net.jsrbc.entity.TblBridge;
import net.jsrbc.entity.TblBridgeDisease;
import net.jsrbc.entity.TblBridgeDiseasePhoto;
import net.jsrbc.entity.TblBridgeParts;
import net.jsrbc.entity.TblBridgeSide;
import net.jsrbc.enumeration.ConnectionStatus;
import net.jsrbc.enumeration.PhotoType;
import net.jsrbc.enumeration.UploadStatus;
import net.jsrbc.frame.annotation.client.HttpClient;
import net.jsrbc.frame.annotation.database.Mapper;
import net.jsrbc.frame.base.BaseService;
import net.jsrbc.frame.exception.NonAuthoritativeException;
import net.jsrbc.mapper.BridgeDiseaseMapper;
import net.jsrbc.mapper.BridgeDiseasePhotoMapper;
import net.jsrbc.mapper.BridgeMapper;
import net.jsrbc.mapper.BridgePartsMapper;
import net.jsrbc.mapper.BridgeSideMapper;
import net.jsrbc.mapper.BridgeSiteMapper;
import net.jsrbc.pojo.RequestResult;
import net.jsrbc.utils.FileUtils;
import net.jsrbc.utils.StringUtils;
import net.jsrbc.utils.SystemUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static net.jsrbc.pojo.RequestResult.Status.SUCCESS;

/**
 * Created by ZZZ on 2018-01-03.
 */
public class UploadService extends BaseService {

    private static volatile boolean isUploading = false;

    /** 上传状态 */
    public final static String UPLOAD_STATUS = UUID.randomUUID().toString();

    /** 上传进度 */
    public final static String UPLOAD_PROGRESS = UUID.randomUUID().toString();

    /** 当前正在上传的桥梁 */
    public final static String CURRENT_UPLOAD_BRIDGE = UUID.randomUUID().toString();

    @Mapper
    private BridgeMapper bridgeMapper;

    @Mapper
    private BridgeSideMapper bridgeSideMapper;

    @Mapper
    private BridgeSiteMapper bridgeSiteMapper;

    @Mapper
    private BridgePartsMapper bridgePartsMapper;

    @Mapper
    private BridgeDiseaseMapper bridgeDiseaseMapper;

    @Mapper
    private BridgeDiseasePhotoMapper bridgeDiseasePhotoMapper;

    @HttpClient
    private UploadClient uploadClient;

    /** 是否正在上传 */
    public static boolean isUploading() {
        return isUploading;
    }

    private LocalBroadcastManager localBroadcastManager;

    @Override
    protected void created() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onHandleIntent(Intent intent) {
        isUploading = true;
        TblBridge bridge = (TblBridge) intent.getSerializableExtra(CURRENT_UPLOAD_BRIDGE);
        //初始化这座桥的进度
        bridge.setProgress(0);
        sendUpdateProgressBroadcast(bridge);
        try {
            //开始上传
            //上传经纬度信息
            boolean isBridgeLocationUploadSuccess = true;
            if (bridge.getUploadLocation() == UploadStatus.NEED_UPLOAD.getCode()) {
                RequestResult result = uploadClient.uploadBridgeLocation(bridge);
                if (result.getStatus() != SUCCESS) isBridgeLocationUploadSuccess = false;
                else bridgeMapper.setBridgeLocationUploadSuccess(bridge.getId());
            }
            if (isBridgeLocationUploadSuccess) sendUpdateProgressBroadcast(bridge);
            //上传正侧面照
            boolean isBridgePhotoUploadSuccess = true;
            if (bridge.getUploadPhoto() == UploadStatus.NEED_UPLOAD.getCode()) {
                if (!StringUtils.isEmpty(bridge.getFrontPath()) && FileUtils.exists(bridge.getFrontPath())) {
                    RequestResult result = uploadClient.uploadBridgePhoto(new File(bridge.getFrontPath()), bridge.getId(), bridge.getTaskBridgeId(), PhotoType.FRONT.getText());
                    if (result.getStatus() != SUCCESS) isBridgePhotoUploadSuccess = false;
                }
                if (!StringUtils.isEmpty(bridge.getSidePath()) && FileUtils.exists(bridge.getSidePath())) {
                    RequestResult result = uploadClient.uploadBridgePhoto(new File(bridge.getSidePath()), bridge.getId(), bridge.getTaskBridgeId(), PhotoType.SIDE.getText());
                    if (result.getStatus() != SUCCESS) isBridgePhotoUploadSuccess = false;
                }
                if (!StringUtils.isEmpty(bridge.getUpwardPath()) && FileUtils.exists(bridge.getUpwardPath())) {
                    RequestResult result = uploadClient.uploadBridgePhoto(new File(bridge.getUpwardPath()), bridge.getId(), bridge.getTaskBridgeId(), PhotoType.UPWARD.getText());
                    if (result.getStatus() != SUCCESS) isBridgePhotoUploadSuccess = false;
                }
            }
            if (isBridgePhotoUploadSuccess) {
                bridgeMapper.setBridgePhotoUploadSuccess(bridge.getId());
                sendUpdateProgressBroadcast(bridge);
            }
            //上传分幅信息
            List<TblBridgeSide> bridgeSideList = bridgeSideMapper.getBridgeSideListForUpload(bridge.getId());
            if (bridgeSideList != null) bridgeSideMapper.addOrReplaceBridgeSideList(uploadClient.uploadBridgeSide(bridgeSideList));
            sendUpdateProgressBroadcast(bridge);
            //上传孔跨信息
            List<TblBridgeSide> bridgeSideListForUploadSite = bridgeSideMapper.getBridgeSideListForUploadSite(bridge.getId());
            if (bridgeSideListForUploadSite != null) {
                RequestResult siteResult = uploadClient.uploadSpanCombination(bridgeSideListForUploadSite);
                if (siteResult.getStatus() == SUCCESS) bridgeSideListForUploadSite.forEach(s -> bridgeSiteMapper.setSiteUploadSuccess(s));
            }
            sendUpdateProgressBroadcast(bridge);
            //上传部件信息
            List<TblBridgeParts> bridgePartsList = bridgePartsMapper.getBridgePartsListForUpload(bridge.getId());
            if (bridgePartsList != null) bridgePartsMapper.addOrReplaceBridgePartsList(uploadClient.uploadBridgeParts(bridgePartsList));
            sendUpdateProgressBroadcast(bridge);
            //上传病害信息
            List<TblBridgeDisease> bridgeDiseaseList = bridgeDiseaseMapper.getDiseaseListForUpload(bridge.getTaskId(), bridge.getId());
            if (bridgeDiseaseList != null) bridgeDiseaseMapper.addOrReplaceDiseaseList(uploadClient.uploadBridgeDisease(bridgeDiseaseList));
            sendUpdateProgressBroadcast(bridge);
            //上传病害照片
            List<TblBridgeDiseasePhoto> diseasePhotoList = bridgeDiseasePhotoMapper.getDiseasePhotoListForUpload(bridge.getId());
            boolean isDiseasePhotoUploadSuccess = true;
            if (diseasePhotoList != null) {
                for (TblBridgeDiseasePhoto photo : diseasePhotoList) {
                    if (FileUtils.exists(photo.getPath())) {
                        RequestResult result = uploadClient.uploadDiseasePhoto(new File(photo.getPath()), photo.getDiseaseId(), bridge.getId(), bridge.getTaskId());
                        if (result == null) {
                            bridgeDiseasePhotoMapper.setDiseasePhotoUploadSuccess(photo.getId());
                        } else {
                            if (result.getStatus() == SUCCESS) bridgeDiseasePhotoMapper.setDiseasePhotoUploadSuccess(photo.getId());
                            else isDiseasePhotoUploadSuccess = false;
                        }
                    } else {
                        bridgeDiseasePhotoMapper.setDiseasePhotoUploadSuccess(photo.getId());
                    }
                }
            }
            if (isDiseasePhotoUploadSuccess) sendUpdateProgressBroadcast(bridge);
            //提示数据上传成功
            SystemUtils.showNotification(this, String.format("%s数据上传结束", bridge.getName()), MyTaskActivity.class);
        }catch (IOException e) {
            sendUploadFailedBroadcast();
            SystemUtils.showNotification(this, "上传失败，请检查网络后重试", MyTaskActivity.class);
        }catch (NonAuthoritativeException e) {
            sendNonAuthBroadcast();
            SystemUtils.showNotification(this, "账号信息异常，请重新登录", LoginActivity.class);
        }finally {
            isUploading = false;
        }
    }

    /** 发送上传成功广播 */
    private void sendUpdateProgressBroadcast(TblBridge bridge) {
        bridge.setProgress(bridge.getProgress() + 1);
        bridgeMapper.updateBridgeProgress(bridge);
        Intent intent = new Intent(UPLOAD_PROGRESS);
        intent.putExtra(UPLOAD_STATUS, ConnectionStatus.LINKED);
        intent.putExtra(CURRENT_UPLOAD_BRIDGE, bridge);
        localBroadcastManager.sendBroadcast(intent);
    }

    /** 发送下载失败的广播 */
    private void sendUploadFailedBroadcast() {
        Intent intent = new Intent(UPLOAD_PROGRESS);
        intent.putExtra(UPLOAD_STATUS, ConnectionStatus.DISCONNECTED);
        localBroadcastManager.sendBroadcast(intent);
    }

    /** 发送下载失败的广播 */
    private void sendNonAuthBroadcast() {
        Intent intent = new Intent(UPLOAD_PROGRESS);
        intent.putExtra(UPLOAD_STATUS, ConnectionStatus.NON_AUTH);
        localBroadcastManager.sendBroadcast(intent);
    }

    public UploadService() {
        super("UploadService");
    }
}
