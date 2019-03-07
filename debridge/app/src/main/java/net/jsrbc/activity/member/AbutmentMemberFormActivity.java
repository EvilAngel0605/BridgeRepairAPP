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
import net.jsrbc.entity.TblParamType;
import net.jsrbc.enumeration.ActivityResultCode;
import net.jsrbc.enumeration.MemberType;
import net.jsrbc.enumeration.ParamGroupField;
import net.jsrbc.enumeration.PartsType;
import net.jsrbc.enumeration.SpecialSection;
import net.jsrbc.frame.annotation.database.Mapper;
import net.jsrbc.frame.annotation.element.AndroidActivity;
import net.jsrbc.frame.annotation.element.AndroidView;
import net.jsrbc.frame.annotation.event.OnClick;
import net.jsrbc.frame.base.BaseActivity;
import net.jsrbc.mapper.BaseDataMapper;
import net.jsrbc.utils.StringUtils;
import net.jsrbc.utils.SystemUtils;

import java.util.List;
import java.util.stream.Collectors;

@AndroidActivity(R.layout.activity_abutment_member_form)
public class AbutmentMemberFormActivity extends BaseActivity
        implements View.OnClickListener{

    @AndroidView(R.id.ll_material_type)
    private LinearLayout materialTypeWrapper;

    @AndroidView(R.id.sp_material_type)
    private Spinner materialTypeSelector;

    @AndroidView(R.id.ll_horizontal_count)
    private LinearLayout horizontalCountWrapper;

    @AndroidView(R.id.et_horizontal_count)
    private EditText horizontalCountInput;

    @AndroidView(R.id.ll_position)
    private LinearLayout positionWrapper;

    @AndroidView(R.id.cb_position_left)
    private CheckBox positionLeftCheck;

    @AndroidView(R.id.cb_position_right)
    private CheckBox positionRightCheck;

    @AndroidView(R.id.ll_exists)
    private LinearLayout existsWrapper;

    @AndroidView(R.id.cb_exists)
    private CheckBox existsCheck;

    @AndroidView(R.id.tv_cancel)
    @OnClick
    private TextView cancelBtn;

    @AndroidView(R.id.tv_save)
    @OnClick
    private TextView saveBtn;

    @Mapper
    private BaseDataMapper baseDataMapper;

    private TblBridgeParts currentBridgeParts;

    private List<TblParamType> materialTypeList;

    @Override
    protected void created() {
        initView();
        setMaterialTypeSelector();
        setDefaultValue();
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
            TblParamType materialType = (TblParamType)materialTypeSelector.getSelectedItem();
            int horizontalCount = StringUtils.toInt(horizontalCountInput.getText().toString());
            StringBuilder specialSectionBuilder = new StringBuilder();
            if (positionLeftCheck.isChecked()) specialSectionBuilder.append(SpecialSection.POSITION_LEFT.getValue()).append(",");
            if (positionRightCheck.isChecked()) specialSectionBuilder.append(SpecialSection.POSITION_RIGHT.getValue()).append(",");
            if (existsCheck.isChecked()) specialSectionBuilder.append(SpecialSection.EXISTS.getValue()).append(",");
            if (specialSectionBuilder.length() > 0) specialSectionBuilder.deleteCharAt(specialSectionBuilder.length()-1);
            //注入给对对象
            currentBridgeParts.setMaterialTypeId(materialType.getId());
            currentBridgeParts.setMaterialTypeName(materialType.getName());
            currentBridgeParts.setHorizontalCount(horizontalCount);
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
        currentBridgeParts = (TblBridgeParts) getIntent().getSerializableExtra(SuperstructureFormActivity.CURRENT_BRIDGE_PARTS);
        if (currentBridgeParts.getPartsTypeName().equals(PartsType.ABUTMENT.getValue())) horizontalCountWrapper.setVisibility(View.VISIBLE);
        if (currentBridgeParts.getPartsTypeName().equals(PartsType.WING_EAR_WALL.getValue()) || currentBridgeParts.getPartsTypeName().equals(PartsType.CONICAL_PROTECTION_SLOP.getValue())) {
            if (currentBridgeParts.getMemberTypeName().equals(MemberType.SLOP_PROTECTION.getValue())) {
                existsWrapper.setVisibility(View.VISIBLE);
            } else {
                positionWrapper.setVisibility(View.VISIBLE);
            }
        }
    }

    /** 设置材料类型选择器 */
    private void setMaterialTypeSelector() {
        materialTypeList = baseDataMapper.getParamTypeListByGroupField(ParamGroupField.MATERIAL_TYPE);
        materialTypeList.add(0, TblParamType.empty());
        ArrayAdapter<TblParamType> adapter = new ArrayAdapter<>(this, R.layout.item_spinner, materialTypeList);
        materialTypeSelector.setAdapter(adapter);
    }

    /** 设置初始值 */
    private void setDefaultValue() {
        if (!StringUtils.isEmpty(currentBridgeParts.getMaterialTypeId())) {
            materialTypeSelector.setSelection(materialTypeList.stream().map(TblParamType::getId).collect(Collectors.toList()).indexOf(currentBridgeParts.getMaterialTypeId()));
        }
        if (currentBridgeParts.getHorizontalCount() != 0) horizontalCountInput.setText(String.valueOf(currentBridgeParts.getHorizontalCount()));
        if (currentBridgeParts.getSpecialSection().contains(SpecialSection.POSITION_LEFT.getValue())) positionLeftCheck.setChecked(true);
        if (currentBridgeParts.getSpecialSection().contains(SpecialSection.POSITION_RIGHT.getValue())) positionLeftCheck.setChecked(true);
        if (currentBridgeParts.getSpecialSection().contains(SpecialSection.EXISTS.getValue())) existsCheck.setChecked(true);
    }

    /** 判断材料类型是否未选 */
    private boolean validate() {
        if (materialTypeSelector.getSelectedItem() == TblParamType.empty()) {
            SystemUtils.prompt(this, "请选择材料类型");
            return false;
        }
        return true;
    }
}
