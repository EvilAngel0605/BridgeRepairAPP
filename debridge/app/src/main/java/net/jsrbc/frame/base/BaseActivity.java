package net.jsrbc.frame.base;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import net.jsrbc.R;
import net.jsrbc.constant.AndroidConstant;
import net.jsrbc.frame.annotation.element.AndroidMenu;
import net.jsrbc.frame.handler.android.ActionViewAnnotationHandler;
import net.jsrbc.frame.handler.android.ActivityAnnotationHandler;
import net.jsrbc.frame.handler.client.ClientAnnotationHandler;
import net.jsrbc.frame.handler.database.DatabaseAnnotationHandler;
import net.jsrbc.utils.ImageUtils;

/**
 * Created by ZZZ on 2017-11-27.
 */
@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    /** 非明确进度对话框 */
    private AlertDialog progressDialog;

    /** 数据库处理对象 */
    private DatabaseAnnotationHandler databaseAnnotationHandler;

    /** activity create 完成回调 */
    protected void created() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //强制锁定为竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //初始化视图、事件等
        ActivityAnnotationHandler.ready(this)
                .setContentView()
                .bindView()
                .bindEvent();
        //初始化数据库访问对象
        databaseAnnotationHandler = DatabaseAnnotationHandler.newInstance(this);
        databaseAnnotationHandler.bindMapper();
        //初始化客户端
        ClientAnnotationHandler.ready(this).bindClient();
        //创建完成后的事件回调
        created();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //如果没有菜单，则返回false
        if (!this.getClass().isAnnotationPresent(AndroidMenu.class)) return false;
        //加载菜单
        getMenuInflater().inflate(getClass().getAnnotation(AndroidMenu.class).value(), menu);
        //绑定菜单相关内容
        ActionViewAnnotationHandler.ready(this, menu)
                .bindActionView()
                .bindEvent();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        databaseAnnotationHandler.closeDatabase();
        super.onDestroy();
    }

    /** 设置默认的toolbar，有返回箭头 */
    protected final void setDefaultToolbar() {
        //设置toolbar，打开返回箭头
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /** 显示进度对话框 */
    protected  final void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new AlertDialog.Builder(this)
                    .setView(R.layout.dialog_progress)
                    .setCancelable(false)
                    .create();
        }
        progressDialog.show();
    }

    /** 隐藏进度对话框 */
    protected final void hideProgressDialog() {
        if (progressDialog != null) progressDialog.dismiss();
    }

    /** 异步加载图片资源 */
    protected final void loadThumbnail(ImageView view, String path) {
        new Thread(()->{
            final Bitmap bmp = ImageUtils.createThumbnailBitmap(path);
            AndroidConstant.HANDLER.post(()->view.setImageBitmap(bmp));
        }).start();
    }
}
