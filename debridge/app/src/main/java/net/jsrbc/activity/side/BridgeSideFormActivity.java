package net.jsrbc.activity.side;

import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import net.jsrbc.R;
import net.jsrbc.constant.AndroidConstant;
import net.jsrbc.entity.TblBridge;
import net.jsrbc.entity.TblBridgeMember;
import net.jsrbc.entity.TblBridgeParts;
import net.jsrbc.entity.TblBridgeSide;
import net.jsrbc.entity.TblBridgeSite;
import net.jsrbc.entity.TblParamType;
import net.jsrbc.enumeration.OperationType;
import net.jsrbc.enumeration.ParamGroupField;
import net.jsrbc.enumeration.UploadStatus;
import net.jsrbc.frame.annotation.database.Mapper;
import net.jsrbc.frame.annotation.element.AndroidActivity;
import net.jsrbc.frame.annotation.element.AndroidMenu;
import net.jsrbc.frame.annotation.element.AndroidView;
import net.jsrbc.frame.annotation.event.OnClick;
import net.jsrbc.frame.base.BaseActivity;
import net.jsrbc.mapper.BaseDataMapper;
import net.jsrbc.mapper.BridgeMemberMapper;
import net.jsrbc.mapper.BridgePartsMapper;
import net.jsrbc.mapper.BridgeSideMapper;
import net.jsrbc.mapper.BridgeSiteMapper;
import net.jsrbc.utils.JsonUtils;
import net.jsrbc.utils.StringUtils;
import net.jsrbc.utils.SystemUtils;

import java.math.RoundingMode;
import java.util.List;

@AndroidActivity(R.layout.activity_bridge_side_form)
@AndroidMenu(R.menu.empty)
public class BridgeSideFormActivity extends BaseActivity
        implements View.OnClickListener {

    @AndroidView(R.id.tv_cancel)
    @OnClick
    private TextView cancelBtn;

    @AndroidView(R.id.tv_save)
    @OnClick
    private TextView saveBtn;

    @AndroidView(R.id.sp_side_type)
    private Spinner sideTypeSelector;

    @AndroidView(R.id.et_stake_no)
    private EditText stakeNoInput;

    @AndroidView(R.id.et_bridge_length)
    private EditText bridgeLengthInput;

    @AndroidView(R.id.et_deck_total_width)
    private EditText deckTotalWidthInput;

    @AndroidView(R.id.et_lane_width)
    private EditText laneWidthInput;

    @AndroidView(R.id.et_deck_evaluation)
    private EditText deckEvaluationInput;

    @AndroidView(R.id.et_sub_clear_height)
    private EditText subClearHeightInput;

    @AndroidView(R.id.et_super_clear_height)
    private EditText superClearHeightInput;

    @AndroidView(R.id.et_access_total_width)
    private EditText accessTotalWidthInput;

    @AndroidView(R.id.et_access_road_width)
    private EditText accessRoadWidthInput;

    @AndroidView(R.id.sp_access_linear)
    private Spinner accessLinearSelector;

    @Mapper
    private BaseDataMapper baseDataMapper;

    @Mapper
    private BridgeSideMapper bridgeSideMapper;

    @Mapper
    private BridgeSiteMapper bridgeSiteMapper;

    @Mapper
    private BridgePartsMapper bridgePartsMapper;

    @Mapper
    private BridgeMemberMapper bridgeMemberMapper;

    private OperationType operationType;

    private TblBridge currentBridge;

    private TblBridgeSide currentSide;

    private List<TblParamType> sideTypeList;

    private List<TblParamType> accessLinearList;

    @Override
    protected void created() {
        Intent intent = getIntent();
        currentBridge = (TblBridge) intent.getSerializableExtra(BridgeSideActivity.CURRENT_BRIDGE);
        currentSide = (TblBridgeSide) intent.getSerializableExtra(BridgeSideActivity.CURRENT_SIDE);
        operationType = (OperationType) intent.getSerializableExtra(BridgeSideActivity.OPERATION_TYPE);
        init();
    }

    /** 视图初始化 */
    private void init() {
        sideTypeList = baseDataMapper.getParamTypeListByGroupField(ParamGroupField.SIDE_TYPE);
        accessLinearList = baseDataMapper.getParamTypeListByGroupField(ParamGroupField.ACCESS_LINEAR);
        accessLinearList.add(0, TblParamType.empty());
        setSelector(sideTypeSelector, sideTypeList);
        setSelector(accessLinearSelector, accessLinearList);
        if (operationType == OperationType.EDIT || operationType == OperationType.COPY) setDefaultValue();
    }

    /** 设置各控件的默认值 */
    private void setDefaultValue() {
        if (currentSide == null) return;
        if (operationType == OperationType.EDIT) {
            int sideTypeIndex = sideTypeList.indexOf(sideTypeList.stream().filter(s -> currentSide.getSideTypeId().equals(s.getId())).findFirst().orElse(null));
            sideTypeSelector.setSelection(sideTypeIndex);
        }
        int accessLinearIndex = accessLinearList.indexOf(accessLinearList.stream().filter(a->currentSide.getAccessLinearId().equals(a.getId())).findFirst().orElse(null));
        accessLinearSelector.setSelection(accessLinearIndex);
        stakeNoInput.setText(String.valueOf(currentSide.getStakeNo()));
        bridgeLengthInput.setText(StringUtils.sizeToStr(currentSide.getBridgeLength(), RoundingMode.HALF_UP));
        deckTotalWidthInput.setText(StringUtils.sizeToStr(currentSide.getDeckTotalWidth(), RoundingMode.HALF_UP));
        laneWidthInput.setText(StringUtils.sizeToStr(currentSide.getLaneWidth(), RoundingMode.HALF_UP));
        deckEvaluationInput.setText(StringUtils.sizeToStr(currentSide.getDeckEvaluation(), RoundingMode.HALF_UP));
        subClearHeightInput.setText(StringUtils.sizeToStr(currentSide.getSubClearHeight(), RoundingMode.HALF_UP));
        superClearHeightInput.setText(StringUtils.sizeToStr(currentSide.getSuperClearHeight(), RoundingMode.HALF_UP));
        accessTotalWidthInput.setText(StringUtils.sizeToStr(currentSide.getAccessTotalWidth(), RoundingMode.HALF_UP));
        accessRoadWidthInput.setText(StringUtils.sizeToStr(currentSide.getAccessRoadWidth(), RoundingMode.HALF_UP));
    }

    /** 设置选择框 */
    private <T> void setSelector(Spinner selector, List<T> data) {
        if (data == null) return;
        ArrayAdapter<T> adapter = new ArrayAdapter<>(this, R.layout.item_spinner, data);
        selector.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.tv_save:
                saveBridgeSide();
                break;
        }
    }

    /** 更新桥梁分幅对象 */
    private void saveBridgeSide() {
        if (validate()) {
            TblBridgeSide.Builder builder = new TblBridgeSide.Builder();
            if (currentSide != null) {
                if (!StringUtils.isEmpty(currentSide.getId())) builder.id(currentSide.getId());
                else builder.id(StringUtils.createId());
                builder.bridgeSpanCombination(currentSide.getBridgeSpanCombination());
            } else {
                builder.id(StringUtils.createId());
            }
            builder.bridgeId(currentBridge.getId())
                    .bridgeName(currentBridge.getName())
                    .sideTypeId(((TblParamType) sideTypeSelector.getSelectedItem()).getId())
                    .sideTypeName(((TblParamType) sideTypeSelector.getSelectedItem()).getName())
                    .sideTypeSortNo(((TblParamType) sideTypeSelector.getSelectedItem()).getSortNo())
                    .stakeNo(StringUtils.toFloat(stakeNoInput.getText().toString()))
                    .bridgeLength(StringUtils.toFloat(bridgeLengthInput.getText().toString()) * 1000)
                    .deckTotalWidth(StringUtils.toFloat(deckTotalWidthInput.getText().toString()) * 1000)
                    .laneWidth(StringUtils.toFloat(laneWidthInput.getText().toString()) * 1000)
                    .deckEvaluation(StringUtils.toFloat(deckEvaluationInput.getText().toString()) * 1000)
                    .subClearHeight(StringUtils.toFloat(subClearHeightInput.getText().toString()) * 1000)
                    .superClearHeight(StringUtils.toFloat(superClearHeightInput.getText().toString()) * 1000)
                    .accessTotalWidth(StringUtils.toFloat(accessTotalWidthInput.getText().toString()) * 1000)
                    .accessRoadWidth(StringUtils.toFloat(accessRoadWidthInput.getText().toString()) * 1000)
                    .accessLinearId(((TblParamType) accessLinearSelector.getSelectedItem()).getId())
                    .accessLinearName(((TblParamType) accessLinearSelector.getSelectedItem()).getName())
                    .taskId(currentBridge.getTaskId())
                    .upload(UploadStatus.NEED_UPLOAD.getCode());
            TblBridgeSide bridgeSide = builder.build();
            //保存
            showProgressDialog();
            new Thread(()->{
                bridgeSideMapper.addOrReplaceBridgeSide(bridgeSide);
                //如果是复制模式，则复制构件
                if (operationType == OperationType.COPY) copyPartsMembers(bridgeSide);
                //退出表单界面
                AndroidConstant.HANDLER.post(()->{
                    hideProgressDialog();
                    finish();
                });
            }).start();
        }
    }

    /** 复制部构件 */
    private void copyPartsMembers(TblBridgeSide bridgeSide) {
        //复制孔跨
        List<TblBridgeSite> bridgeSiteList = bridgeSiteMapper.getBridgeSiteListBySide(currentSide.getBridgeId(), currentSide.getSideTypeId());
        bridgeSiteList.forEach(s->{
            s.setId(StringUtils.createId());
            s.setSideTypeId(bridgeSide.getSideTypeId());
            s.setUpload(UploadStatus.NEED_UPLOAD.getCode());
        });
        bridgeSiteMapper.addOrReplaceBridgeSiteList(bridgeSiteList);
        //复制部件
        List<TblBridgeParts> bridgePartsList = bridgePartsMapper.getBridgePartsListBySide(currentSide.getBridgeId(), currentSide.getSideTypeId());
        bridgePartsList.forEach(p->{
            p.setId(StringUtils.createId());
            p.setSideTypeId(bridgeSide.getSideTypeId());
            p.setUpload(UploadStatus.NEED_UPLOAD.getCode());
        });
        bridgePartsMapper.addOrReplaceBridgePartsList(bridgePartsList);
        //生成构件
        List<TblBridgeMember> bridgeMemberList = bridgeMemberMapper.getBridgeMemberListBySide(currentSide.getBridgeId(), currentSide.getSideTypeId());
        bridgeMemberList.forEach(m->{
            m.setId(StringUtils.createId());
            m.setSideTypeId(bridgeSide.getSideTypeId());
        });
        bridgeMemberMapper.addOrReplaceBridgeMemberList(bridgeMemberList);
    }

    /** 校验输入的内容 */
    private boolean validate() {
        String sideTypeId = ((TblParamType)sideTypeSelector.getSelectedItem()).getId();
        if (operationType == OperationType.EDIT) {
            if (currentSide != null && !currentSide.getSideTypeId().equals(sideTypeId) && bridgeSideMapper.getBridgeSideBySideType(currentBridge.getId(), sideTypeId) != null) {
                SystemUtils.prompt(this, "修改的分幅类型已经存在，请选择其他分幅");
                return false;
            }
        } else if (bridgeSideMapper.getBridgeSideBySideType(currentBridge.getId(), sideTypeId) != null) {
            SystemUtils.prompt(this, "新增的分幅类型已经存在，请选择其他分幅");
            return false;
        }
        if (StringUtils.isEmpty(stakeNoInput.getText().toString())) {
            stakeNoInput.requestFocus();
            stakeNoInput.setError("桩号不能为空");
            return false;
        }
        return true;
    }
}
