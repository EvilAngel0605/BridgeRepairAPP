package net.jsrbc.activity.system;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.jsrbc.R;
import net.jsrbc.constant.AndroidConstant;
import net.jsrbc.enumeration.InspectionDirection;
import net.jsrbc.frame.annotation.element.AndroidActivity;
import net.jsrbc.frame.annotation.element.AndroidMenu;
import net.jsrbc.frame.annotation.element.AndroidView;
import net.jsrbc.frame.annotation.event.OnClick;
import net.jsrbc.frame.base.BaseActivity;
import net.jsrbc.utils.DataUtils;
import net.jsrbc.utils.SystemUtils;

import java.util.Arrays;
import java.util.List;

@AndroidActivity(R.layout.activity_setting)
@AndroidMenu(R.menu.empty)
public class SettingActivity extends BaseActivity
        implements View.OnClickListener{

    @AndroidView(R.id.ll_direction_setting_wrapper)
    @OnClick
    private LinearLayout directionSettingWrapper;

    @AndroidView(R.id.ll_logout_wrapper)
    @OnClick
    private LinearLayout logoutWrapper;

    @AndroidView(R.id.tv_direction_setting)
    private TextView directionSettingView;

    private List<InspectionDirection> inspectionDirectionList;

    @Override
    protected void created() {
        setDefaultToolbar();
        init();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_direction_setting_wrapper: {
                SystemUtils.choose(this, inspectionDirectionList, (d, i)->{
                    InspectionDirection selectedDirection = inspectionDirectionList.get(i);
                    DataUtils.put(this, AndroidConstant.INSPECTION_DIRECTION, selectedDirection);
                    directionSettingView.setText(selectedDirection.getName());
                });
                break;
            }
            case R.id.ll_logout_wrapper: {
                SystemUtils.toLogin(this);
                DataUtils.remove(this, AndroidConstant.TOKEN);
                break;
            }
        }
    }

    /** 初始化各视图 */
    private void init() {
        //初始化数据
        inspectionDirectionList = Arrays.asList(InspectionDirection.values());
        //设置视图
        InspectionDirection currentInspectionDirection = DataUtils.get(this, AndroidConstant.INSPECTION_DIRECTION, InspectionDirection.class);
        if (currentInspectionDirection != null) directionSettingView.setText(currentInspectionDirection.getName());
        else directionSettingView.setText(InspectionDirection.LEFT_TO_RIGHT.getName());
    }
}
