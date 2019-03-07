package net.jsrbc.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import net.jsrbc.R;
import net.jsrbc.activity.system.LoginActivity;
import net.jsrbc.activity.MainActivity;
import net.jsrbc.client.BaseDataClient;
import net.jsrbc.frame.exception.NonAuthoritativeException;
import net.jsrbc.mapper.BaseDataMapper;
import net.jsrbc.mapper.TaskMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Android系统工具
 * Created by ZZZ on 2017-11-30.
 */
public final class SystemUtils {

    /**
     * 检测网络是否连接
     * @param context  上下文环境
     * @return         true-连接状态、false-未连接
     */
    public static boolean isNetworkConnect(Context context) {
        if (context != null) {
            ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (manager != null) {
                NetworkInfo networkInfo = manager.getActiveNetworkInfo();
                if (networkInfo != null) return networkInfo.getState() == NetworkInfo.State.CONNECTED;
            }
        }
        return false;
    }

    /**
     * 安装更新文件
     * @param file 安装文件
     */
    public static void installApk(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri apkUri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            apkUri = FileProvider.getUriForFile(context, "net.jsrbc.fileprovider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            apkUri = Uri.fromFile(file);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 强制隐藏键盘
     * @param view     放入布局中的任意View
     * @param context  传入一个上下文环境
     */
    public static void hideSoftInputFromWindow(View view, Context context) {
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager != null) manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    /**
     * 显示警告对话框
     * @param context         上下文
     * @param formatText      警告的信息，格式化字符串
     * @param args            格式化字符串的参数
     */
    public static void alert(Context context, String formatText, Object... args) {
        new AlertDialog.Builder(context)
                .setTitle("注意")
                .setMessage(String.format(formatText, args))
                .setCancelable(true)
                .setIcon(R.drawable.ic_dialog_alert)
                .setPositiveButton("确定", null)
                .create()
                .show();
    }

    /**
     * 显示提示对话框
     * @param context         上下文
     * @param formatText      警告的信息，格式化字符串
     * @param args            格式化字符串的参数
     */
    public static void prompt(Context context, String formatText, Object... args) {
        new AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage(String.format(formatText, args))
                .setCancelable(true)
                .setIcon(R.drawable.ic_dialog_info)
                .setPositiveButton("知道了", null)
                .create()
                .show();
    }

    /**
     * 显示提示对话框
     * @param context         上下文
     * @param text            信息，格式化字符串
     */
    public static void promptWithAction(Context context, String text, DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage(text)
                .setCancelable(false)
                .setIcon(R.drawable.ic_dialog_info)
                .setPositiveButton("知道了", onClickListener)
                .create()
                .show();
    }

    /**
     * 显示确定对话框
     * @param context             上下文
     * @param text                信息，格式化字符串
     * @param confirmListener     点击确定后的事件
     */
    public static void confirm(Context context, String text, DialogInterface.OnClickListener confirmListener) {
        new AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage(text)
                .setCancelable(false)
                .setIcon(R.drawable.ic_dialog_confirm)
                .setPositiveButton("确定", confirmListener)
                .setNegativeButton("取消", null)
                .create()
                .show();
    }

    /**
     * 显示确定对话框
     * @param context             上下文
     * @param text                信息，格式化字符串
     * @param confirmListener     点击确定后的事件
     */
    public static void confirm(Context context, String text, DialogInterface.OnClickListener confirmListener, DialogInterface.OnClickListener cancelListener) {
        new AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage(text)
                .setCancelable(false)
                .setIcon(R.drawable.ic_dialog_confirm)
                .setPositiveButton("确定", confirmListener)
                .setNegativeButton("取消", cancelListener)
                .create()
                .show();
    }

    /**
     * 列表选择对话框
     * @param context          上下文环境
     * @param items            供选择项
     * @param onClickListener  点击后的事件
     */
    public static <T> void choose(Context context, List<T> items, DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(context)
                .setTitle("请选择")
                .setIcon(R.drawable.ic_item_bridge)
                .setItems(items.stream().map(String::valueOf).collect(Collectors.toList()).toArray(new String[items.size()]), onClickListener)
                .setCancelable(true)
                .create()
                .show();
    }

    /**
     * 显示简要的提示语句（短时间显示）
     * @param context         上下文
     * @param formatText      警告的信息，格式化字符串
     * @param args            格式化字符串的参数
     */
    public static void shortToast(Context context, String formatText, Object... args) {
        Toast.makeText(context, String.format(formatText, args), Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示简要的提示语句（长时间显示）
     * @param context         上下文
     * @param formatText      警告的信息，格式化字符串
     * @param args            格式化字符串的参数
     */
    public static void longToast(Context context, String formatText, Object... args) {
        Toast.makeText(context, String.format(formatText, args), Toast.LENGTH_LONG).show();
    }

    /** 会话到期，提示重新登录 */
    public static void sessionTimeout(Activity context) {
        new AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage("登录信息已过期，请重新登录")
                .setCancelable(false)
                .setIcon(R.drawable.ic_dialog_info)
                .setPositiveButton("知道了", (d, i) -> {
                    Intent intent = new Intent(context, LoginActivity.class);
                    context.startActivity(intent);
                    context.finish();
                })
                .create()
                .show();
    }

    /** 会话到期，提示重新登录 */
    public static void sessionTimeout(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage("登录信息已过期，请重新登录")
                .setCancelable(false)
                .setIcon(R.drawable.ic_dialog_info)
                .setPositiveButton("知道了", (d, i) -> {
                    Intent intent = new Intent(context, LoginActivity.class);
                    context.startActivity(intent);
                })
                .create()
                .show();
    }

    /** 显示提醒 */
    public static void showNotification(Context context, String text, @Nullable Class<? extends Activity> activityClass) {
        String packageName = "net.jsrbc.debridge";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(packageName, packageName, NotificationManager.IMPORTANCE_HIGH);
           if (notificationManager != null) notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, packageName);
        builder.setContentTitle(context.getString(R.string.app_name))
                .setContentText(text)
                .setTicker(text)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.logo)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.logo))
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setAutoCancel(true);
        if (activityClass != null) {
            Intent intent = new Intent(context, activityClass);
            builder.setContentIntent(PendingIntent.getActivity(context, context.getString(R.string.app_name).hashCode(), intent, context.getString(R.string.app_name).hashCode()));
        }
        if (notificationManager != null) {
            notificationManager.notify(context.getString(R.string.app_name).hashCode(), builder.build());
        }
    }

    /** 下载基础数据 */
    public static void downloadBaseData(BaseDataMapper baseDataMapper, BaseDataClient baseDataClient, Activity context) throws NonAuthoritativeException, IOException {
        //先清空所有基础数据
        baseDataMapper.clearBaseData();
        //结构类型相关数据
        baseDataMapper.addOrReplaceParamGroupList(baseDataClient.getParamGroupList());
        baseDataMapper.addOrReplaceParamTypeList(baseDataClient.getParamTypeList());
        baseDataMapper.addOrReplaceStructureGroupList(baseDataClient.getParamStructureGroupList());
        baseDataMapper.addOrReplaceStructureTypeList(baseDataClient.getParamStructureTypeList());
        //部构件相关数据
        baseDataMapper.addOrReplacePositionTypeList(baseDataClient.getParamPositionTypeList());
        baseDataMapper.addOrReplacePartsTypeList(baseDataClient.getParamPartsTypeList());
        baseDataMapper.addOrReplaceMemberTypeList(baseDataClient.getParamMemberTypeList());
        baseDataMapper.addOrReplaceMemberDescList(baseDataClient.getParamMemberDescList());
        baseDataMapper.addOrReplaceBridgePartsRelationList(baseDataClient.getBridgePartsRelationList());
        baseDataMapper.addOrReplaceAbutmentMemberRelationList(baseDataClient.getAbutmentMemberRelationList());
        baseDataMapper.addOrReplaceSuperstructureMemberRelationList(baseDataClient.getSuperstructureMemberRelationList());
        //病害相关数据
        baseDataMapper.addOrReplaceDiseaseDescList(baseDataClient.getParamDiseaseDescList());
        baseDataMapper.addOrReplaceDiseaseTypeList(baseDataClient.getParamDiseaseTypeList());
        baseDataMapper.addOrReplaceEvaluationIndexList(baseDataClient.getParamEvaluationIndexList());
        baseDataMapper.addOrReplaceMemberDiseaseRelationList(baseDataClient.getMemberDiseaseRelationList());
    }

    /** 删除任务单相关的数据 */
    public static void deleteTaskCascade(TaskMapper taskMapper, String taskId) {
        taskMapper.deleteTaskMember(taskId);
        taskMapper.deleteTaskParts(taskId);
        taskMapper.deleteTaskSite(taskId);
        taskMapper.deleteTaskSide(taskId);
        taskMapper.deleteTaskBridge(taskId);
    }

    /** 跳转到主页 */
    public static void toMain(Activity context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
        context.finish();
    }

    /** 跳转到登录页 */
    public static void toLogin(Activity context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
        context.finish();
    }

    /** 返回桌面 */
    public static void toDesk(Context context) {
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        home.addCategory(Intent.CATEGORY_HOME);
        context.startActivity(home);
    }

    /** 当前线程等待几秒，异常已经内部处理 */
    public static void waitSeconds(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            Log.e(SystemUtils.class.getName(), "sleep has been interrupted", e);
        }
    }

    /** 获取屏幕宽度 */
    public static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (manager == null) return 0;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    /** 封闭构造函数 */
    private SystemUtils() {}
}
