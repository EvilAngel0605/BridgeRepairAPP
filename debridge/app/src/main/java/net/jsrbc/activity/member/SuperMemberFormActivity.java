package net.jsrbc.activity.member;

import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import net.jsrbc.R;
import net.jsrbc.entity.TblBridgeParts;
import net.jsrbc.entity.TblParamStructureType;
import net.jsrbc.entity.TblParamType;
import net.jsrbc.enumeration.ActivityResultCode;
import net.jsrbc.enumeration.MemberDescParam;
import net.jsrbc.enumeration.ParamGroupField;
import net.jsrbc.enumeration.PartsType;
import net.jsrbc.enumeration.SpecialSection;
import net.jsrbc.enumeration.StakeSide;
import net.jsrbc.enumeration.StructureGroupField;
import net.jsrbc.frame.annotation.database.Mapper;
import net.jsrbc.frame.annotation.element.AndroidActivity;
import net.jsrbc.frame.annotation.element.AndroidView;
import net.jsrbc.frame.annotation.event.OnClick;
import net.jsrbc.frame.base.BaseActivity;
import net.jsrbc.mapper.BaseDataMapper;
import net.jsrbc.utils.JsonUtils;
import net.jsrbc.utils.MemberUtils;
import net.jsrbc.utils.StringUtils;
import net.jsrbc.utils.SystemUtils;

import java.util.List;
import java.util.stream.Collectors;

@AndroidActivity(R.layout.activity_super_member_form)
public class SuperMemberFormActivity extends BaseActivity
        implements View.OnClickListener{

    @AndroidView(R.id.ll_support_type)
    private LinearLayout supportTypeWrapper;

    @AndroidView(R.id.sp_support_type)
    private Spinner supportTypeSelector;

    @AndroidView(R.id.ll_material_type)
    private LinearLayout materialTypeWrapper;

    @AndroidView(R.id.sp_material_type)
    private Spinner materialTypeSelector;

    @AndroidView(R.id.ll_vertical_count)
    private LinearLayout verticalCountWrapper;

    @AndroidView(R.id.et_vertical_count)
    private EditText verticalCountInput;

    @AndroidView(R.id.ll_horizontal_count)
    private LinearLayout horizontalCountWrapper;

    @AndroidView(R.id.et_horizontal_count)
    private EditText horizontalCountInput;

    @AndroidView(R.id.ll_stake_side)
    private LinearLayout stakeSideWrapper;

    @AndroidView(R.id.sp_stake_side)
    private Spinner stakeSideSelector;

    @AndroidView(R.id.ll_special_section)
    private LinearLayout specialSectionWrapper;

    @AndroidView(R.id.cb_closure_section)
    private CheckBox closureSection;

    @AndroidView(R.id.cb_side_section)
    private CheckBox sideSection;

    @AndroidView(R.id.tv_cancel)
    @OnClick
    private TextView cancelBtn;

    @AndroidView(R.id.tv_save)
    @OnClick
    private TextView saveBtn;

    @Mapper
    private BaseDataMapper baseDataMapper;

    private TblBridgeParts currentBridgeParts;

    private List<TblParamStructureType> supportTypeList;

    private List<TblParamType> materialTypeList;

    @Override
    protected void created() {
        initView();
        setSupportTypeSelector();
        setMaterialTypeSelector();
        setStakeSideSelector();
        setDefaultValue();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SystemUtils.hideSoftInputFromWindow(saveBtn, this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel: {
                finish();
                break;
            }
            case R.id.tv_save: {
                //保存
                save();
                break;
            }
        }
    }

    /** 保存输入的内容 */
    private void save() {
        if (validate()) {
            TblParamStructureType supportType = (TblParamStructureType)supportTypeSelector.getSelectedItem();
            TblParamType materialType = (TblParamType)materialTypeSelector.getSelectedItem();
            int verticalCount = StringUtils.toInt(verticalCountInput.getText().toString());
            int horizontalCount = StringUtils.toInt(horizontalCountInput.getText().toString());
            int stakeSide = ((StakeSide)stakeSideSelector.getSelectedItem()).getCode();
            StringBuilder specialSectionBuilder = new StringBuilder();
            if (closureSection.isChecked()) specialSectionBuilder.append(SpecialSection.CLOSURE_SECTION.getValue()).append(",");
            if (sideSection.isChecked()) specialSectionBuilder.append(SpecialSection.SIDE_SECTION.getValue()).append(",");
            if (specialSectionBuilder.length() > 0) specialSectionBuilder.deleteCharAt(specialSectionBuilder.length()-1);
            //注入给对对象
            currentBridgeParts.setStructureTypeId(supportType.getId());
            currentBridgeParts.setStructureTypeName(supportType.getName());
            currentBridgeParts.setMaterialTypeId(materialType.getId());
            currentBridgeParts.setMaterialTypeName(materialType.getName());
            currentBridgeParts.setVerticalCount(verticalCount);
            currentBridgeParts.setHorizontalCount(horizontalCount);
            currentBridgeParts.setStakeSide(stakeSide);
            currentBridgeParts.setSpecialSection(specialSectionBuilder.toString());
            //返回结果
            Intent intent = new Intent();
            intent.putExtra(SuperstructureFormActivity.CURRENT_BRIDGE_PARTS, currentBridgeParts);
            setResult(ActivityResultCode.SUCCESS.getCode(), intent);
            finish();
        }
    }

    /** 初始化控件 */
    private void initView() {
        currentBridgeParts = JsonUtils.fromJson(getIntent().getStringExtra(SuperstructureFormActivity.CURRENT_BRIDGE_PARTS), TblBridgeParts.class);
        if (isSupport()) supportTypeWrapper.setVisibility(View.VISIBLE);
        else materialTypeWrapper.setVisibility(View.VISIBLE);
        if (MemberUtils.containDescParam(currentBridgeParts, MemberDescParam.VERTICAL_NO)) verticalCountWrapper.setVisibility(View.VISIBLE);
        if (MemberUtils.containDescParam(currentBridgeParts, MemberDescParam.HORIZONTAL_NO)) horizontalCountWrapper.setVisibility(View.VISIBLE);
        if (MemberUtils.containDescParam(currentBridgeParts, MemberDescParam.STAKE_SIDE) || MemberUtils.containDescParam(currentBridgeParts, MemberDescParam.PIER_NO)) stakeSideWrapper.setVisibility(View.VISIBLE);
        if (MemberUtils.containDescParam(currentBridgeParts, MemberDescParam.SPECIAL_SECTION)) specialSectionWrapper.setVisibility(View.VISIBLE);
    }

    /** 设置支座的类型选择器 */
    private void setSupportTypeSelector() {
        supportTypeList = baseDataMapper.getStructureTypeByGroupField(StructureGroupField.SUPPORT_TYPE);
        supportTypeList.add(0, TblParamStructureType.empty());
        ArrayAdapter<TblParamStructureType> adapter = new ArrayAdapter<>(this, R.layout.item_spinner, supportTypeList);
        supportTypeSelector.setAdapter(adapter);
    }

    /** 设置材料类型选择器 */
    private void setMaterialTypeSelector() {
        materialTypeList = baseDataMapper.getParamTypeListByGroupField(ParamGroupField.MATERIAL_TYPE);
        materialTypeList.add(0, TblParamType.empty());
        ArrayAdapter<TblParamType> adapter = new ArrayAdapter<>(this, R.layout.item_spinner, materialTypeList);
        materialTypeSelector.setAdapter(adapter);
    }

    /** 设置大小桩号侧 */
    private void setStakeSideSelector() {
        ArrayAdapter<StakeSide> adapter = new ArrayAdapter<>(this, R.layout.item_spinner, StakeSide.values());
        stakeSideSelector.setAdapter(adapter);
    }

    /** 设置初始值 */
    private void setDefaultValue() {
        if (!StringUtils.isEmpty(currentBridgeParts.getStructureTypeId())) {
            supportTypeSelector.setSelection(supportTypeList.stream().map(TblParamStructureType::getId).collect(Collectors.toList()).indexOf(currentBridgeParts.getStructureTypeId()));
        }
        if (!StringUtils.isEmpty(currentBridgeParts.getMaterialTypeId())) {
            materialTypeSelector.setSelection(materialTypeList.stream().map(TblParamType::getId).collect(Collectors.toList()).indexOf(currentBridgeParts.getMaterialTypeId()));
        }
        if (currentBridgeParts.getVerticalCount() != 0) verticalCountInput.setText(String.valueOf(currentBridgeParts.getVerticalCount()));
        if (currentBridgeParts.getHorizontalCount() != 0) horizontalCountInput.setText(String.valueOf(currentBridgeParts.getHorizontalCount()));
        if (currentBridgeParts.getStakeSide() != 0) stakeSideSelector.setSelection(currentBridgeParts.getStakeSide());
        if (currentBridgeParts.getSpecialSection().contains(SpecialSection.CLOSURE_SECTION.getValue())) closureSection.setChecked(true);
        if (currentBridgeParts.getSpecialSection().contains(SpecialSection.SIDE_SECTION.getValue())) sideSection.setChecked(true);
    }

    /** 是否为支座 */
    private boolean isSupport() {
        return PartsType.SUPPORT.getValue().equals(currentBridgeParts.getPartsTypeName());
    }

    /** 判断材料类型是否未选 */
    private boolean validate() {
        if (!isSupport() && materialTypeSelector.getSelectedItem() == TblParamType.empty()) {
            SystemUtils.prompt(this, "请选择材料类型");
            return false;
        } else if (isSupport() && supportTypeSelector.getSelectedItem() == TblParamStructureType.empty()) {
            SystemUtils.prompt(this, "请选择支座类型");
            return false;
        }
        return true;
    }
}
