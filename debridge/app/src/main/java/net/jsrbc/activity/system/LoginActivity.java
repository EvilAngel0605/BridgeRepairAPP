package net.jsrbc.activity.system;

import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import net.jsrbc.R;
import net.jsrbc.client.BaseDataClient;
import net.jsrbc.client.LoginClient;
import net.jsrbc.constant.AndroidConstant;
import net.jsrbc.frame.annotation.client.HttpClient;
import net.jsrbc.frame.annotation.database.Mapper;
import net.jsrbc.frame.annotation.element.AndroidActivity;
import net.jsrbc.frame.annotation.element.AndroidView;
import net.jsrbc.frame.annotation.event.OnClick;
import net.jsrbc.frame.base.BaseActivity;
import net.jsrbc.frame.exception.NonAuthoritativeException;
import net.jsrbc.mapper.BaseDataMapper;
import net.jsrbc.pojo.Result;
import net.jsrbc.pojo.User;
import net.jsrbc.utils.DataUtils;
import net.jsrbc.utils.StringUtils;
import net.jsrbc.utils.SystemUtils;

import java.io.IOException;

/**
 * 登录界面
 */
@AndroidActivity(R.layout.activity_login)
public class LoginActivity extends BaseActivity
        implements View.OnClickListener{

    @AndroidView(R.id.login_btn)
    @OnClick
    private Button loginBtn;

    @AndroidView(R.id.account)
    private AutoCompleteTextView accountInput;

    @AndroidView(R.id.password)
    private EditText passwordInput;

    /** 登录进度条 */
    private AlertDialog loginDialog;

    @HttpClient
    private LoginClient loginClient;

    @HttpClient
    private BaseDataClient baseDataClient;

    @Mapper
    private BaseDataMapper baseDataMapper;

    @Override
    protected void created() {
        loginDialog = new AlertDialog.Builder(this)
                .setView(R.layout.dialog_login_progress)
                .setCancelable(false)
                .create();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn:{
                attemptLogin();
                break;
            }
        }
    }

    /** 尝试登录 */
    private void attemptLogin() {
        if (validate()) {
            //隐藏键盘、显示登录对话框
            SystemUtils.hideSoftInputFromWindow(passwordInput, this);
            //判断是否连接网络，未连接则直接退出
            if(!SystemUtils.isNetworkConnect(this)) {
                SystemUtils.prompt(this, "网络未连接，请检查网络设置");
                return;
            }
            //开始登陆，并下载基础数据
            loginDialog.show();
            new Thread(()->{
                try {
                    User user = new User(accountInput.getText().toString(), passwordInput.getText().toString());
                    Result result = loginClient.login(user);
                    if (result.getStatus() == Result.Status.SUCCESS) {
                        DataUtils.put(LoginActivity.this, AndroidConstant.TOKEN, result.getContent());
                        try {
                            SystemUtils.downloadBaseData(baseDataMapper, baseDataClient, this);
                        }catch (NonAuthoritativeException e) {
                            AndroidConstant.HANDLER.post(()->{
                                loginDialog.dismiss();
                                SystemUtils.sessionTimeout(this);
                            });
                            return;
                        }
                        SystemUtils.toMain(this);
                        AndroidConstant.HANDLER.post(()->loginDialog.dismiss());
                    } else {
                        AndroidConstant.HANDLER.post(()->loginDialog.dismiss());
                        AndroidConstant.HANDLER.post(()->SystemUtils.prompt(LoginActivity.this, "账号或密码错误，请重新登录"));
                    }
                }catch (IOException e) {
                    Log.e(getClass().getName(), "login error", e);
                    AndroidConstant.HANDLER.post(()->{
                        loginDialog.dismiss();
                        SystemUtils.prompt(LoginActivity.this, "网络异常，请检查网络是否连接");
                    });
                }
            }).start();
        }
    }

    /** 校验输入的内容 */
    private boolean validate() {
        User user = new User();
        user.setAccount(accountInput.getText().toString());
        user.setPassword(passwordInput.getText().toString());
        if (StringUtils.isEmpty(user.getAccount())) {
            accountInput.requestFocus();
            accountInput.setError("请输入账号");
            return false;
        }
        if (StringUtils.isEmpty(user.getPassword())) {
            passwordInput.requestFocus();
            passwordInput.setError("请输入密码");
            return false;
        }
        return true;
    }

    /** 返回键按下后返回桌面，不退出 */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            SystemUtils.toDesk(this);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}