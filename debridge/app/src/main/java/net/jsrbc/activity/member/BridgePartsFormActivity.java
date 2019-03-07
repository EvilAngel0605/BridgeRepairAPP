package net.jsrbc.activity.member;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import net.jsrbc.R;
import net.jsrbc.constant.AndroidConstant;
import net.jsrbc.entity.TblBridgeParts;
import net.jsrbc.entity.TblParamStructureType;
import net.jsrbc.entity.TblParamType;
import net.jsrbc.enumeration.ActivityResultCode;
import net.jsrbc.enumeration.MemberDescParam;
import net.jsrbc.enumeration.ParamGroupField;
import net.jsrbc.enumeration.PartsType;
import net.jsrbc.enumeration.PositionType;
import net.jsrbc.enumeration.SpecialSection;
import net.jsrbc.enumeration.StakeSide;
import net.jsrbc.enumeration.StructureGroupField;
import net.jsrbc.frame.annotation.database.Mapper;
import net.jsrbc.frame.annotation.element.AndroidActivity;
import net.jsrbc.frame.annotation.element.AndroidMenu;
import net.jsrbc.frame.annotation.element.AndroidView;
import net.jsrbc.frame.base.BaseActivity;
import net.jsrbc.mapper.BaseDataMapper;
import net.jsrbc.mapper.BridgeMemberMapper;
import net.jsrbc.mapper.BridgePartsMapper;
import net.jsrbc.utils.MemberUtils;
import net.jsrbc.utils.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@AndroidActivity(R.layout.activity_bridge_parts_form)
@AndroidMenu(R.menu.save)
public class BridgePartsFormActivity extends BaseActivity {

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
    private CheckBox closureSectionCheck;

    @AndroidView(R.id.cb_side_section)
    private CheckBox sideSectionCheck;

    @AndroidView(R.id.ll_position)
    private LinearLayout positionWrapper;

    @AndroidView(R.id.cb_position_left)
    private CheckBox positionLeftCheck;

    @AndroidView(R.id.cb_position_right)
    private CheckBox positionRightCheck;

    @AndroidView(R.id.ll_expansion_joint)
    private LinearLayout expansionJointWrapper;

    @AndroidView(R.id.sp_expansion_joint_type)
    private Spinner expansionJointSelector;

    @AndroidView(R.id.tv_empty)
    private TextView emptyView;

    @Mapper
    private BaseDataMapper baseDataMapper;

    @Mapper
    private BridgePartsMapper bridgePartsMapper;

    @Mapper
    private BridgeMemberMapper bridgeMemberMapper;

    private TblBridgeParts currentBridgeParts;

    private List<TblParamStructureType> supportTypeList;

    private List<TblParamType> materialTypeList;

    private List<TblParamStructureType> expansionJointTypeList;

    @Override
    protected void created() {
        currentBridgeParts = (TblBridgeParts)getIntent().getSerializableExtra(BridgePartsActivity.CURRENT_PARTS);
        setDefaultToolbar();
        setSupportTypeSelector();
        setMaterialTypeSelector();
        setStakeSideSelector();
        setExpansionJointTypeSelector();
        showFormItem();
        setDefaultValue();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_save:
                save();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /** 保存结果 */
    private void save() {
        showProgressDialog();
        new Thread(()->{
            if (currentBridgeParts.getPositionTypeName().equals(PositionType.SUPER.getValue())) {
                if (currentBridgeParts.getPartsTypeName().equals(PartsType.SUPPORT.getValue())) currentBridgeParts.setStructureTypeId(((TblParamStructureType)supportTypeSelector.getSelectedItem()).getId());
                else currentBridgeParts.setMaterialTypeId(((TblParamType)materialTypeSelector.getSelectedItem()).getId());
                if (MemberUtils.containDescParam(currentBridgeParts, MemberDescParam.VERTICAL_NO)) currentBridgeParts.setVerticalCount(StringUtils.toInt(verticalCountInput.getText().toString()));
                if (MemberUtils.containDescParam(currentBridgeParts, MemberDescParam.HORIZONTAL_NO)) currentBridgeParts.setHorizontalCount(StringUtils.toInt(horizontalCountInput.getText().toString()));
                if (MemberUtils.containDescParam(currentBridgeParts, MemberDescParam.STAKE_SIDE) || MemberUtils.containDescParam(currentBridgeParts, MemberDescParam.PIER_NO)) currentBridgeParts.setStakeSide(((StakeSide)stakeSideSelector.getSelectedItem()).getCode());
                if (MemberUtils.containDescParam(currentBridgeParts, MemberDescParam.SPECIAL_SECTION)) {
                    StringBuilder sb = new StringBuilder();
                    if (closureSectionCheck.isChecked()) sb.append(SpecialSection.CLOSURE_SECTION.getValue()).append(",");
                    if (sideSectionCheck.isChecked()) sb.append(SpecialSection.SIDE_SECTION.getValue()).append(",");
                    if (sb.length() > 0) sb.deleteCharAt(sb.length()-1);
                    currentBridgeParts.setSpecialSection(sb.toString());
                }
            }else {
                if (MemberUtils.containDescParam(currentBridgeParts, MemberDescParam.HORIZONTAL_NO)) currentBridgeParts.setHorizontalCount(StringUtils.toInt(horizontalCountInput.getText().toString()));
                if (needMaterialType(currentBridgeParts.getPartsTypeName())) currentBridgeParts.setMaterialTypeId(((TblParamType)materialTypeSelector.getSelectedItem()).getId());
                if (MemberUtils.containDescParam(currentBridgeParts, MemberDescParam.SPECIAL_SECTION)) {
                    StringBuilder sb = new StringBuilder();
                    if (positionLeftCheck.isChecked()) sb.append(SpecialSection.POSITION_LEFT.getValue()).append(",");
                    if (positionRightCheck.isChecked()) sb.append(SpecialSection.POSITION_RIGHT.getValue()).append(",");
                    if (sb.length() > 0) sb.deleteCharAt(sb.length()-1);
                    currentBridgeParts.setSpecialSection(sb.toString());
                }
                if (currentBridgeParts.getPartsTypeName().equals(PartsType.EXPANSION_JOINT.getValue())) currentBridgeParts.setStructureTypeId(((TblParamStructureType)supportTypeSelector.getSelectedItem()).getId());
            }
            bridgeMemberMapper.deleteBridgeMemberByParts(currentBridgeParts);
            bridgeMemberMapper.addOrReplaceBridgeMemberList(MemberUtils.generateBridgeMembers(currentBridgeParts));
            bridgePartsMapper.updateBridgeParts(currentBridgeParts);
            //返回修改的内容
            AndroidConstant.HANDLER.post(()->{
                hideProgressDialog();
                Intent intent = new Intent();
                intent.putExtra(BridgePartsActivity.CURRENT_PARTS, currentBridgeParts);
                setResult(ActivityResultCode.SUCCESS.getCode(), intent);
                finish();
            });
        }).start();
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

    /** 设置伸缩缝类型 */
    private void setExpansionJointTypeSelector() {
        expansionJointTypeList = baseDataMapper.getStructureTypeByGroupField(StructureGroupField.EXPANSION_JOINT_TYPE);
        expansionJointTypeList.add(0, TblParamStructureType.empty());
        ArrayAdapter<TblParamStructureType> adapter = new ArrayAdapter<>(this, R.layout.item_spinner, expansionJointTypeList);
        expansionJointSelector.setAdapter(adapter);
    }

    /** 显示表单项 */
    private void showFormItem() {
        if (currentBridgeParts.getPositionTypeName().equals(PositionType.SUPER.getValue())) {
            if (currentBridgeParts.getPartsTypeName().equals(PartsType.SUPPORT.getValue())) supportTypeWrapper.setVisibility(View.VISIBLE);
            else materialTypeWrapper.setVisibility(View.VISIBLE);
            if (MemberUtils.containDescParam(currentBridgeParts, MemberDescParam.VERTICAL_NO)) verticalCountWrapper.setVisibility(View.VISIBLE);
            if (MemberUtils.containDescParam(currentBridgeParts, MemberDescParam.HORIZONTAL_NO)) horizontalCountWrapper.setVisibility(View.VISIBLE);
            if (MemberUtils.containDescParam(currentBridgeParts, MemberDescParam.STAKE_SIDE) || MemberUtils.containDescParam(currentBridgeParts, MemberDescParam.PIER_NO)) stakeSideWrapper.setVisibility(View.VISIBLE);
            if (MemberUtils.containDescParam(currentBridgeParts, MemberDescParam.SPECIAL_SECTION)) specialSectionWrapper.setVisibility(View.VISIBLE);
        }else {
            if (MemberUtils.containDescParam(currentBridgeParts, MemberDescParam.HORIZONTAL_NO)) horizontalCountWrapper.setVisibility(View.VISIBLE);
            if (needMaterialType(currentBridgeParts.getPartsTypeName())) materialTypeWrapper.setVisibility(View.VISIBLE);
            if (MemberUtils.containDescParam(currentBridgeParts, MemberDescParam.SPECIAL_SECTION)) positionWrapper.setVisibility(View.VISIBLE);
            if (currentBridgeParts.getPartsTypeName().equals(PartsType.EXPANSION_JOINT.getValue())) expansionJointWrapper.setVisibility(View.VISIBLE);
        }
        if (isNotEditable()) emptyView.setVisibility(View.VISIBLE);
    }

    /** 设置默认值 */
    private void setDefaultValue() {
        if (!StringUtils.isEmpty(currentBridgeParts.getMaterialTypeId())) {
            materialTypeSelector.setSelection(materialTypeList.stream().map(TblParamType::getId).collect(Collectors.toList()).indexOf(currentBridgeParts.getMaterialTypeId()));
        }
        supportTypeSelector.setSelection(supportTypeList.stream().map(TblParamStructureType::getId).collect(Collectors.toList()).indexOf(currentBridgeParts.getStructureTypeId()));
        expansionJointSelector.setSelection(expansionJointTypeList.stream().map(TblParamStructureType::getId).collect(Collectors.toList()).indexOf(currentBridgeParts.getStructureTypeId()));
        if (currentBridgeParts.getVerticalCount() != 0) verticalCountInput.setText(String.valueOf(currentBridgeParts.getVerticalCount()));
        if (currentBridgeParts.getHorizontalCount() != 0) horizontalCountInput.setText(String.valueOf(currentBridgeParts.getHorizontalCount()));
        if (currentBridgeParts.getStakeSide() != 0) stakeSideSelector.setSelection(currentBridgeParts.getStakeSide());
        if (currentBridgeParts.getSpecialSection().contains(SpecialSection.CLOSURE_SECTION.getValue())) closureSectionCheck.setChecked(true);
        if (currentBridgeParts.getSpecialSection().contains(SpecialSection.SIDE_SECTION.getValue())) sideSectionCheck.setChecked(true);
        if (currentBridgeParts.getSpecialSection().contains(SpecialSection.POSITION_LEFT.getValue())) positionLeftCheck.setChecked(true);
        if (currentBridgeParts.getSpecialSection().contains(SpecialSection.POSITION_RIGHT.getValue())) positionRightCheck.setChecked(true);
    }

    /** 是否可编辑 */
    private boolean isNotEditable() {
        return specialSectionWrapper.getVisibility() == View.GONE &&
                expansionJointWrapper.getVisibility() == View.GONE &&
                positionWrapper.getVisibility() == View.GONE &&
                stakeSideWrapper.getVisibility() == View.GONE &&
                materialTypeWrapper.getVisibility() == View.GONE &&
                horizontalCountWrapper.getVisibility() == View.GONE &&
                verticalCountWrapper.getVisibility() == View.GONE &&
                supportTypeWrapper.getVisibility() == View.GONE;
    }

    /** 需要材料类型的部件，单指下部结构及桥面系部件 */
    private boolean needMaterialType(String partsTypeName) {
        return Arrays.asList(PartsType.PIER.getValue(),
                PartsType.ABUTMENT.getValue(),
                PartsType.BASE.getValue(),
                PartsType.WING_EAR_WALL.getValue(),
                PartsType.CONICAL_PROTECTION_SLOP.getValue(),
                PartsType.DECK_PAVEMENT.getValue()).contains(partsTypeName);
    }
}
